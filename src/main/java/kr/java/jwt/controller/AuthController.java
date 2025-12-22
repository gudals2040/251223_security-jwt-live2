package kr.java.jwt.controller;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import kr.java.jwt.filter.JwtAuthenticationFilter;
import kr.java.jwt.model.dto.LoginRequest;
import kr.java.jwt.model.dto.TokenResponse;
import kr.java.jwt.service.AuthService;
import kr.java.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

// 1-8-1
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtService jwtService;

    @PostMapping("/login")
    public ResponseEntity<TokenResponse> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletResponse response) {

        TokenResponse tokenResponse = authService.login(request);

        // 보안 처리 -> Cookie
        ResponseCookie cookie = ResponseCookie
                .from(JwtAuthenticationFilter.ACCESS_TOKEN_COOKIE,
                        // Cookie 값
                        tokenResponse.accessToken())
                .httpOnly(true) // JS 로 읽어들일 수 없는 쿠키 -> XSS
                .secure(false) // true여야함 (https domain)
                .path("/")
                .maxAge(jwtService.getAccessTokenExpirySeconds())
                .sameSite("Lax")
                // 같은 도메인에서 사용 가능 but a tag, link tag 같이 유저의 액션을 통해서 이동했을때만 cookie 전송됨
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

        return ResponseEntity.ok(tokenResponse);
    }

    // 1-8-3
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout(HttpServletResponse response) {
        // 쿠키 삭제
        ResponseCookie cookie = ResponseCookie
                .from(JwtAuthenticationFilter.ACCESS_TOKEN_COOKIE, "") // Cookie Name
                .httpOnly(true)
                .secure(false)
                .path("/")
                .maxAge(0) // 삭제
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok(Map.of("message", "로그아웃 완료"));
    }
}
