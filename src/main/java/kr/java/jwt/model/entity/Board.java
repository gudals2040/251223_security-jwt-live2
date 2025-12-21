package kr.java.jwt.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
public class Board extends BaseEntity {
    @Column(nullable = false)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String content;

    // 작성자와의 다대일 관계
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private UserAccount author;

    public static Board create(String title, String content, UserAccount author) {
        Board board = new Board();
        board.title = title;
        board.content = content;
        board.author = author;
        return board;
    }

    // 수정 메서드: 작성자만 호출 가능하도록 서비스에서 검증
    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }
}