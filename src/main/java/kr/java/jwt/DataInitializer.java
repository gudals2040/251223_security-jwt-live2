package kr.java.jwt;

import kr.java.jwt.model.entity.Board;
import kr.java.jwt.model.entity.UserAccount;
import kr.java.jwt.model.repository.BoardRepository;
import kr.java.jwt.model.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserAccountRepository userAccountRepository;
    private final BoardRepository boardRepository;

    // DROP TABLE IF EXISTS memo;

    // import org.springframework.transaction.annotation.Transactional;
    @Override
    @Transactional
    public void run(String... args) {
        if (userAccountRepository.count() > 0) {
            log.info("데이터가 이미 존재합니다.");
            return;
        }

        log.info("테스트 데이터 초기화...");

        // BCrypt 해시 (Security 적용 전이므로 직접 해시값 입력)
        // "admin123" -> $2a$10$...
        // "user123" -> $2a$10$...
        // 실제로는 PasswordEncoder 사용, 여기서는 임시로 평문 저장
        UserAccount admin = UserAccount.create(
                "admin@example.com",
                "admin123",  // Security 적용 시 인코딩된 값으로 교체
                "관리자",
                UserAccount.Role.ADMIN
        );

        UserAccount user = UserAccount.create(
                "user@example.com",
                "user123",
                "일반사용자",
                UserAccount.Role.USER
        );

        userAccountRepository.saveAll(List.of(admin, user));

        Board board1 = Board.create("공지사항", "관리자 공지입니다.", admin);
        Board board2 = Board.create("첫 게시글", "일반 사용자 글입니다.", user);
        Board board3 = Board.create("Spring Security 학습", "JWT 인증 학습중!", user);

        boardRepository.saveAll(List.of(board1, board2, board3));

        log.info("초기화 완료!");
        log.info("admin@example.com / admin123 (ID: {})", admin.getId());
        log.info("user@example.com / user123 (ID: {})", user.getId());
    }
}
