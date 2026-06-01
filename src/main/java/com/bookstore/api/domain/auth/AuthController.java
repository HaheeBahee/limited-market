package com.bookstore.api.domain.auth;

import com.bookstore.api.domain.auth.dto.LoginRequest;
import com.bookstore.api.domain.auth.dto.LoginResponse;
import com.bookstore.api.domain.auth.dto.SignupRequest;
import com.bookstore.api.domain.auth.dto.TokenResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    // 회원가입
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody @Valid SignupRequest request) {
        authService.signup(request);
        return ResponseEntity.ok().build();
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {

        // 1. AuthService에서 두 토큰 모두 받아옴
        TokenResponse tokenResponse = authService.login(request);

        // 2. Refresh Token httpOnly 쿠키 설정
        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", tokenResponse.refreshToken())  // 나중에 실제 refreshToken으로 교체
                .httpOnly(true)
                .secure(false)        // 로컬 개발 시 false, 배포 시 true
                .sameSite("Lax")
                .maxAge(Duration.ofDays(1))
                .path("/")
                .build();

        // accessToken만 Body에 담아서 반환
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginResponse(tokenResponse.accessToken()));
    }
}
