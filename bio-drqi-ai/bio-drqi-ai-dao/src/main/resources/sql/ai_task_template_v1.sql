CREATE TABLE IF NOT EXISTS ai_task_template (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code VARCHAR(64) NOT NULL COMMENT '模板编码',
    template_name VARCHAR(128) NOT NULL COMMENT '模板名称',
    intent_code VARCHAR(64) NOT NULL COMMENT '关联意图编码',
    domain VARCHAR(64) COMMENT '业务领域',
    description TEXT COMMENT '模板说明',
    status VARCHAR(32) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/DISABLED/DELETED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    UNIQUE KEY uk_template_code(template_code),
    KEY idx_intent_code(intent_code),
    KEY idx_status(status)
) COMMENT='AI任务模板';

CREATE TABLE IF NOT EXISTS ai_task_template_step (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    template_code VARCHAR(64) NOT NULL COMMENT '模板编码',
    step_no INT NOT NULL COMMENT '步骤顺序',
    task_code VARCHAR(64) NOT NULL COMMENT '任务编码',
    task_name VARCHAR(128) NOT NULL COMMENT '任务名称',
    task_type VARCHAR(32) NOT NULL COMMENT '任务类型：QUERY/ANALYSIS/MERGE/DIFY/TOOL',
    business_object VARCHAR(64) COMMENT '业务对象',
    target_code VARCHAR(128) COMMENT '目标编码，通常是工具编码或Dify应用编码',
    required_params JSON COMMENT '必填参数JSON数组',
    input_mapping JSON COMMENT '入参映射JSON',
    depends_on JSON COMMENT '依赖步骤JSON数组',
    status VARCHAR(32) DEFAULT 'ACTIVE' COMMENT '状态：ACTIVE/DISABLED/DELETED',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_template_code(template_code),
    KEY idx_task_code(task_code),
    KEY idx_status(status)
) COMMENT='AI任务模板步骤';
