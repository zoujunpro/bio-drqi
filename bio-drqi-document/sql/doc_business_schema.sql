-- DocHub 文档业务表迁移脚本。
-- 只包含 doc_* 业务表，不包含 DocHub 自带的 sys_* 用户/角色/部门/菜单表。
-- USER/ROLE/DEPT 权限目标统一映射到现有 system_user_tb/system_role_tb/system_dept_tb。

CREATE TABLE IF NOT EXISTS `doc_callback_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` bigint DEFAULT NULL,
  `version_id` bigint DEFAULT NULL,
  `callback_status` int DEFAULT NULL,
  `callback_payload` longtext DEFAULT NULL,
  `download_url` varchar(1000) DEFAULT NULL,
  `result` varchar(255) DEFAULT NULL,
  `fail_reason` varchar(255) DEFAULT NULL,
  `retry_count` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_callback_log';

CREATE TABLE IF NOT EXISTS `doc_category_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `parent_id` bigint DEFAULT NULL,
  `category_name` varchar(255) DEFAULT NULL,
  `category_code` varchar(255) DEFAULT NULL,
  `category_type` varchar(255) DEFAULT NULL,
  `sort_num` int DEFAULT NULL,
  `status` tinyint DEFAULT NULL,
  `manager_user_id` bigint DEFAULT NULL,
  `manager_scope` varchar(255) DEFAULT NULL,
  `inherit_permission` char(1) DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` char(1) NOT NULL DEFAULT 'N',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_category_info';

CREATE TABLE IF NOT EXISTS `doc_code_sequence` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_code` varchar(255) DEFAULT NULL,
  `year_month` varchar(255) DEFAULT NULL,
  `current_seq` int DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_doc_code_sequence` (`category_code`, `year_month`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_code_sequence';

CREATE TABLE IF NOT EXISTS `doc_download_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` bigint DEFAULT NULL,
  `version_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `user_agent` varchar(1000) DEFAULT NULL,
  `download_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_download_log';

CREATE TABLE IF NOT EXISTS `doc_edit_lock` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` bigint DEFAULT NULL,
  `version_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `lock_token` varchar(255) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_edit_lock';

CREATE TABLE IF NOT EXISTS `doc_favorite_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint DEFAULT NULL,
  `file_id` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_favorite_rel';

CREATE TABLE IF NOT EXISTS `doc_file_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `category_id` bigint DEFAULT NULL,
  `doc_name` varchar(255) DEFAULT NULL,
  `doc_code` varchar(255) DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `file_size` bigint DEFAULT NULL,
  `current_version_id` bigint DEFAULT NULL,
  `owner_user_id` bigint DEFAULT NULL,
  `space_type` varchar(255) DEFAULT NULL,
  `permission_mode` varchar(255) DEFAULT NULL,
  `effective_type` varchar(255) DEFAULT NULL,
  `effective_start_time` datetime DEFAULT NULL,
  `effective_end_time` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_by` bigint DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  `deleted` char(1) NOT NULL DEFAULT 'N',
  `remark` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_file_info';

CREATE TABLE IF NOT EXISTS `doc_file_tag_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` bigint DEFAULT NULL,
  `tag_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_file_tag_rel';

CREATE TABLE IF NOT EXISTS `doc_operation_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` bigint DEFAULT NULL,
  `version_id` bigint DEFAULT NULL,
  `operation_type` varchar(255) DEFAULT NULL,
  `operation_content` text DEFAULT NULL,
  `before_value` text DEFAULT NULL,
  `after_value` text DEFAULT NULL,
  `operator_id` bigint DEFAULT NULL,
  `operator_name` varchar(255) DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `user_agent` varchar(1000) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_operation_log';

CREATE TABLE IF NOT EXISTS `doc_permission_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `resource_type` varchar(255) DEFAULT NULL,
  `resource_id` bigint DEFAULT NULL,
  `target_type` varchar(255) DEFAULT NULL,
  `target_id` bigint DEFAULT NULL,
  `can_view` tinyint DEFAULT NULL,
  `can_download` tinyint DEFAULT NULL,
  `can_edit` tinyint DEFAULT NULL,
  `can_share` tinyint DEFAULT NULL,
  `can_delete` tinyint DEFAULT NULL,
  `can_version` tinyint DEFAULT NULL,
  `can_permission` tinyint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_permission_info';

CREATE TABLE IF NOT EXISTS `doc_recycle_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` bigint DEFAULT NULL,
  `delete_user_id` bigint DEFAULT NULL,
  `delete_time` datetime DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  `restore_time` datetime DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_recycle_info';

CREATE TABLE IF NOT EXISTS `doc_share_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` bigint DEFAULT NULL,
  `share_user_id` bigint DEFAULT NULL,
  `share_type` varchar(255) DEFAULT NULL,
  `share_token` varchar(255) DEFAULT NULL,
  `can_view` tinyint DEFAULT NULL,
  `can_download` tinyint DEFAULT NULL,
  `can_edit` tinyint DEFAULT NULL,
  `expire_time` datetime DEFAULT NULL,
  `access_count` int DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  `update_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_share_info';

CREATE TABLE IF NOT EXISTS `doc_share_target_rel` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `share_id` bigint DEFAULT NULL,
  `target_type` varchar(255) DEFAULT NULL,
  `target_id` bigint DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_share_target_rel';

CREATE TABLE IF NOT EXISTS `doc_tag_info` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `tag_name` varchar(255) DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_tag_info';

CREATE TABLE IF NOT EXISTS `doc_version_his` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` bigint DEFAULT NULL,
  `version_no` varchar(255) DEFAULT NULL,
  `file_name` varchar(255) DEFAULT NULL,
  `file_path` varchar(1000) DEFAULT NULL,
  `file_md5` varchar(64) DEFAULT NULL,
  `file_sha256` varchar(255) DEFAULT NULL,
  `file_size` bigint DEFAULT NULL,
  `file_type` varchar(255) DEFAULT NULL,
  `change_log` text DEFAULT NULL,
  `is_current` tinyint DEFAULT NULL,
  `create_by` bigint DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_version_his';

CREATE TABLE IF NOT EXISTS `doc_view_log` (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `file_id` bigint DEFAULT NULL,
  `version_id` bigint DEFAULT NULL,
  `user_id` bigint DEFAULT NULL,
  `ip_address` varchar(255) DEFAULT NULL,
  `user_agent` varchar(1000) DEFAULT NULL,
  `view_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='doc_view_log';

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

CREATE INDEX `idx_doc_file_category` ON `doc_file_info` (`category_id`);
CREATE INDEX `idx_doc_file_owner` ON `doc_file_info` (`owner_user_id`);
CREATE INDEX `idx_doc_file_code` ON `doc_file_info` (`doc_code`);
CREATE INDEX `idx_doc_version_file` ON `doc_version_his` (`file_id`);
CREATE INDEX `idx_doc_permission_resource` ON `doc_permission_info` (`resource_type`, `resource_id`);
CREATE INDEX `idx_doc_permission_target` ON `doc_permission_info` (`target_type`, `target_id`);

-- 文档权限目标说明：doc_permission_info.target_type = USER/ROLE/DEPT，target_id 使用现有系统表 ID。
