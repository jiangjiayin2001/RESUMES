package com.resumestudio.auth;

public interface LoginAttemptStore {
    void assertAllowed(String username, String clientIp);
    void recordFailure(String username, String clientIp);
    void clear(String username, String clientIp);
}
