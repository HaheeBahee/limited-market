package com.bookstore.api.domain.auth;

import com.bookstore.api.domain.auth.dto.LoginRequest;
import com.bookstore.api.domain.auth.dto.LoginResponse;
import com.bookstore.api.domain.auth.dto.SignupRequest;
import com.bookstore.api.domain.auth.dto.TokenResponse;
import com.bookstore.api.global.exception.auth.InvalidTokenException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import com.bookstore.api.global.security.CustomUserDetails;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;

@RestController
@RequiredArgsConstructor
@Tag(name = "1. 회원가입/로그인")
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
    @Operation(description = "accessToken을 받아서 Authorize 버튼에 입력하세요")
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

    // Access Token 재발급
    @PostMapping("/reissue")
    public ResponseEntity<LoginResponse> reissue(HttpServletRequest request) {

        // 쿠키에서 Refresh Token 추출
        String refreshToken = extractRefreshTokenFromCookie(request);
        if (refreshToken == null) {
            throw new InvalidTokenException();
        }

        TokenResponse tokenResponse = authService.reissue(refreshToken);

        // 새 Refresh Token 쿠키 설정
        ResponseCookie refreshCookie = ResponseCookie
                .from("refreshToken", tokenResponse.refreshToken())
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(Duration.ofDays(1))
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, refreshCookie.toString())
                .body(new LoginResponse(tokenResponse.accessToken()));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest request, @AuthenticationPrincipal CustomUserDetails userDetails){

        // Authorization 헤더에서 Access Token 추출
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            throw new InvalidTokenException();
        }
        String accessToken = authHeader.substring(7);

        authService.logout(accessToken, userDetails.getMemberId());

        // Refresh Token 쿠키 삭제 (maxAge=0)
        ResponseCookie deleteCookie = ResponseCookie
                .from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .sameSite("Lax")
                .maxAge(0)
                .path("/")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, deleteCookie.toString())
                .build();
    }

    // 쿠키에서 Refresh Token 추출
    private String extractRefreshTokenFromCookie(HttpServletRequest request) {
        if (request.getCookies() == null) return null;

        for (Cookie cookie : request.getCookies()) {
            if ("refreshToken".equals(cookie.getName())) {
                return cookie.getValue();
            }
        }
        return null;
    }
}
