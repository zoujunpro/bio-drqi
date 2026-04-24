package com.bio.drqi.es.api;

import com.bio.drqi.es.sync.EsFullSyncService;
import com.bio.drqi.es.sync.EsSyncProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.listener.MessageListenerContainer;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/es/sync")
@ConditionalOnProperty(prefix = "sync.es", name = "enabled", havingValue = "true")
public class EsSyncController {

    private final EsFullSyncService fullSyncService;
    private final EsSyncProperties properties;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    public EsSyncController(EsFullSyncService fullSyncService,
                            EsSyncProperties properties,
                            KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.fullSyncService = fullSyncService;
        this.properties = properties;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    @PostMapping("/full")
    public Map<String, Object> fullSync(@RequestBody(required = false) FullSyncRequest request) {
        if (!properties.getFull().isEnabled()) {
            throw new IllegalStateException("全量同步能力未开启，请先配置 sync.es.full.enabled=true");
        }
        Set<String> ruleKeySet = null;
        if (request != null && request.getRuleKeys() != null && !request.getRuleKeys().isEmpty()) {
            ruleKeySet = request.getRuleKeys().stream().filter(key -> key != null && !key.trim().isEmpty()).collect(Collectors.toSet());
            if (ruleKeySet.isEmpty()) {
                ruleKeySet = null;
            }
        }
        Map<String, Object> result = ruleKeySet == null ? fullSyncService.syncAllConfiguredTables() : fullSyncService.syncByRuleKeys(ruleKeySet);
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "全量同步完成");
        response.put("data", result);
        return response;
    }

    @PostMapping("/full/all")
    public Map<String, Object> fullSyncAll() {
        if (!properties.getFull().isEnabled()) {
            throw new IllegalStateException("全量同步能力未开启，请先配置 sync.es.full.enabled=true");
        }
        Map<String, Object> result = fullSyncService.syncAllConfiguredTables();
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "全量同步完成");
        response.put("data", result);
        return response;
    }

    @PostMapping("/realtime/start")
    public Map<String, Object> startRealtime() {
        if (!properties.isRealtimeEnabled()) {
            throw new IllegalStateException("实时增量同步未开启，请先配置 sync.es.realtime-enabled=true");
        }
        MessageListenerContainer container = kafkaListenerEndpointRegistry.getListenerContainer("canalEsSyncListener");
        if (container == null) {
            throw new IllegalStateException("增量监听器不存在: canalEsSyncListener");
        }
        if (!container.isRunning()) {
            container.start();
        }
        Map<String, Object> response = new LinkedHashMap<>();
        response.put("success", true);
        response.put("message", "增量同步监听器已启动");
        response.put("running", container.isRunning());
        return response;
    }
}
