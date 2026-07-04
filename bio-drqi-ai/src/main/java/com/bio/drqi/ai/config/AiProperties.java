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
     * AI命令配置。AI服务通过这些命令调用现有后端接口，避免直接侵入业务代码。
     */
    private List<Command> commands = new ArrayList<>();

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

    /**
     * 意图识别配置。
     */
    private Intent intent = new Intent();

    /**
     * 业务术语配置。只把命中用户问题的术语传给模型。
     */
    private List<Term> terms = new ArrayList<>();

    /**
     * 查询风险控制配置。
     */
    private Risk risk = new Risk();

    /**
     * 会话记忆配置。
     */
    private Memory memory = new Memory();

    /**
     * LLM结果缓存配置。
     */
    private Cache cache = new Cache();

    @Data
    public static class Llm {
        /**
         * OpenAI兼容接口地址，例如：http://172.16.14.9:11434/v1
         */
        private String baseUrl;

        /**
         * API Key。调用阿里云百炼 Qwen-Plus 时必填；本地 Ollama/vLLM 可为空。
         */
        private String apiKey;

        /**
         * 模型名称，例如：qwen-plus、qwen3:8b。
         */
        private String model = "qwen3:8b";

        /**
         * 查询计划生成需要稳定输出，温度不要太高。
         */
        private Double temperature = 0.1D;

        private Double routerTemperature = 0.0D;

        private Double queryTemperature = 0.1D;

        private Double reportTemperature = 0.1D;

        private Double commandTemperature = 0.1D;

        private Double chatTemperature = 0.7D;

        /**
         * 单次模型调用超时时间，单位毫秒。
         */
        private Integer timeout = 120000;
    }

    @Data
    public static class Intent {
        private Double confidenceThreshold = 0.6D;
        private List<String> reportExportKeywords = new ArrayList<>(Arrays.asList("导出", "excel", "Excel", "报表", "下载"));
        private List<String> workflowKeywords = new ArrayList<>(Arrays.asList("审批", "工单", "待办", "已办", "我发起", "流程"));
        private List<String> businessQueryKeywords = new ArrayList<>(Arrays.asList("统计", "查询", "查看", "明细", "表格", "数量", "多少", "成功", "失败", "趋势", "占比", "列表"));
        private List<String> chatKeywords = new ArrayList<>(Arrays.asList("你好", "你是谁", "怎么用", "解释一下"));
    }

    @Data
    public static class Term {
        /**
         * 用户常用表达，例如：取样数量。
         */
        private String phrase;

        /**
         * 所属业务域，可为空。
         */
        private String domain;

        /**
         * 标准业务含义。
         */
        private String meaning;

        /**
         * 建议使用的指标。
         */
        private String metric;

        /**
         * 建议使用的字段。
         */
        private String field;
    }

    @Data
    public static class Risk {
        /**
         * 明细查询最大返回行数。
         */
        private Integer detailMaxLimit = 200;

        /**
         * 聚合查询最大返回行数。
         */
        private Integer aggregateMaxLimit = 500;

        /**
         * 无过滤条件明细查询最大返回行数。
         */
        private Integer noFilterDetailMaxLimit = 50;

        /**
         * 是否禁止无过滤条件明细查询。默认先不禁止，只收紧limit。
         */
        private Boolean rejectNoFilterDetail = Boolean.FALSE;

        /**
         * JDBC查询超时时间，单位秒。
         */
        private Integer queryTimeoutSeconds = 30;
    }

    @Data
    public static class Memory {
        /**
         * 短期会话保留小时数。
         */
        private Integer retentionHours = 24;

        /**
         * 是否启用过期会话清理。
         */
        private Boolean cleanupEnabled = Boolean.TRUE;
    }

    @Data
    public static class Cache {
        /**
         * 是否启用 Redis 缓存。Redis 不可用时会自动降级为不缓存。
         */
        private Boolean enabled = Boolean.TRUE;

        /**
         * 业务域识别结果缓存时间，单位秒。
         */
        private Long domainSelectTtlSeconds = 1800L;

        /**
         * 查询计划缓存时间，单位秒。
         */
        private Long queryPlanTtlSeconds = 1800L;
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

    @Data
    public static class Command {
        /**
         * 命令编码，例如 workflow.todo.query。
         */
        private String code;

        /**
         * 命令名称，给模型和日志展示。
         */
        private String name;

        /**
         * 命令说明，模型根据它判断什么时候调用该命令。
         */
        private String description;

        /**
         * 目标服务名，支持 Nacos 服务名，例如 bio-cer-service。
         */
        private String service;

        /**
         * 目标接口路径，例如 /cer/task/queryPage。
         */
        private String path;

        /**
         * HTTP方法，当前支持 GET/POST。
         */
        private String method = "POST";

        /**
         * 命令参数定义。
         */
        private List<CommandParam> params = new ArrayList<>();
    }

    @Data
    public static class CommandParam {
        private String name;
        private String description;
        private Boolean required = Boolean.TRUE;
        private Object defaultValue;
    }
}
