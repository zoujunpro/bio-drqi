package com.bio.drqi.ai.service;

import java.util.Map;

public interface AiRuntimeMetricsService {

    void record(String stage, boolean success, long costMillis);

    Map<String, Object> snapshot();
}
