package kr.java.jwt.controller;

import jakarta.validation.Valid;
import kr.java.jwt.model.dto.BoardRequest;
import kr.java.jwt.model.dto.BoardResponse;
import kr.java.jwt.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boards")
@RequiredArgsConstructor
public class BoardController {

    private final BoardService boardService;

    @GetMapping
    public ResponseEntity<List<BoardResponse>> findAll() {
        return ResponseEntity.ok(boardService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<BoardResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(boardService.findById(id));
    }

    // 임시: authorId를 파라미터로 받음 (Security 적용 후 제거)
    @PostMapping
    public ResponseEntity<BoardResponse> create(
            @Valid @RequestBody BoardRequest request,
            @RequestParam Long authorId) {
        BoardResponse response = boardService.create(request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // 임시: userId를 파라미터로 받음
    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody BoardRequest request,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "false") boolean isAdmin) {
        return ResponseEntity.ok(boardService.update(id, request, userId, isAdmin));
    }

    // 임시: userId를 파라미터로 받음
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @RequestParam Long userId,
            @RequestParam(defaultValue = "false") boolean isAdmin) {
        boardService.delete(id, userId, isAdmin);
        return ResponseEntity.noContent().build();
    }
}
