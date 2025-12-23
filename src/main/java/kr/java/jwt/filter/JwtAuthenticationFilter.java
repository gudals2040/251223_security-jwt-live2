package kr.java.jwt.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kr.java.jwt.service.CustomUserDetailsService;
import kr.java.jwt.service.JwtService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

// 1-4
@Component
@RequiredArgsConstructor
@Slf4j
// JwtFilter, JwtAuthorizationFilter ... (이름은...)
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 1. 토큰 추출
        String token = extractToken(request);

        // 2. 토큰 검증 -> 인증 처리
        if (token != null && jwtService.validateToken(token)) {
            try {
                Long userId = jwtService.getUserIdFromToken(token); // token -> userId
                UserDetails userDetails = userDetailsService.loadUserById(userId); // DB - User

                // UserDetailsService <- Spring Security
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails, null, userDetails.getAuthorities()
                        );

                authentication.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authentication);
                log.debug("인증 성공: userId={}", userId);
            } catch (Exception e) {
                log.warn("인증 실패: {}", e.getMessage());
                SecurityContextHolder.clearContext();
                // 실패했기 때문에 직전에 혹시 남아있던 인증 정보가 있다면 확실히 삭제
            }
        }

        filterChain.doFilter(request, response); // 이거 안하면 다음으로 안넘어감
    }

    public static final String ACCESS_TOKEN_COOKIE = "accessToken";

    // 토큰 추출
    // -> cookie.
    // -> header -> Authorization - Bearer
    private String extractToken(HttpServletRequest request) {
        // import jakarta.servlet.http.Cookie;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (ACCESS_TOKEN_COOKIE.equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }
}
