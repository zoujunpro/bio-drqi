package com.bio.drqi.ai.service.impl;

import cn.hutool.crypto.digest.DigestUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.service.AiLlmCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.Duration;

@Service
@Slf4j
public class RedisAiLlmCacheServiceImpl implements AiLlmCacheService {

    private static final String CACHE_PREFIX = "bio:ai:llm:";

    @Resource
    private ObjectProvider<StringRedisTemplate> stringRedisTemplateProvider;

    @Resource
    private AiProperties aiProperties;

    @Override
    public <T> T get(String namespace, String rawKey, Class<T> type) {
        if (!isEnabled()) {
            return null;
        }
        StringRedisTemplate redisTemplate = stringRedisTemplateProvider.getIfAvailable();
        if (redisTemplate == null) {
            return null;
        }
        String cacheKey = buildKey(namespace, rawKey);
        try {
            String value = redisTemplate.opsForValue().get(cacheKey);
            if (value == null) {
                return null;
            }
            log.info("AI LLM缓存命中，namespace={}，key={}", namespace, cacheKey);
            return JSONUtil.toBean(value, type);
        } catch (Exception e) {
            log.warn("AI LLM缓存读取失败，namespace={}，key={}", namespace, cacheKey, e);
            return null;
        }
    }

    @Override
    public void set(String namespace, String rawKey, Object value, Duration ttl) {
        if (!isEnabled() || value == null || ttl == null || ttl.isZero() || ttl.isNegative()) {
            return;
        }
        StringRedisTemplate redisTemplate = stringRedisTemplateProvider.getIfAvailable();
        if (redisTemplate == null) {
            return;
        }
        String cacheKey = buildKey(namespace, rawKey);
        try {
            redisTemplate.opsForValue().set(cacheKey, JSONUtil.toJsonStr(value), ttl);
        } catch (Exception e) {
            log.warn("AI LLM缓存写入失败，namespace={}，key={}", namespace, cacheKey, e);
        }
    }

    private boolean isEnabled() {
        return aiProperties.getCache() != null && Boolean.TRUE.equals(aiProperties.getCache().getEnabled());
    }

    private String buildKey(String namespace, String rawKey) {
        return CACHE_PREFIX + namespace + ":" + DigestUtil.sha256Hex(rawKey == null ? "" : rawKey);
    }
}
