package com.bio.drqi.document.dto;

import lombok.Data;

@Data
public class DocumentPageReqDTO {

    private Integer pageNum = 1;

    private Integer pageSize = 20;

    private Long categoryId;

    private String keyword;

    private String spaceType;

    private String status;
}
