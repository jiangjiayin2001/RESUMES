package com.resumestudio.auth;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Duration;
import java.util.HexFormat;

@Service
@RequiredArgsConstructor
@ConditionalOnProperty(name = "app.security.login-attempt-store", havingValue = "redis", matchIfMissing = true)
public class LoginAttemptService implements LoginAttemptStore {

    private final StringRedisTemplate redisTemplate;

    @Value("${app.security.login-max-attempts:5}")
    private int maxAttempts;

    @Value("${app.security.login-lock-minutes:15}")
    private int lockMinutes;

    @Override
    public void assertAllowed(String username, String clientIp) {
        String count = redisTemplate.opsForValue().get(key(username, clientIp));
        if (count != null && Integer.parseInt(count) >= maxAttempts) {
            throw new TooManyLoginAttemptsException("登录尝试次数过多，请稍后再试");
        }
    }

    @Override
    public void recordFailure(String username, String clientIp) {
        String key = key(username, clientIp);
        Long attempts = redisTemplate.opsForValue().increment(key);
        if (attempts != null && attempts == 1L) {
            redisTemplate.expire(key, Duration.ofMinutes(lockMinutes));
        }
    }

    @Override
    public void clear(String username, String clientIp) {
        redisTemplate.delete(key(username, clientIp));
    }

    private String key(String username, String clientIp) {
        return "resume-studio:login-attempt:" + sha256((username == null ? "" : username.toLowerCase())
                + "|" + (clientIp == null ? "" : clientIp));
    }

    private String sha256(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(digest.digest(value.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 is not available", exception);
        }
    }
}
