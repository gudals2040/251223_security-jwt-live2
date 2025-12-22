package kr.java.jwt.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
// @Table(name = "user_accounts") // DROP 실패로 인한 이슈 1. 이름 바꾸기
// 2. user_account를 참고하고 있는 해당 테이블을 DROP
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