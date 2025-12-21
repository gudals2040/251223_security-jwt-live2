package kr.java.jwt.model.dto;

import kr.java.jwt.model.entity.Board;

import java.time.Instant;

public record BoardResponse(
        Long id,
        String title,
        String content,
        String authorNickname,
        Long authorId,
        Instant createdAt,
        Instant updatedAt
) {
        public static BoardResponse from(Board board) {
                return new BoardResponse(
                        board.getId(),
                        board.getTitle(),
                        board.getContent(),
                        board.getAuthor().getNickname(),
                        board.getAuthor().getId(),
                        board.getCreatedAt(),
                        board.getUpdatedAt()
                );
        }
}
