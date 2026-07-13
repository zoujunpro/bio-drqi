package com.bio.drqi.es.service;

import com.bio.drqi.es.dto.EsIndexMonitorStatusDTO;
import com.bio.drqi.es.dto.EsIndexMonitorTaskPageDTO;
import com.bio.drqi.es.dto.EsDocCheckDTO;

import java.util.List;

public interface EsIndexMonitorService {

    List<EsIndexMonitorStatusDTO> listStatus();

    EsIndexMonitorStatusDTO check(String indexCode, String operatorId, String operatorName);

    EsIndexMonitorStatusDTO rebuild(String indexCode, String operatorId, String operatorName);

    EsIndexMonitorTaskPageDTO listTasks(String indexCode, String taskType, String status, int pageNum, int pageSize);

    EsDocCheckDTO checkDoc(String tableName, String bizId);
}
