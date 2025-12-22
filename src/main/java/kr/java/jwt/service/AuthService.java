package kr.java.jwt.service;

import kr.java.jwt.model.dto.LoginRequest;
import kr.java.jwt.model.dto.TokenResponse;
import kr.java.jwt.model.entity.UserAccount;
import kr.java.jwt.model.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// 1-7
@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {
    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    // import org.springframework.transaction.annotation.Transactional;
    @Transactional(readOnly = true) // login
    public TokenResponse login(LoginRequest request) {
        // 사용자 조회
        UserAccount user = userAccountRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("이메일 또는 비밀번호 오류"));

        // 비밀번호 검증
        if (!passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new BadCredentialsException("이메일 또는 비밀번호 오류");
        }

        // AccessToken 생성
        String accessToken = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name()
        );

        log.info("로그인 성공: {}", user.getEmail());

        return new TokenResponse(
                accessToken,
                "Bearer",
                jwtService.getAccessTokenExpirySeconds()
        );
    }
}
