package com.worfwint.tabletoprpgmanager.auth.service;

import java.time.Duration;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * Manages short-lived token metadata stored in Redis.
 */
@Service
public class TokenCacheService {

    private final RedisTemplate<String, String> redisTemplate;

    /**
     * Creates a new service with the provided Redis template.
     *
     * @param redisTemplate template used for interacting with Redis
     */
    public TokenCacheService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * Stores the version of the access token for the given user.
     *
     * @param userId identifier of the user
     * @param tokenVersion version identifier associated with the token
     * @param ttl time to live for the cached value
     */
    public void saveAccessTokenVersion(Long userId, String tokenVersion, Duration ttl) {
        String key = buildKey(userId);
        redisTemplate.opsForValue().set(key, tokenVersion, ttl);
    }

    /**
     * Checks if the provided token version matches the cached version.
     *
     * @param userId identifier of the user
     * @param tokenVersion version identifier to validate
     * @return {@code true} if the cached version matches the provided version
     */
    public boolean isValidAccessTokenVersion(Long userId, String tokenVersion) {
        String key = buildKey(userId);
        String storedVersion = redisTemplate.opsForValue().get(key);
        return storedVersion != null && storedVersion.equals(tokenVersion);
    }

    /**
     * Removes the cached token version for the given user, invalidating tokens.
     *
     * @param userId identifier of the user
     */
    public void invalidate(Long userId) {
        redisTemplate.delete(buildKey(userId));
    }

    /**
     * Builds the Redis key for storing a user's token version.
     *
     * @param userId identifier of the user
     * @return formatted Redis key
     */
    private String buildKey(Long userId) {
        return "access:user:" + userId;
    }
}
