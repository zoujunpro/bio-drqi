CREATE TABLE IF NOT EXISTS ai_api_registry (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  api_code VARCHAR(128) NOT NULL COMMENT 'API编码',
  service_name VARCHAR(128) NOT NULL COMMENT '服务名',
  path VARCHAR(255) NOT NULL COMMENT '接口路径',
  method VARCHAR(16) NOT NULL COMMENT 'HTTP方法',
  controller_class VARCHAR(255) DEFAULT NULL COMMENT 'Controller类名',
  method_name VARCHAR(128) DEFAULT NULL COMMENT '方法名',
  request_dto VARCHAR(255) DEFAULT NULL COMMENT '请求DTO',
  response_type VARCHAR(255) DEFAULT NULL COMMENT '响应类型',
  api_name VARCHAR(128) DEFAULT NULL COMMENT '接口名称',
  description VARCHAR(500) DEFAULT NULL COMMENT '接口说明',
  ai_enabled TINYINT DEFAULT 0 COMMENT '是否允许AI调用',
  read_only TINYINT DEFAULT 1 COMMENT '是否只读',
  risk_level VARCHAR(32) DEFAULT 'low' COMMENT '风险等级 low/medium/high',
  owner_module VARCHAR(128) DEFAULT NULL COMMENT '所属模块',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除',
  sync_time DATETIME DEFAULT NULL COMMENT '同步时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_ai_api_code (api_code),
  KEY idx_ai_api_service (service_name),
  KEY idx_ai_api_enabled (ai_enabled, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI API注册表';

CREATE TABLE IF NOT EXISTS ai_api_param (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  api_code VARCHAR(128) NOT NULL COMMENT 'API编码',
  param_name VARCHAR(128) NOT NULL COMMENT '参数名',
  param_type VARCHAR(128) DEFAULT NULL COMMENT '参数类型',
  required TINYINT DEFAULT 0 COMMENT '是否必填',
  java_field VARCHAR(128) DEFAULT NULL COMMENT 'Java字段名',
  business_name VARCHAR(128) DEFAULT NULL COMMENT '业务名称',
  aliases VARCHAR(500) DEFAULT NULL COMMENT '用户说法别名，逗号分隔',
  source_type VARCHAR(32) DEFAULT NULL COMMENT '来源类型 user/context/login/default/fixed',
  source_field VARCHAR(128) DEFAULT NULL COMMENT '来源字段',
  default_value VARCHAR(500) DEFAULT NULL COMMENT '默认值',
  ai_enabled TINYINT DEFAULT 0 COMMENT '是否允许AI填充',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除',
  sync_time DATETIME DEFAULT NULL COMMENT '同步时间',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_ai_api_param (api_code, param_name),
  KEY idx_ai_api_param_api (api_code),
  KEY idx_ai_api_param_enabled (ai_enabled, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI API参数表';

CREATE TABLE IF NOT EXISTS ai_workflow_definition (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  workflow_code VARCHAR(128) NOT NULL COMMENT 'Workflow编码',
  workflow_name VARCHAR(128) NOT NULL COMMENT 'Workflow名称',
  description VARCHAR(500) DEFAULT NULL COMMENT '说明',
  category VARCHAR(64) DEFAULT NULL COMMENT '分类',
  dsl_json LONGTEXT NOT NULL COMMENT 'Workflow DSL JSON',
  enabled TINYINT DEFAULT 1 COMMENT '是否启用',
  deleted TINYINT DEFAULT 0 COMMENT '是否删除',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_ai_workflow_code (workflow_code),
  KEY idx_ai_workflow_enabled (enabled, deleted)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI Workflow定义表';

CREATE TABLE IF NOT EXISTS ai_workflow_execution (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  execution_no VARCHAR(64) NOT NULL COMMENT '执行编号',
  workflow_id BIGINT NOT NULL COMMENT 'Workflow ID',
  workflow_code VARCHAR(128) NOT NULL COMMENT 'Workflow编码',
  input_json LONGTEXT DEFAULT NULL COMMENT '输入JSON',
  output_json LONGTEXT DEFAULT NULL COMMENT '输出JSON',
  status VARCHAR(32) NOT NULL COMMENT '状态 RUNNING/SUCCESS/FAILED',
  error_message VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
  cost_ms INT DEFAULT NULL COMMENT '耗时毫秒',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  UNIQUE KEY uk_ai_workflow_execution_no (execution_no),
  KEY idx_ai_workflow_execution_workflow (workflow_id),
  KEY idx_ai_workflow_execution_status (status, create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI Workflow执行记录表';

CREATE TABLE IF NOT EXISTS ai_workflow_step_log (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '主键',
  execution_id BIGINT NOT NULL COMMENT '执行ID',
  node_id VARCHAR(128) NOT NULL COMMENT '节点ID',
  node_type VARCHAR(32) NOT NULL COMMENT '节点类型',
  node_name VARCHAR(128) DEFAULT NULL COMMENT '节点名称',
  tool_code VARCHAR(128) DEFAULT NULL COMMENT '工具编码',
  input_json LONGTEXT DEFAULT NULL COMMENT '节点输入JSON',
  output_json LONGTEXT DEFAULT NULL COMMENT '节点输出JSON',
  status VARCHAR(32) NOT NULL COMMENT '状态 RUNNING/SUCCESS/FAILED',
  error_message VARCHAR(1000) DEFAULT NULL COMMENT '错误信息',
  cost_ms INT DEFAULT NULL COMMENT '耗时毫秒',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  KEY idx_ai_workflow_step_execution (execution_id),
  KEY idx_ai_workflow_step_node (node_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI Workflow节点执行日志表';
