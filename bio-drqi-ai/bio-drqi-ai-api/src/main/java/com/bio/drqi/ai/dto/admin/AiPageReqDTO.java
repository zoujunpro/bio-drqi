package com.bio.drqi.ai.dto.admin;

import lombok.Data;

@Data
public class AiPageReqDTO {

    private Long pageNum = 1L;

    private Long pageSize = 20L;

    private String keyword;

    private String intent;

    private String domain;

    private String apiCode;

    private String serviceName;

    private String riskLevel;

    private Integer enabled;

    private Integer aiEnabled;

    private Integer readOnly;

    private Integer required;

    private String scenario;

    private String success;
}
