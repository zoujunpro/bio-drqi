package com.bio.drqi.ai.dto.chat;

import lombok.Data;

import java.io.Serializable;

/**
 * AI 聊天附件结果。
 */
@Data
public class AiChatAttachmentDTO implements Serializable {

    /**
     * 文件名。
     */
    private String fileName;

    /**
     * 文件类型：EXCEL/WORD/PDF/IMAGE/OTHER。
     */
    private String fileType;

    /**
     * 下载地址。
     */
    private String downloadUrl;

    /**
     * 文件大小。
     */
    private Long fileSize;

    /**
     * 文件业务 ID，可为空。
     */
    private String fileId;

    private static final long serialVersionUID = 1L;
}
