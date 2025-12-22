package kr.java.jwt.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

// 1-3
@Service
@Slf4j // log (sout과 유사)
public class JwtService {

    private final SecretKey secretKey; // 비밀키 (클래스-객체)
    private final long accessTokenExpiry; // long

    public JwtService(
            // import org.springframework.beans.factory.annotation.Value;
            @Value("${jwt.secret}") String secret,
            @Value("${jwt.access-token-expiry}") long accessTokenExpiry
    ) {
        this.accessTokenExpiry = accessTokenExpiry; // long <- string
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    // 토큰 생성
    public String generateAccessToken(Long userId, String email, String role) {
        // import java.util.Date;
        Date now = new Date();
        return Jwts.builder()
                .subject(String.valueOf(userId)) // 토큰 주인에 대한 정보
                // 토큰에 포함하고 싶은 정보 claim
                .claim("email", email)
                .claim("role", role)
                // 발행 기준
                .issuedAt(now) // 발행된 시간
                .expiration(new Date(now.getTime() + accessTokenExpiry)) // 15분 후 만료되는 시간
                .id(UUID.randomUUID().toString()) // 식별
                .signWith(secretKey) // 변환
                .compact(); // 토큰화
    }

    // 이 토큰의 값은 무엇인가? (해석, 파싱)
    public Claims parseToken(String token) {
        return Jwts.parser()
                .verifyWith(secretKey) // secretKey 기준으로 해석할 수 없을 시 에러가 발생
                // 1) 형식을 지키지 않은 토큰 (잘못된 코드)
                // 2) 만료된 토큰
                .build()
                .parseSignedClaims(token) // 토큰을 파서에 집어넣어서
                .getPayload(); // Claims(Payload)를 추출
    }

    // 이 토큰이 제대로된 형태의 토큰인가? (검증)
    public boolean validateToken(String token) {
        try {
            parseToken(token); // 파싱할 수 없거나 만료되면 Exception
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료");
//        } catch (JwtException | IllegalArgumentException e) {
        } catch (Exception e) {
            log.warn("토큰 검증 실패 : {}", e.getMessage());
        }
        return false;
    }

    // Token -> subject/claim -> ...
    // getUserIdFromToken
    public Long getUserIdForToken(String token) {
        return Long.parseLong(
                parseToken(token)
                        .getSubject());
    }

    // 밀리초 -> 초로 나타내기 -> Cookie 만료 시간 == 토큰 만료 시간 일치시키기 위함
    public long getAccessTokenExpirySeconds() {
        return accessTokenExpiry / 1000;
    }
}
