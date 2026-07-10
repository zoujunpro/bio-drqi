package com.bio.drqi.document.dto;

import lombok.Data;

@Data
public class DocumentUploadRspDTO {

    private Long documentId;

    private Long versionId;

    private String docName;

    private String docCode;

    private String filePath;

    private String downloadUrl;
}
