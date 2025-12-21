package kr.java.jwt.service;

import jakarta.persistence.EntityNotFoundException;
import kr.java.jwt.model.dto.BoardRequest;
import kr.java.jwt.model.dto.BoardResponse;
import kr.java.jwt.model.entity.Board;
import kr.java.jwt.model.entity.UserAccount;
import kr.java.jwt.model.repository.BoardRepository;
import kr.java.jwt.model.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
// import org.springframework.transaction.annotation.Transactional;
@Transactional(readOnly = true)
public class BoardService {

    private final BoardRepository boardRepository;
    private final UserAccountRepository userAccountRepository;

    // 전체 조회
    public List<BoardResponse> findAll() {
        return boardRepository.findAllWithAuthor().stream()
                .map(BoardResponse::from)
                .collect(Collectors.toList());
    }

    // 단건 조회
    public BoardResponse findById(Long id) {
        Board board = boardRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: ID=" + id));
        return BoardResponse.from(board);
    }

    // 작성 (authorId는 나중에 인증에서 받아옴, 지금은 파라미터로)
    @Transactional
    public BoardResponse create(BoardRequest request, Long authorId) {
        UserAccount author = userAccountRepository.findById(authorId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        Board board = Board.create(request.title(), request.content(), author);
        Board saved = boardRepository.save(board);
        return BoardResponse.from(saved);
    }

    // 수정
    @Transactional
    public BoardResponse update(Long id, BoardRequest request, Long userId, boolean isAdmin) {
        Board board = boardRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: ID=" + id));

        // 소유권 검증
        if (!board.getAuthor().getId().equals(userId) && !isAdmin) {
            // import org.springframework.security.access.AccessDeniedException;
            throw new AccessDeniedException("수정 권한이 없습니다.");
        }

        board.update(request.title(), request.content());
        return BoardResponse.from(board);
    }

    // 삭제
    @Transactional
    public void delete(Long id, Long userId, boolean isAdmin) {
        Board board = boardRepository.findByIdWithAuthor(id)
                .orElseThrow(() -> new EntityNotFoundException("게시글을 찾을 수 없습니다: ID=" + id));

        if (!board.getAuthor().getId().equals(userId) && !isAdmin) {
            throw new AccessDeniedException("삭제 권한이 없습니다.");
        }

        boardRepository.delete(board);
    }
}
