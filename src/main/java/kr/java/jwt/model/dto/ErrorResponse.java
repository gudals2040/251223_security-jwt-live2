package kr.java.jwt.model.dto;

import java.time.Instant;

public record ErrorResponse(
        int status,           // HTTP 상태 코드
        String error,         // 에러 유형
        String message,       // 상세 메시지
        Instant timestamp

) {
        public static ErrorResponse of(int status, String error, String message) {
                return new ErrorResponse(
                        status,
                        error,
                        message,
                        Instant.now()
                );
        }
}
