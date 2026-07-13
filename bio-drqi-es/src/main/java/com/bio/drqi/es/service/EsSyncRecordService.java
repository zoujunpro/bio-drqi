package com.bio.drqi.es.service;

import com.bio.drqi.es.dto.EsSyncRecordDTO;
import com.bio.drqi.es.enums.EsSyncRecordStageEnum;
import com.bio.drqi.es.enums.EsSyncRecordStatusEnum;

import java.util.Map;

public interface EsSyncRecordService {

    String start(String tableName, String bizId, String operationType, String sourceType);

    void finish(String traceId,
                EsSyncRecordStatusEnum status,
                EsSyncRecordStageEnum stage,
                String tableIndexName,
                String tableDocId,
                String globalIndexName,
                String globalDocId,
                String docHash,
                String errorMessage);

    EsSyncRecordDTO findLatest(String tableName, String bizId);

    String hash(Map<String, Object> doc);
}
