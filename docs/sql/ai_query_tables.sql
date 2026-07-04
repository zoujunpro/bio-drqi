-- AI智能查询生产化建议表结构。
-- 当前代码先使用内存短期记忆；需要多实例、重启不丢上下文时，再接入这些表。

CREATE TABLE IF NOT EXISTS AI_conversation (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    user_id VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
    title VARCHAR(128) DEFAULT NULL COMMENT '会话标题',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态：1有效 0删除',
    expire_time DATETIME DEFAULT NULL COMMENT '短期记忆过期时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ai_conversation_id (conversation_id),
    KEY idx_ai_conversation_user_time (user_id, update_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI会话';

CREATE TABLE IF NOT EXISTS AI_message (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    user_id VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
    role VARCHAR(16) NOT NULL COMMENT '角色：system/user/assistant/tool',
    content TEXT NOT NULL COMMENT '消息内容',
    intent VARCHAR(32) DEFAULT NULL COMMENT '识别意图',
    domain VARCHAR(64) DEFAULT NULL COMMENT '业务域',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_ai_message_conversation_time (conversation_id, create_time),
    KEY idx_ai_message_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI会话消息';

CREATE TABLE IF NOT EXISTS AI_query_context (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    conversation_id VARCHAR(64) NOT NULL COMMENT '会话ID',
    current_domain VARCHAR(64) DEFAULT NULL COMMENT '当前业务域',
    last_query_plan LONGTEXT DEFAULT NULL COMMENT '最近一次查询计划JSON',
    confirmed_terms LONGTEXT DEFAULT NULL COMMENT '已确认术语JSON',
    pending_clarification VARCHAR(512) DEFAULT NULL COMMENT '待澄清问题',
    last_result_summary VARCHAR(512) DEFAULT NULL COMMENT '最近一次结果摘要',
    last_result_snapshot LONGTEXT DEFAULT NULL COMMENT '最近一次结果快照JSON，包含表头和部分数据',
    expire_time DATETIME DEFAULT NULL COMMENT '过期时间',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ai_query_context_conversation (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI短期查询上下文';

CREATE TABLE IF NOT EXISTS AI_business_term (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    phrase VARCHAR(128) NOT NULL COMMENT '用户常用说法',
    domain VARCHAR(64) DEFAULT NULL COMMENT '所属业务域',
    meaning VARCHAR(512) NOT NULL COMMENT '业务含义',
    metric VARCHAR(128) DEFAULT NULL COMMENT '对应指标',
    field VARCHAR(128) DEFAULT NULL COMMENT '对应字段',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    KEY idx_ai_business_term_phrase (phrase),
    KEY idx_ai_business_term_domain (domain)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI业务术语词典';

CREATE TABLE IF NOT EXISTS AI_metric_definition (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    domain VARCHAR(64) NOT NULL COMMENT '业务域',
    metric VARCHAR(128) NOT NULL COMMENT '指标编码',
    label VARCHAR(128) NOT NULL COMMENT '指标名称',
    description VARCHAR(512) DEFAULT NULL COMMENT '指标说明',
    expression_type VARCHAR(32) NOT NULL DEFAULT 'count' COMMENT '表达式类型：count/sum/avg/min/max/count_distinct',
    field VARCHAR(128) DEFAULT NULL COMMENT '指标字段',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ai_metric_domain_metric (domain, metric)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI指标定义';

CREATE TABLE IF NOT EXISTS AI_intent_keyword (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    intent VARCHAR(32) NOT NULL COMMENT '意图',
    keyword VARCHAR(64) NOT NULL COMMENT '关键词',
    weight INT NOT NULL DEFAULT 1 COMMENT '权重',
    enabled TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_ai_intent_keyword (intent, keyword)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI意图关键词';

CREATE TABLE IF NOT EXISTS AI_query_audit_log (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    trace_id VARCHAR(64) DEFAULT NULL COMMENT '链路ID',
    conversation_id VARCHAR(64) DEFAULT NULL COMMENT '会话ID',
    user_id VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
    scenario VARCHAR(32) NOT NULL COMMENT '场景',
    question TEXT NOT NULL COMMENT '用户问题',
    intent VARCHAR(32) DEFAULT NULL COMMENT '意图',
    domain VARCHAR(64) DEFAULT NULL COMMENT '业务域',
    query_plan LONGTEXT DEFAULT NULL COMMENT '查询计划JSON',
    sql_text TEXT DEFAULT NULL COMMENT '最终执行SQL',
    row_count INT NOT NULL DEFAULT 0 COMMENT '返回行数',
    cost_ms BIGINT NOT NULL DEFAULT 0 COMMENT '耗时毫秒',
    success TINYINT NOT NULL DEFAULT 1 COMMENT '是否成功',
    error_message VARCHAR(1024) DEFAULT NULL COMMENT '错误信息',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_ai_query_audit_time (create_time),
    KEY idx_ai_query_audit_user_time (user_id, create_time),
    KEY idx_ai_query_audit_conversation (conversation_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI查询审计日志';

CREATE TABLE IF NOT EXISTS AI_feedback (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    conversation_id VARCHAR(64) DEFAULT NULL COMMENT '会话ID',
    message_id BIGINT DEFAULT NULL COMMENT '消息ID',
    user_id VARCHAR(64) DEFAULT NULL COMMENT '用户ID',
    feedback_type VARCHAR(32) NOT NULL COMMENT '反馈类型：like/dislike/correction',
    correction TEXT DEFAULT NULL COMMENT '纠正内容',
    create_time DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_ai_feedback_conversation (conversation_id),
    KEY idx_ai_feedback_user_time (user_id, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI用户反馈';
