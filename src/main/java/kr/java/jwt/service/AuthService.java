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
    // 3-7-1
    private final RefreshTokenService refreshTokenService;
    // -> RefreshTokenRepository, Entity 등을 구현해서 대체 가능

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
        // 3-7-2
        // RefreshToken 생성
        String refreshToken = jwtService.generateRefreshToken(user.getId());
        refreshTokenService.save(user.getId(), refreshToken);

        log.info("로그인 성공: {}", user.getEmail());

        return new TokenResponse(
                accessToken,
                // 3-7-3
                refreshToken,
                "Bearer",
                jwtService.getAccessTokenExpirySeconds()
        );
    }

    // 3-7-4
    @Transactional(readOnly = true)
    public TokenResponse refresh(String refreshToken) {
        if (!jwtService.validateToken(refreshToken)) {
            // 문제가 있으면
            throw new BadCredentialsException("유효하지 않은 Refresh Token");
            // 토큰 검증/데이터 추출은 같은 알고리즘과 같은 비밀키를 쓴다면 공유
        }

        Long userId = jwtService.getUserIdFromToken(refreshToken);

        if (!refreshTokenService.validate(userId, refreshToken)) {
            throw new BadCredentialsException("Refresh Token 불일치 또는 만료");
        }

        UserAccount user = userAccountRepository.findById(userId)
                .orElseThrow(() -> new BadCredentialsException("사용자 없음"));

        // Refresh Token 갱신 + Access Token도 같이 갱신
        String newAccessToken = jwtService.generateAccessToken(
                user.getId(), user.getEmail(), user.getRole().name()
        );
        String newRefreshToken = jwtService.generateRefreshToken(user.getId());

        refreshTokenService.rotate(userId, newRefreshToken);

        log.info("토큰 갱신: userId={}", userId);

        return new TokenResponse(
                newAccessToken,
                newRefreshToken,
                "Bearer",
                jwtService.getAccessTokenExpirySeconds()
        );
    }
}
