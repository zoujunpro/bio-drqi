package com.bio.drqi.es.dto;

import lombok.Data;

import java.util.Map;

/**
 * ES 单条数据一致性检测结果。
 */
@Data
public class EsDocCheckDTO {

    private String tableName;

    private String bizId;

    private String tableIndexName;

    private String tableDocId;

    private String globalIndexName;

    private String globalDocId;

    private Boolean dbExists;

    private Boolean tableIndexExists;

    private Boolean globalIndexExists;

    private Boolean tableDocExists;

    private Boolean globalDocExists;

    private Boolean tableConsistent;

    private Boolean globalConsistent;

    private String expectedHash;

    private String tableDocHash;

    private String globalDocHash;

    private EsSyncRecordDTO lastSyncRecord;

    private Map<String, Object> expectedDoc;

    private Map<String, Object> tableDoc;

    private Map<String, Object> globalDoc;
}
