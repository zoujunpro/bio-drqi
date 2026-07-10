package com.bio.drqi.document.dto;

import lombok.Data;

import java.util.List;

@Data
public class DocumentPermissionSaveDTO {

    private String resourceType;

    private Long resourceId;

    private List<DocumentPermissionItemDTO> permissions;
}
