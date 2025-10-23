package com.worfwint.tabletoprpgmanager.services;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

// TODO: for future - add DeviceID

/**
 *
 * @author michael
 */
@Service
public class TokenCacheService {
    private final RedisTemplate<String, String> redisTemplate;

    public TokenCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // Сохранение токен-версии
    public void saveAccessTokenVersion(Long userId, String tokenVersion, Duration ttl) {
        String key = buildKey(userId);
        redisTemplate.opsForValue().set(key, tokenVersion, ttl);
    }

    // Проверка токен-версии
    public boolean isValidAccessTokenVersion(Long userId, String tokenVersion) {
        String key = buildKey(userId);
        String storedVersion = redisTemplate.opsForValue().get(key);
        return storedVersion != null && storedVersion.equals(tokenVersion);
    }

    // Инвалидация при logout
    public void invalidate(Long userId) {
        redisTemplate.delete(buildKey(userId));
    }

    private String buildKey(Long userId) {
        return "access:user:" + userId;
    }
}
