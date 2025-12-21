package kr.java.jwt.model.dto;

public record UserInfoResponse(
        Long id,
        String email,
        String nickname,
        String role
) {
}
