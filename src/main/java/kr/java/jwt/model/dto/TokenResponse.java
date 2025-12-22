package kr.java.jwt.model.dto;

// 1-6
public record TokenResponse(
        String accessToken,
        String tokenType,
        Long expiresIn
) {
}
