package com.bio.drqi.document.dto;

import lombok.Data;

@Data
public class DocumentDownloadRspDTO {

    private Long documentId;

    private Long versionId;

    private String fileName;

    private String filePath;

    private String downloadUrl;
}
