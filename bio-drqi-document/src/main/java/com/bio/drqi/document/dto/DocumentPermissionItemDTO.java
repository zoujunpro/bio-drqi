package com.bio.drqi.document.dto;

import lombok.Data;

@Data
public class DocumentPermissionItemDTO {

    private String targetType;

    private Long targetId;

    private Integer canView;

    private Integer canDownload;

    private Integer canEdit;

    private Integer canShare;

    private Integer canDelete;

    private Integer canVersion;

    private Integer canPermission;
}
