package com.bio.drqi.ai.toolservice.dto;

import lombok.Data;

import java.util.Map;

@Data
public class AiManageQueryReqDTO {

    private Integer pageNum;

    private Integer pageSize;

    /**
     * 通用关键词。各工具会按自身业务映射到最常用查询字段。
     */
    private String keyword;

    private String projectCode;

    private String projectName;

    private String subProjectCode;

    private String vectorTaskCode;

    private String taskNum;

    private String speciesCode;

    private String breedCode;

    private String beginDate;

    private String endDate;

    private String transformCode;

    private String sampleCode;

    private String applyNo;

    private String seedNum;

    private String plantApplyNum;

    private String pdImplementCode;

    private String status;

    private String geneEditMethod;

    private String experimentType;

    private String seedType;

    private String generation;

    private Map<String, Object> extra;

    public Integer safePageNum() {
        return pageNum == null ? 0 : pageNum;
    }

    public Integer safePageSize() {
        return pageSize == null ? 20 : pageSize;
    }
}
