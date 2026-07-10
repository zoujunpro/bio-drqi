create table if not exists ai_session (
    id bigint primary key auto_increment comment '主键',
    session_id varchar(64) not null comment '业务会话ID',
    user_id varchar(64) not null comment '用户ID',
    username varchar(128) null comment '用户名',
    nickname varchar(128) null comment '用户昵称',
    job_num varchar(64) null comment '工号',
    agent_id varchar(64) null comment 'Agent或应用ID',
    title varchar(255) null comment '会话标题',
    dify_conversation_id varchar(128) null comment 'Dify会话ID',
    status varchar(32) not null default 'ACTIVE' comment '状态',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    unique key uk_ai_session_session_id (session_id),
    key idx_ai_session_user_id (user_id),
    key idx_ai_session_dify_conversation_id (dify_conversation_id)
) comment 'AI会话';

create table if not exists ai_message (
    id bigint primary key auto_increment comment '主键',
    session_id varchar(64) not null comment '业务会话ID',
    user_id varchar(64) not null comment '用户ID',
    role varchar(32) not null comment 'user/assistant/tool/system',
    content longtext not null comment '消息内容',
    source varchar(64) null comment '消息来源',
    dify_message_id varchar(128) null comment 'Dify消息ID',
    metadata json null comment '扩展JSON',
    create_time datetime not null default current_timestamp comment '创建时间',
    key idx_ai_message_session_id_id (session_id, id),
    key idx_ai_message_user_id (user_id),
    key idx_ai_message_dify_message_id (dify_message_id)
) comment 'AI消息';

create table if not exists ai_user_memory (
    id bigint primary key auto_increment comment '主键',
    user_id varchar(64) not null comment '用户ID',
    agent_id varchar(64) null comment 'Agent或应用ID',
    memory_type varchar(64) not null comment '记忆类型',
    memory_key varchar(128) not null comment '记忆键',
    memory_value text not null comment '记忆值',
    source varchar(64) null comment '来源',
    confidence decimal(5,4) null comment '可信度',
    importance int not null default 0 comment '重要程度',
    status varchar(32) not null default 'ACTIVE' comment '状态',
    expire_time datetime null comment '过期时间',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    key idx_ai_user_memory_user_id (user_id),
    key idx_ai_user_memory_user_key (user_id, memory_key),
    key idx_ai_user_memory_status_expire (status, expire_time)
) comment 'AI用户长期记忆';

create table if not exists ai_memory_summary (
    id bigint primary key auto_increment comment '主键',
    session_id varchar(64) not null comment '业务会话ID',
    user_id varchar(64) not null comment '用户ID',
    agent_id varchar(64) null comment 'Agent或应用ID',
    summary text not null comment '摘要内容',
    last_message_id bigint null comment '覆盖到的最后消息ID',
    version int not null default 1 comment '摘要版本',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    key idx_ai_memory_summary_session_version (session_id, version),
    key idx_ai_memory_summary_user_id (user_id)
) comment 'AI会话摘要';

create table if not exists ai_message_file (
    id bigint primary key auto_increment comment '主键',
    session_id varchar(64) not null comment '业务会话ID',
    message_id bigint null comment '关联消息ID',
    user_id varchar(64) not null comment '用户ID',
    file_id varchar(64) not null comment '业务文件ID',
    file_name varchar(255) not null comment '原文件名',
    file_type varchar(32) not null comment '文件类型',
    mime_type varchar(128) null comment 'MIME类型',
    file_size bigint null comment '文件大小',
    bucket_name varchar(128) null comment 'MinIO bucket',
    object_key varchar(512) null comment 'MinIO object key',
    file_url varchar(1024) null comment '临时访问地址或内部文件地址',
    parse_status varchar(32) not null default 'WAITING' comment '解析状态',
    parsed_text longtext null comment '解析文本',
    summary text null comment '文件摘要',
    error_message varchar(1024) null comment '解析失败原因',
    create_time datetime not null default current_timestamp comment '创建时间',
    update_time datetime not null default current_timestamp on update current_timestamp comment '更新时间',
    unique key uk_ai_message_file_file_id (file_id),
    key idx_ai_message_file_session_id (session_id),
    key idx_ai_message_file_message_id (message_id),
    key idx_ai_message_file_user_id (user_id),
    key idx_ai_message_file_parse_status (parse_status)
) comment 'AI会话消息文件';
