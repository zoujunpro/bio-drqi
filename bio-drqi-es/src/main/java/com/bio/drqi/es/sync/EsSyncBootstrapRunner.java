package com.bio.drqi.es.sync;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@ConditionalOnProperty(prefix = "sync.es", name = "enabled", havingValue = "true")
public class EsSyncBootstrapRunner implements ApplicationRunner {

    private final EsSyncProperties properties;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    public EsSyncBootstrapRunner(EsSyncProperties properties,
                                 KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry) {
        this.properties = properties;
        this.kafkaListenerEndpointRegistry = kafkaListenerEndpointRegistry;
    }

    @Override
    public void run(ApplicationArguments args) {
        // 全量同步改为 API 手动触发，不在启动时自动执行
        if (properties.isRealtimeEnabled() && properties.isRealtimeAutoStart()) {
            if (kafkaListenerEndpointRegistry.getListenerContainer("canalEsSyncListener") != null) {
                kafkaListenerEndpointRegistry.getListenerContainer("canalEsSyncListener").start();
                log.info("已启动 Kafka 增量同步监听器 canalEsSyncListener");
            } else {
                log.warn("Kafka 增量监听器不存在: canalEsSyncListener");
            }
        } else {
            log.info("跳过 Kafka 增量同步自动启动，realtimeEnabled={}, realtimeAutoStart={}",
                    properties.isRealtimeEnabled(), properties.isRealtimeAutoStart());
        }
    }
}
