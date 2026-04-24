package com.bio.drqi.es.sync;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
@Component
@ConfigurationProperties(prefix = "sync.es")
public class EsSyncProperties {

    /**
     * 是否开启 Kafka -> ES 增量同步
     */
    private boolean enabled;

    /**
     * 实时增量同步开关（Kafka -> ES）
     */
    private boolean realtimeEnabled = true;

    /**
     * 实时监听是否随应用启动自动启动
     */
    private boolean realtimeAutoStart = false;

    /**
     * Canal 投递 Kafka 的 topic
     */
    private String topic = "canal_binlog";

    /**
     * Kafka consumer group
     */
    private String groupId = "bio-drqi-es-sync";

    /**
     * ES 地址，例如：http://127.0.0.1:9200
     */
    private List<String> hosts = new ArrayList<>();

    private String username;

    private String password;

    /**
     * 按 "database.table" 配置索引规则
     */
    private Map<String, TableRule> tableRules = new HashMap<>();

    private FullSync full = new FullSync();

    @Data
    public static class FullSync {

        /**
         * 是否启用全量能力
         */
        private boolean enabled = true;

        /**
         * 单批同步条数
         */
        private int batchSize = 1000;
    }

    @Data
    public static class TableRule {

        /**
         * ES index name
         */
        private String index;

        /**
         * 业务主键字段名，映射为 ES _id
         */
        private String idField = "id";

        /**
         * 全量 SQL 的来源表（不配默认用 rule key，即 database.table）
         */
        private String sourceTable;

        /**
         * 全量同步 where 条件（不带 where 关键字）
         */
        private String whereClause;

        /**
         * 该表是否参与全量
         */
        private boolean fullSync = true;
    }
}
