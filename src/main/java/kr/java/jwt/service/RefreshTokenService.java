package kr.java.jwt.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

// 3-5
@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RedisTemplate<String, String> redisTemplate; // Redis -> Repository
    private final JwtService jwtService;

    private static final String PREFIX = "refresh:"; // 레디스 데이터 구분 접두사

    // 4개
    // 1. 저장 2. 불러오기 3. 삭제 4. 교체
    public void save(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set(
                PREFIX + userId, // key
                refreshToken, // value
                jwtService.getRefreshTokenExpiryMillis() // 얼마나 유지시킬지 (없으면 영구보관)
                // Time To Live
        );
    }

    public String get(Long userId) {
        return redisTemplate.opsForValue().get(PREFIX + userId);
    }

    public void delete(Long userId) {
        redisTemplate.delete(PREFIX + userId);
    }

    public void rotate(Long userId, String newToken) {
        delete(userId);
        save(userId, newToken);
    }

    public boolean validate(Long userId, String refreshToken) {
        String stored = get(userId); // 레디스에 저장되어있는 refreshToken
        return stored != null && stored.equals(refreshToken)
                && jwtService.validateToken(refreshToken);
    }
}
