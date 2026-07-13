package com.bio.drqi.es.dto;

import lombok.Data;

/**
 * ES 索引监控状态。
 */
@Data
public class EsIndexMonitorStatusDTO {

    private Long id;

    private String indexCode;

    private String indexName;

    private String systemCode;

    private String businessCode;

    private String tableName;

    private String status;

    private Long esDocCount;

    private Long dbDocCount;

    private Long diffCount;

    private String clusterStatus;

    private String lastTaskId;

    private String lastErrorMessage;

    private String lastSyncTime;

    private String lastCheckTime;

    private String createTime;

    private String updateTime;
}
