package com.bio.drqi.es.handler;

import com.alibaba.fastjson2.JSON;
import com.bio.drqi.es.dto.CanalMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CanalKafkaConsumer {

    private final CanalMessageHandler canalMessageHandler;

    @KafkaListener(topics = "project-search-topic", groupId = "es-sync-consumer")
    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            String value = record.value();
            log.info("收到 Canal 消息: {}", value);
            CanalMessage message = JSON.parseObject(value, CanalMessage.class);
            canalMessageHandler.handle(message);
            // ES 写成功后再提交 offset
            ack.acknowledge();

        } catch (Exception e) {
            log.error("同步 ES 失败，不提交 offset，等待下次重试。message={}", record.value(), e);
            throw new RuntimeException(e);
        }
    }
}
