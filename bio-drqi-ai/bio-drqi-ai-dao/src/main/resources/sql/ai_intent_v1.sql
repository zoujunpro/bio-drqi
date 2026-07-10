create table if not exists ai_intent (
    id bigint primary key auto_increment comment '主键',
    intent_code varchar(64) not null comment '意图唯一编码',
    intent_name varchar(128) not null comment '意图名称',
    domain varchar(64) null comment '业务领域',
    description text null comment '意图描述',
    handler_type varchar(32) not null comment '处理类型：TOOL/RAG/FILE/CHAT/WORKFLOW',
    status varchar(32) not null default 'ACTIVE' comment '状态：ACTIVE/DISABLED/DELETED',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    unique key uk_ai_intent_code (intent_code),
    key idx_ai_intent_status (status),
    key idx_ai_intent_domain (domain)
) comment 'AI业务意图主表';

create table if not exists ai_intent_example (
    id bigint primary key auto_increment comment '主键',
    intent_code varchar(64) not null comment '意图编码',
    example_text varchar(500) not null comment '用户表达样例',
    status varchar(32) not null default 'ACTIVE' comment '状态：ACTIVE/DISABLED/DELETED',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    key idx_ai_intent_example_code (intent_code),
    key idx_ai_intent_example_status (status)
) comment 'AI业务意图表达样例';

create table if not exists ai_tool_definition (
    id bigint primary key auto_increment comment '主键',
    tool_code varchar(64) not null comment '工具唯一编码',
    tool_name varchar(128) not null comment '工具名称',
    description text null comment '工具描述',
    tool_type varchar(32) not null default 'API' comment '工具类型：API/WORKFLOW/DIFY/MCP/LOCAL',
    target_code varchar(128) null comment '目标编码：api_code/workflow_code/dify_tool_code/mcp_tool_code',
    input_schema longtext null comment '入参JSON Schema',
    output_schema longtext null comment '出参JSON Schema',
    service_url varchar(500) null comment '服务地址或网关路径',
    http_method varchar(20) null comment 'HTTP方法',
    risk_level varchar(32) not null default 'LOW' comment '风险等级：LOW/MEDIUM/HIGH',
    read_only tinyint not null default 1 comment '是否只读：1是0否',
    status varchar(32) not null default 'ACTIVE' comment '状态：ACTIVE/DISABLED/DELETED',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    unique key uk_ai_tool_code (tool_code),
    key idx_ai_tool_type (tool_type),
    key idx_ai_tool_target (target_code),
    key idx_ai_tool_status (status)
) comment 'AI工具定义';

create table if not exists ai_intent_tool_rel (
    id bigint primary key auto_increment comment '主键',
    intent_code varchar(64) not null comment '意图编码',
    tool_code varchar(64) not null comment '工具编码',
    priority int not null default 100 comment '执行优先级',
    status varchar(32) not null default 'ACTIVE' comment '状态：ACTIVE/DISABLED/DELETED',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    unique key uk_ai_intent_tool (intent_code, tool_code),
    key idx_ai_intent_tool_intent (intent_code),
    key idx_ai_intent_tool_tool (tool_code),
    key idx_ai_intent_tool_status (status)
) comment 'AI意图工具关系';
