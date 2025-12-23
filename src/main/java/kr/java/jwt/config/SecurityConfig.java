package kr.java.jwt.config;

import kr.java.jwt.exception.CustomAccessDeniedHandler;
import kr.java.jwt.exception.CustomAuthenticationEntryPoint;
import kr.java.jwt.filter.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

// 1-5
@Configuration
@EnableWebSecurity
@EnableMethodSecurity // step2에서 메서드 기반 추가 테스팅...
@RequiredArgsConstructor // Filter를 등록해주기 위해
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter; // 생성자 주입
    // 2-1-2
    private final CustomAuthenticationEntryPoint authenticationEntryPoint;
    // 2-2-2
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 4-1-2
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // POST, PUT, DELETE 등의 요청은 CSRF 토큰이 없으면 차단 (403 Forbidden)
                // .csrf(csrf -> csrf.disable())
                .csrf(AbstractHttpConfigurer::disable) // 메서드 참조

                // 기본 Security 설정을 풀고 -> token 기반으로 전환
                .sessionManagement(session
                        -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Security가 세션단위로 페이지를 렌더링하면서 제공하던 기능 비활성화
                // .formLogin(form -> form.disable())
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)

                // 2-1-3
                .exceptionHandling(e -> e
                        .authenticationEntryPoint(authenticationEntryPoint)
                        // 2-2-3
                        .accessDeniedHandler(accessDeniedHandler) // 원래는 돌아감
                )

//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().permitAll());
                .authorizeHttpRequests(auth -> auth
                        // 5-3
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                                "/v3/api-docs/**", "/swagger-resources/**").permitAll()

                        // 테스트용 인덱스 페이지
                        .requestMatchers("/").permitAll()
                        // 인증 API
                        // 3-9
//                        .requestMatchers("/api/auth/login").permitAll()
                        .requestMatchers("/api/auth/**").permitAll()
//                        .requestMatchers("/api/auth/login", "/api/auth/refresh").permitAll()
                        // 게시글 조회
                        .requestMatchers(HttpMethod.GET, "/api/boards/**").permitAll()
                        // 나머지는 인증이 필요
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    // cors
    // 4-1-1
    @Bean // (외부에 @Configuration으로 둬도 무방)
    // import org.springframework.web.cors.CorsConfigurationSource;
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowedOriginPatterns(List.of(
                "http://localhost:*", // http 프로토콜 + localhost 도메인 + 모든 포트를 허용
                "http://127.0.0.1:*"
                // 환경설정? -> @Value 주입 후 넣으면 됨
        ));
//        config.setAllowedMethods(List.of("*"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true); // cookie 통한 인증 정보를 받아주겠다
//        config.setMaxAge(3600L); // 1시간

        // import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
//        source.registerCorsConfiguration("/api/**", config);
        return source;
    }
}
