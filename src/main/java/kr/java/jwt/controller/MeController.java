package kr.java.jwt.controller;

import kr.java.jwt.model.dto.UserInfoResponse;
import kr.java.jwt.model.entity.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/me")
@RequiredArgsConstructor
public class MeController {

    @GetMapping
    public ResponseEntity<UserInfoResponse> getMyInfo(
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        return ResponseEntity.ok(new UserInfoResponse(
                userDetails.getId(),
                userDetails.getEmail(),
                userDetails.getEmail(),
                userDetails.getAuthorities()
                        .stream().findFirst().orElseThrow()
                        .getAuthority())
        );
    }
}
