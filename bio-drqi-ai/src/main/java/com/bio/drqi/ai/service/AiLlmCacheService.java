package com.bio.drqi.ai.service;

import java.time.Duration;

public interface AiLlmCacheService {

    <T> T get(String namespace, String rawKey, Class<T> type);

    void set(String namespace, String rawKey, Object value, Duration ttl);
}
