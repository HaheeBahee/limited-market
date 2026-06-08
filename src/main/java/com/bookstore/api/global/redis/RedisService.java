package com.bookstore.api.global.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    // 값 저장
    public void set(String key, String value, long duration, TimeUnit timeUnit) {
        redisTemplate.opsForValue().set(key, value, duration, timeUnit);
    }

    // 값 조회
    public String get(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    // 키 삭제
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    // 키 존재 여부 확인
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    // Refresh Token 전용 메서드

    // Refresh Token 저장
    public void saveRefreshToken(Long memberId, String refreshToken, long duration, TimeUnit timeUnit) {
        set("refresh:" + memberId, refreshToken, duration, timeUnit);
    }

    // Refresh Token 조회
    public String getRefreshToken(Long memberId) {
        return get("refresh:" + memberId);
    }

    // Refresh Token 삭제 (로그아웃 시)
    public void deleteRefreshToken(Long memberId) {
        delete("refresh:" + memberId);
    }

    // 블랙리스트 전용 메서드

    // Access Token 블랙리스트 등록 (로그아웃 시)
    public void addToBlacklist(String accessToken, long remainingExpiration) {
        set("blacklist:" + accessToken, "logout", remainingExpiration, TimeUnit.MILLISECONDS);
    }

    // 블랙리스트 여부 확인
    public boolean isBlacklisted(String accessToken) {
        return exists("blacklist:" + accessToken);
    }
}
