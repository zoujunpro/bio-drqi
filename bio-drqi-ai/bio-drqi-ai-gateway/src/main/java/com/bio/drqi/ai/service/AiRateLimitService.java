package com.bio.drqi.ai.service;

import cn.hutool.core.util.StrUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.config.AiProperties;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AiRateLimitService {

    @Resource
    private AiProperties aiProperties;

    private final AtomicInteger concurrentRequests = new AtomicInteger();

    private final Map<String, WindowCounter> clientCounters = new ConcurrentHashMap<>();

    public void acquire(String clientKey) {
        AiProperties.RateLimit rateLimit = aiProperties.getRateLimit();
        if (rateLimit == null || !Boolean.TRUE.equals(rateLimit.getEnabled())) {
            return;
        }
        int maxConcurrent = valueOrDefault(rateLimit.getMaxConcurrentRequests(), 20);
        int current = concurrentRequests.incrementAndGet();
        if (current > maxConcurrent) {
            concurrentRequests.decrementAndGet();
            throw new BusinessException("AI服务当前请求较多，请稍后重试");
        }
        if (!allowClient(clientKey, valueOrDefault(rateLimit.getRequestsPerMinute(), 30))) {
            concurrentRequests.decrementAndGet();
            throw new BusinessException("AI请求过于频繁，请稍后再试");
        }
    }

    public void release() {
        AiProperties.RateLimit rateLimit = aiProperties.getRateLimit();
        if (rateLimit == null || !Boolean.TRUE.equals(rateLimit.getEnabled())) {
            return;
        }
        concurrentRequests.updateAndGet(value -> Math.max(value - 1, 0));
    }

    private boolean allowClient(String clientKey, int maxRequestsPerMinute) {
        String key = StrUtil.blankToDefault(clientKey, "unknown");
        long currentWindow = System.currentTimeMillis() / 60000L;
        WindowCounter counter = clientCounters.computeIfAbsent(key, item -> new WindowCounter(currentWindow));
        synchronized (counter) {
            if (counter.windowMinute != currentWindow) {
                counter.windowMinute = currentWindow;
                counter.count = 0;
            }
            counter.count++;
            return counter.count <= maxRequestsPerMinute;
        }
    }

    private int valueOrDefault(Integer value, int defaultValue) {
        return value == null || value <= 0 ? defaultValue : value;
    }

    private static class WindowCounter {
        private long windowMinute;
        private int count;

        private WindowCounter(long windowMinute) {
            this.windowMinute = windowMinute;
        }
    }
}
