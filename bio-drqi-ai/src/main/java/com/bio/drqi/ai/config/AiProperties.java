package com.bio.drqi.ai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Data
@ConfigurationProperties(prefix = "bio.ai")
public class AiProperties {

    /**
     * 大模型连接配置。
     * 当前按 OpenAI 兼容接口设计，Ollama/vLLM/Dify 等只要暴露 /chat/completions 都可以接入。
     */
    private Llm llm = new Llm();

    /**
     * 自动表域字段字典映射。
     * 自动读取数据库表字段时，只有配置在这里的字段才会按字典翻译。
     */
    private List<DictField> dictFields = new ArrayList<>();

    /**
     * 自动注册表白名单。为空时表示不过滤白名单。
     */
    private List<String> includeTables = new ArrayList<>();

    /**
     * 自动注册表黑名单，支持 * 通配符。
     */
    private List<String> excludeTables = new ArrayList<>(Arrays.asList(
            "system_*", "*_log", "*_ref", "bio_request_log"
    ));

    /**
     * 自动注册字段黑名单，支持 * 通配符。
     */
    private List<String> excludeFields = new ArrayList<>(Arrays.asList(
            "password", "token", "*secret*", "*key*", "phone", "mobile", "id_card", "identity_card"
    ));

    /**
     * 传给模型选择业务域的最大候选数量，避免 prompt 过长。
     */
    private Integer maxDomainPromptSize = 80;

    @Data
    public static class Llm {
        /**
         * OpenAI兼容接口地址，例如：http://172.16.14.9:11434/v1
         */
        private String baseUrl;

        /**
         * 模型名称，例如：qwen3:8b
         */
        private String model = "qwen3:8b";

        /**
         * 查询计划生成需要稳定输出，温度不要太高。
         */
        private Double temperature = 0.1D;

        /**
         * 单次模型调用超时时间，单位毫秒。
         */
        private Integer timeout = 120000;
    }

    @Data
    public static class DictField {
        /**
         * 表名，例如 tc_harvest_seed_tb。填 * 表示按字段名全局匹配。
         */
        private String tableName;

        /**
         * 字段名，支持数据库列名 seed_type 或驼峰字段名 seedType。
         */
        private String fieldName;

        /**
         * 字典来源：bio_dict / bms_dict。
         */
        private String dictSource = "bio_dict";

        /**
         * 字典类型。bio_dict 对应 dict_type，bms_dict 对应 dict_type_code。
         */
        private String dictType;
    }
}
