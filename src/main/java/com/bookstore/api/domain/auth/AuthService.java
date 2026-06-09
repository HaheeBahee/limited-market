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
import com.bookstore.api.global.jwt.JwtProperties;
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
    private final JwtProperties jwtProperties;
    private final RedisService redisService;

    @Transactional
    public void signup(SignupRequest request) {

        if (memberRepository.existsByEmail(request.email())) {
            throw new DuplicateEmailException();
        }

        String encodedPassword = passwordEncoder.encode(request.password());

        Member member = Member.create(
                request.email(),
                encodedPassword,
                request.name()
        );

        memberRepository.save(member);
    }

    @Transactional(readOnly = true)
    public TokenResponse login(LoginRequest request) {

        Member member = memberRepository.findByEmail(request.email())
                .orElseThrow(MemberNotFoundException::new);

        if (!passwordEncoder.matches(request.password(), member.getPassword())) {
            throw new MemberNotFoundException();
        }

        if (member.getMemberStatus() == MemberStatus.WITHDRAWN) {
            throw new MemberNotFoundException();
        }

        String accessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String refreshToken = jwtProvider.createRefreshToken(member.getId());

        redisService.saveRefreshToken(member.getId(), refreshToken, jwtProperties.refreshTokenExpiration(), TimeUnit.MILLISECONDS);

        return new TokenResponse(accessToken, refreshToken);
    }

    public TokenResponse reissue(String refreshToken) {

        if (!jwtProvider.validateToken(refreshToken)) {
            throw new InvalidTokenException();
        }

        Long memberId = jwtProvider.getMemberId(refreshToken);

        String storedRefreshToken = redisService.getRefreshToken(memberId);
        if (storedRefreshToken == null || !storedRefreshToken.equals(refreshToken)) {
            throw new InvalidTokenException();
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(MemberNotFoundException::new);

        String newAccessToken = jwtProvider.createAccessToken(member.getId(), member.getRole());
        String newRefreshToken = jwtProvider.createRefreshToken(member.getId());
        redisService.saveRefreshToken(member.getId(), newRefreshToken, jwtProperties.refreshTokenExpiration(), TimeUnit.MILLISECONDS);

        return new TokenResponse(newAccessToken, newRefreshToken);
    }

    public void logout(String accessToken, Long memberId) {
        long remainingExpiration = jwtProvider.getRemainingExpiration(accessToken);
        redisService.addToBlacklist(accessToken, remainingExpiration);
        redisService.deleteRefreshToken(memberId);
    }
}
