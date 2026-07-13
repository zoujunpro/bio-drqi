package com.bio.drqi.es.dto;

import lombok.Data;

import java.util.List;

/**
 * ES 索引任务分页结果。
 */
@Data
public class EsIndexMonitorTaskPageDTO {

    private long total;

    private List<EsIndexMonitorTaskDTO> records;
}
