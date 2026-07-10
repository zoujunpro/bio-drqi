-- 文档 AI 索引事件增量脚本。
-- 用于已迁移过 DocHub 业务表的环境，只补充 AI/RAG 索引事件 outbox 表。

CREATE TABLE IF NOT EXISTS `doc_ai_index_event` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `event_type` varchar(64) NOT NULL COMMENT '事件类型',
  `document_id` bigint NOT NULL COMMENT '文档ID',
  `version_id` bigint DEFAULT NULL COMMENT '版本ID',
  `doc_code` varchar(255) DEFAULT NULL COMMENT '文档编号',
  `doc_name` varchar(255) DEFAULT NULL COMMENT '文档名称',
  `file_path` varchar(1000) DEFAULT NULL COMMENT '文件对象路径',
  `file_type` varchar(64) DEFAULT NULL COMMENT '文件类型',
  `status` varchar(32) NOT NULL DEFAULT 'PENDING' COMMENT '状态：PENDING/PROCESSING/SUCCESS/FAILED/IGNORED',
  `retry_count` int NOT NULL DEFAULT 0 COMMENT '重试次数',
  `error_message` varchar(1000) DEFAULT NULL COMMENT '错误信息',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_doc_ai_event_status` (`status`, `id`),
  KEY `idx_doc_ai_event_document` (`document_id`, `version_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='文档AI索引事件表';
