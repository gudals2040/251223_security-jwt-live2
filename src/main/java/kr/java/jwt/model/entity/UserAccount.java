package kr.java.jwt.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.Instant;
import java.time.LocalDateTime;

@Entity
@Getter @Setter
@NoArgsConstructor
public class UserAccount extends BaseEntity {
    @Column(nullable = false, unique = true)
    private String email;  // 로그인 ID로 사용

    @Column(nullable = false)
    private String password;  // BCrypt 암호화된 비밀번호

    @Column(nullable = false)
    private String nickname;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;  // USER 또는 ADMIN

    // 권한을 나타내는 열거형
    public enum Role {
        USER, ADMIN
    }

    // 정적 팩토리 메서드: 새 사용자 생성용
    public static UserAccount create(String email, String encodedPassword, String nickname, Role role) {
        UserAccount user = new UserAccount();
        user.email = email;
        user.password = encodedPassword;
        user.nickname = nickname;
        user.role = role;
        return user;
    }
}
