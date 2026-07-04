package com.bio.drqi.ai.dto.admin;

import lombok.Data;

@Data
public class AiPageReqDTO {

    private Long pageNum = 1L;

    private Long pageSize = 20L;

    private String keyword;

    private String intent;

    private String domain;

    private Integer enabled;

    private String scenario;

    private String success;
}
