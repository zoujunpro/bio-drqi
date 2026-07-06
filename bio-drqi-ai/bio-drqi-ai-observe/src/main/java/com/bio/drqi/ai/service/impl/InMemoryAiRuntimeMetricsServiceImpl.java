package com.bio.drqi.ai.service.impl;

import com.bio.drqi.ai.service.AiRuntimeMetricsService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class InMemoryAiRuntimeMetricsServiceImpl implements AiRuntimeMetricsService {

    private final Map<String, StageMetrics> metricsMap = new ConcurrentHashMap<>();

    @Override
    public void record(String stage, boolean success, long costMillis) {
        StageMetrics metrics = metricsMap.computeIfAbsent(stage, key -> new StageMetrics());
        metrics.total.incrementAndGet();
        if (success) {
            metrics.success.incrementAndGet();
        } else {
            metrics.failure.incrementAndGet();
        }
        metrics.totalCostMillis.addAndGet(Math.max(costMillis, 0));
        metrics.maxCostMillis.updateAndGet(current -> Math.max(current, costMillis));
    }

    @Override
    public Map<String, Object> snapshot() {
        Map<String, Object> snapshot = new LinkedHashMap<>();
        for (Map.Entry<String, StageMetrics> entry : metricsMap.entrySet()) {
            StageMetrics metrics = entry.getValue();
            long total = metrics.total.get();
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("total", total);
            item.put("success", metrics.success.get());
            item.put("failure", metrics.failure.get());
            item.put("avgCostMillis", total == 0 ? 0 : metrics.totalCostMillis.get() / total);
            item.put("maxCostMillis", metrics.maxCostMillis.get());
            snapshot.put(entry.getKey(), item);
        }
        return snapshot;
    }

    private static class StageMetrics {
        private final AtomicLong total = new AtomicLong();
        private final AtomicLong success = new AtomicLong();
        private final AtomicLong failure = new AtomicLong();
        private final AtomicLong totalCostMillis = new AtomicLong();
        private final AtomicLong maxCostMillis = new AtomicLong();
    }
}
