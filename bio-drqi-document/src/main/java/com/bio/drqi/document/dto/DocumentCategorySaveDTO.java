package com.bio.drqi.document.dto;

import lombok.Data;

@Data
public class DocumentCategorySaveDTO {

    private Long id;

    private Long parentId;

    private String categoryName;

    private String categoryCode;

    private String categoryType;

    private Integer sortNum;

    private Long managerUserId;

    private String managerScope;

    private String inheritPermission;
}
