package kr.java.jwt.controller;

import jakarta.validation.Valid;
import kr.java.jwt.model.dto.BoardRequest;
import kr.java.jwt.model.dto.BoardResponse;
import kr.java.jwt.model.entity.CustomUserDetails;
import kr.java.jwt.service.BoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    // 1-9
    @PostMapping
    public ResponseEntity<BoardResponse> create(
            @Valid @RequestBody BoardRequest request,
//            @RequestParam Long authorId
            @AuthenticationPrincipal CustomUserDetails userDetails // <- JwtAuthenticationFilter
            ) {
//        BoardResponse response = boardService.create(request, authorId);
        BoardResponse response = boardService.create(request, userDetails.getId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<BoardResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody BoardRequest request,
//            @RequestParam Long userId,
//            @RequestParam(defaultValue = "false") boolean isAdmin
            @AuthenticationPrincipal CustomUserDetails userDetails
    ) {
//        return ResponseEntity.ok(boardService.update(id, request, userId, isAdmin));
        boolean isAdmin = hasAdminRole(userDetails);
        return ResponseEntity.ok(boardService.update(id, request,
                userDetails.getId(), isAdmin));
    }

    private boolean hasAdminRole(CustomUserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a ->
                        a.getAuthority().equals("ROLE_ADMIN"));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
//            @RequestParam Long userId,
//            @RequestParam(defaultValue = "false") boolean isAdmin
            @AuthenticationPrincipal CustomUserDetails customUserDetails
    ) {
//        boardService.delete(id, userId, isAdmin);
        boardService.delete(id, customUserDetails.getId(), hasAdminRole(customUserDetails));
        return ResponseEntity.noContent().build();
    }
}
