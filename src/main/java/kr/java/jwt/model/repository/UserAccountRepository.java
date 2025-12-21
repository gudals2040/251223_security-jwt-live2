package kr.java.jwt.model.repository;

import kr.java.jwt.model.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {

    // 이메일로 사용자 조회 (로그인, 중복 검사에 사용)
    Optional<UserAccount> findByEmail(String email);

    // 이메일 존재 여부 확인
    boolean existsByEmail(String email);
}