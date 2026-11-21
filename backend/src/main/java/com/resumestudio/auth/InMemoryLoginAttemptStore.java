package com.resumestudio.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@ConditionalOnProperty(name = "app.security.login-attempt-store", havingValue = "memory")
public class InMemoryLoginAttemptStore implements LoginAttemptStore {

    private final Map<String, Attempt> attempts = new ConcurrentHashMap<>();

    @Value("${app.security.login-max-attempts:5}")
    private int maxAttempts;

    @Value("${app.security.login-lock-minutes:15}")
    private int lockMinutes;

    @Override
    public void assertAllowed(String username, String clientIp) {
        String key = key(username, clientIp);
        Attempt attempt = attempts.get(key);
        if (attempt == null) {
            return;
        }
        if (attempt.expiresAt().isBefore(Instant.now())) {
            attempts.remove(key, attempt);
            return;
        }
        if (attempt.count() >= maxAttempts) {
            throw new TooManyLoginAttemptsException("登录尝试次数过多，请稍后再试");
        }
    }

    @Override
    public void recordFailure(String username, String clientIp) {
        String key = key(username, clientIp);
        attempts.compute(key, (ignored, current) -> {
            if (current == null || current.expiresAt().isBefore(Instant.now())) {
                return new Attempt(1, Instant.now().plusSeconds(lockMinutes * 60L));
            }
            return new Attempt(current.count() + 1, current.expiresAt());
        });
    }

    @Override
    public void clear(String username, String clientIp) {
        attempts.remove(key(username, clientIp));
    }

    private String key(String username, String clientIp) {
        return (username == null ? "" : username.toLowerCase()) + "|" + (clientIp == null ? "" : clientIp);
    }

    private record Attempt(int count, Instant expiresAt) {
    }
}
