package com.bio.drqi.es.dto;

import lombok.Data;

/**
 * ES 单条同步审计记录。
 */
@Data
public class EsSyncRecordDTO {

    private Long id;

    private String traceId;

    private String tableName;

    private String bizId;

    private String operationType;

    private String sourceType;

    private String status;

    private String stage;

    private String tableIndexName;

    private String tableDocId;

    private String globalIndexName;

    private String globalDocId;

    private String errorMessage;

    private String docHash;

    private Long costMs;

    private String createTime;
}
