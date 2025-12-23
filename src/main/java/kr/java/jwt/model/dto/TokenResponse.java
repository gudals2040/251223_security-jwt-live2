package kr.java.jwt.model.dto;

// 1-6
public record TokenResponse(
        String accessToken,
        // 3-6
        String refreshToken,
        String tokenType,
        Long expiresIn
) {
}
