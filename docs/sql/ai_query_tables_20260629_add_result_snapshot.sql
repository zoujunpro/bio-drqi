ALTER TABLE ai_query_context
    ADD COLUMN last_result_snapshot LONGTEXT DEFAULT NULL COMMENT '最近一次结果快照JSON，包含表头和部分数据' AFTER last_result_summary;
