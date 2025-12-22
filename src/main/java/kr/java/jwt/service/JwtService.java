package kr.java.jwt.service;

import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

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
}
