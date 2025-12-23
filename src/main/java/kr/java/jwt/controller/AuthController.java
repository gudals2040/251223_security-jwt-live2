package kr.java.jwt.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.java.jwt.filter.JwtAuthenticationFilter;
import kr.java.jwt.model.dto.LoginRequest;
import kr.java.jwt.model.dto.TokenResponse;
import kr.java.jwt.model.entity.CustomUserDetails;
import kr.java.jwt.service.AuthService;
import kr.java.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

// 1-8-1
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    // 3-8-1
    public static final String REFRESH_TOKEN_COOKIE = "refreshToken";

    // 3-8-2
    private void addCookie(
            HttpServletResponse response,
            String name, // key
            String value, // 문자열
            long maxAge // 초 단위 (밀리초 X)
    ) {
        ResponseCookie cookie = ResponseCookie.from(name, value)
                .httpOnly(true)
                .secure(true) // https. localhost면 괜찮음
                .path("/") // 같은 도메인이면
                .maxAge(maxAge) // 초 단위
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    // 3-8-3
    private void removeCookie(
            HttpServletResponse response,
            String name
    ) {
        ResponseCookie cookie = ResponseCookie.from(name, "")
                .httpOnly(true)
                .secure(true) // https. localhost면 괜찮음
                .path("/") // 같은 도메인이면
                .maxAge(0) // 초 단위
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        TokenResponse tokenResponse = authService.login(request);

        // 3-8-4
        addCookie(response,
                JwtAuthenticationFilter.ACCESS_TOKEN_COOKIE,
                tokenResponse.accessToken(),
                jwtService.getAccessTokenExpirySeconds());
        addCookie(response,
                JwtAuthenticationFilter.ACCESS_TOKEN_COOKIE,
                tokenResponse.refreshToken(),
                jwtService.getRefreshTokenExpirySeconds());

        return ResponseEntity.ok(tokenResponse);
    }

    // 3-8-5
    @PostMapping("/refresh")
    public ResponseEntity<TokenResponse> refresh(
            @CookieValue(name = REFRESH_TOKEN_COOKIE, required = false) String cookieToken,
            // 별도 메서드로 안하고 jwtFilter에서 자동갱신시켜도 됨 (jwt social login 예시에서 자동갱신으로 작성해놓을게요)
            // Cookie가 없는 상황에서도 쓸 예정
            @RequestBody(required = false) Map<String, String> body,
            HttpServletResponse response) {

        String refreshToken = cookieToken; // 없을 수도
        if (refreshToken == null && body != null) { // json body로 갱신할 토큰을 전달한 상황
            refreshToken = body.get("refreshToken");
        }
        if (refreshToken == null) {
            throw new BadCredentialsException("Refresh Token 필요");
        }

        TokenResponse tokenResponse = authService.refresh(refreshToken);

        addCookie(response,
                JwtAuthenticationFilter.ACCESS_TOKEN_COOKIE,
                tokenResponse.accessToken(),
                jwtService.getAccessTokenExpirySeconds());
        addCookie(response,
                JwtAuthenticationFilter.ACCESS_TOKEN_COOKIE,
                tokenResponse.refreshToken(),
                jwtService.getRefreshTokenExpirySeconds());

        return ResponseEntity.ok(tokenResponse);
    }

    // 1-8-3
    // 3-8-6
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(
            HttpServletResponse response,
            @AuthenticationPrincipal CustomUserDetails userDetails) {
        authService.logout(userDetails.getId());

        removeCookie(response, JwtAuthenticationFilter.ACCESS_TOKEN_COOKIE);
        removeCookie(response, REFRESH_TOKEN_COOKIE);

        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }
}
