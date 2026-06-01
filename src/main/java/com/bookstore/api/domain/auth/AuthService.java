package com.bookstore.api.domain.auth;

import com.bookstore.api.domain.auth.dto.LoginRequest;
import com.bookstore.api.domain.auth.dto.SignupRequest;
import com.bookstore.api.domain.auth.dto.TokenResponse;
import com.bookstore.api.domain.member.Member;
import com.bookstore.api.domain.member.MemberRepository;
import com.bookstore.api.domain.member.MemberStatus;
import com.bookstore.api.global.exception.member.DuplicateEmailException;
import com.bookstore.api.global.exception.member.MemberNotFoundException;
import com.bookstore.api.global.jwt.JwtProvider;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

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
            throw new IllegalArgumentException("비밀번호가 올바르지 않습니다");
        }

        // 3. 탈퇴 회원 확인
        if (member.getMemberStatus() == MemberStatus.WITHDRAWN) {
            throw new IllegalArgumentException("탈퇴한 회원입니다");
        }

        // 4. Access Token 생성
        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());

        // 5. Refresh Token 생성 (Redis 저장은 다음 단계에서)
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        return new TokenResponse(accessToken, refreshToken);
        // refreshToken은 Controller에서 httpOnly 쿠키로 설정
    }
}
