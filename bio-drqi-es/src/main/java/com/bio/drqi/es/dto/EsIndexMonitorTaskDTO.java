package com.bio.drqi.es.dto;

import lombok.Data;

/**
 * ES 索引任务记录。
 */
@Data
public class EsIndexMonitorTaskDTO {

    private Long id;

    private String taskId;

    private String indexCode;

    private String indexName;

    private String taskType;

    private String status;

    private Long totalCount;

    private Long successCount;

    private Long failCount;

    private Long costMs;

    private String errorMessage;

    private String operatorId;

    private String operatorName;

    private String startTime;

    private String endTime;

    private String createTime;
}
