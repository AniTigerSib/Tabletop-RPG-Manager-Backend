package com.worfwint.tabletoprpgmanager.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Defines Redis beans used by the application for token caching and other fast lookups.
 */
@Configuration
public class RedisConfig {

    /**
     * Creates a Redis connection factory pointing to the configured host and port.
     *
     * @param host Redis host name, defaults to {@code localhost}
     * @param port Redis port, defaults to {@code 6379}
     * @param username optional username for Redis authentication
     * @param password optional password for Redis authentication
     * @return a fully configured {@link RedisConnectionFactory}
     */
    @Bean
    public RedisConnectionFactory redisConnectionFactory(
            @Value("${spring.data.redis.host:localhost}") String host,
            @Value("${spring.data.redis.port:6379}") int port,
            @Value("${spring.data.redis.username:}") String username,
            @Value("${spring.data.redis.password:}") String password) {

        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration(host, port);
        if (!username.isBlank()) {
            config.setUsername(username);
        }
        if (!password.isBlank()) {
            config.setPassword(password);
        }
        return new LettuceConnectionFactory(config);
    }

    /**
     * Configures a {@link RedisTemplate} for simple String based key-value operations.
     *
     * @param connectionFactory the Redis connection factory to use
     * @return a pre-configured {@link RedisTemplate} instance
     */
    @Bean
    public RedisTemplate<String, String> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // serialize keys and values as plain strings for predictable interoperability
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());

        template.afterPropertiesSet();
        return template;
    }
}
