package com.bio.drqi.es.handler;

import com.alibaba.fastjson2.JSON;
import com.bio.drqi.es.dto.CanalMessage;
import com.bio.drqi.es.config.CanalSyncEnabledCondition;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.context.annotation.Conditional;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
@Conditional(CanalSyncEnabledCondition.class)
public class CanalKafkaConsumer {

    private final CanalMessageHandler canalMessageHandler;

    @KafkaListener(
            topics = "${bio.es.canal.topic}",
            groupId = "${bio.es.canal.group-id}"
    )
    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            String value = record.value();
            log.debug("收到 Canal 消息 topic={}, partition={}, offset={}, value={}",
                    record.topic(), record.partition(), record.offset(), value);
            CanalMessage message = JSON.parseObject(value, CanalMessage.class);
            canalMessageHandler.handle(message);
            // ES 写成功后再提交 offset
            ack.acknowledge();

        } catch (Exception e) {
            log.error("同步 ES 失败，不提交 offset，等待下次重试。topic={}, partition={}, offset={}, message={}",
                    record.topic(), record.partition(), record.offset(), record.value(), e);
            throw new RuntimeException(e);
        }
    }
}
