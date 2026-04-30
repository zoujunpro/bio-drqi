package com.bio.drqi.es.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "bio.es")
public class EsSyncProperties {

    /**
     * 是否开启 Kafka -> ES 增量同步
     */
    private boolean enabled;

    /**
     * ES 地址，例如：http://127.0.0.1:9200
     */
    private List<String> hosts = new ArrayList<>();

    private String username;

    private String password;

    private Canal canal = new Canal();

    @Data
    public static class Canal {

        /**
         * 是否开启 Kafka -> ES 增量同步。
         */
        private boolean enabled;

        /**
         * Canal 写入的 Kafka topic。
         */
        private String topic;

        /**
         * ES 同步消费者组。
         */
        private String groupId;
    }
}
