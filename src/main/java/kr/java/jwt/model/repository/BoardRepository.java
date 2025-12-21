package kr.java.jwt.model.repository;

import kr.java.jwt.model.entity.Board;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {

    // 작성자 정보를 함께 조회 (N+1 문제 방지)
    @Query("SELECT b FROM Board b JOIN FETCH b.author ORDER BY b.createdAt DESC")
    List<Board> findAllWithAuthor();

    // 단건 조회 시에도 작성자 정보 함께 로드
    @Query("SELECT b FROM Board b JOIN FETCH b.author WHERE b.id = :id")
    Optional<Board> findByIdWithAuthor(@Param("id") Long id);
}