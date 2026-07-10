create table if not exists ai_business_dictionary (
    id bigint primary key auto_increment comment '主键',
    dict_type varchar(64) not null comment '词典类型：PROJECT/CROP/BASE/DEPARTMENT等',
    dict_code varchar(128) not null comment '业务编码',
    dict_name varchar(255) not null comment '标准名称',
    aliases varchar(1000) null comment '别名，英文逗号分隔',
    domain varchar(64) null comment '业务领域',
    status varchar(32) not null default 'ACTIVE' comment '状态：ACTIVE/DISABLED/DELETED',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    unique key uk_ai_dict_type_code (dict_type, dict_code),
    key idx_ai_dict_type (dict_type),
    key idx_ai_dict_domain (domain),
    key idx_ai_dict_status (status)
) comment 'AI业务词典';

create table if not exists ai_semantic_pattern (
    id bigint primary key auto_increment comment '主键',
    pattern_type varchar(64) not null comment '模式类型：REFERENCE_WORD/SYSTEM_CLASSIFY/INTENT_KEYWORD/ENTITY_REGEX/TIME_RULE',
    pattern_code varchar(128) not null comment '模式编码',
    pattern_text varchar(1000) not null comment '模式内容',
    target_value varchar(255) null comment '目标值',
    weight decimal(8,4) not null default 1.0000 comment '权重',
    domain varchar(64) null comment '业务领域',
    status varchar(32) not null default 'ACTIVE' comment '状态：ACTIVE/DISABLED/DELETED',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    key idx_ai_pattern_type (pattern_type),
    key idx_ai_pattern_code (pattern_code),
    key idx_ai_pattern_status (status)
) comment 'AI语义规则模式';
