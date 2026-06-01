package com.bookstore.api.domain.auth;

import com.bookstore.api.domain.auth.dto.LoginRequest;
import com.bookstore.api.domain.auth.dto.SignupRequest;
import com.bookstore.api.domain.auth.dto.TokenResponse;
import com.bookstore.api.domain.member.Member;
import com.bookstore.api.domain.member.MemberRepository;
import com.bookstore.api.domain.member.MemberStatus;
import com.bookstore.api.global.exception.auth.InvalidTokenException;
import com.bookstore.api.global.exception.member.DuplicateEmailException;
import com.bookstore.api.global.exception.member.MemberNotFoundException;
import com.bookstore.api.global.jwt.JwtProvider;
import com.bookstore.api.global.redis.RedisService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    // 회원가입
    @Transactional
    public void signup(SignupRequest request) {

        // 1. 이메일 중복 확인
        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        // 2. 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.password());

        // 3. Member 엔티티 생성
        Member member = Member.create(
                request.email(),
                encodedPassword,
                request.name()
        );

        // 4. DB 저장
        memberRepository.save(member);
    }

    // 로그인
    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {

        // 1. 이메일로 회원 조회
        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(MemberNotFoundException::new);

        // 2. 비밀번호 검증 - 이것도 나중에 custom 예외처리하나?
        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new MemberNotFoundException();
        }

        // 3. 탈퇴 회원 확인
        if (member.getMemberStatus() == MemberStatus.WITHDRAWN) {
            throw new MemberNotFoundException();
        }

        // 4. Access Token 생성
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());

        // 5. Refresh Token 생성
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        // 6. Refresh Token Redis에 저장
        redisService.saveRefreshToken(member.getId(), refreshToken, 1, TimeUnit.DAYS);

        return new TokenResponse(accessToken, refreshToken);
        // refreshToken은 Controller에서 httpOnly 쿠키로 설정
    }

    // Access Token 재발급
    public TokenResponse reissue(String refreshToken) {

        // 1. Refresh Token 유효성 검증
        if (!jwtProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        // 2. Refresh Token 에서 memberId 추출
        Long memberId = jwtProvider.getMemberId(refreshToken);

        // 3. Redis 에 저장된 Refresh Token과 비교
        String storedRefreshToken = redisService.getRefreshToken(memberId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new InvalidTokenException();
        }

        // 4.  회원 조회 - accessToken 발급 위해선 memberId, role 둘 다 필요해서
        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        // 5. 새 Access Token 발급
        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());

        // 6. 새 Refresh Token 발급, 기존 Refresh Token을 새것으로 교체
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId());
        redisService.saveRefreshToken(member.getId(), newRefreshToken, 3, TimeUnit.DAYS);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    // 로그아웃
    public void logout(String accessToken, Long memberId) {

        // 1. Access Token 블랙리스트 등록
        long remainingExpiration = jwtProvider.getRemainingExpiration(accessToken);
        redisService.addToBlacklist(accessToken, remainingExpiration);

        // 2. Refresh Token 삭제
        redisService.deleteRefreshToken(memberId);
    }
}
