package com.bio.drqi.ai.dao.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * AI 会话消息文件。
 */
@TableName(value = "ai_message_file")
@Data
public class AiMessageFile implements Serializable {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String sessionId;

    private Long messageId;

    private String userId;

    private String fileId;

    private String fileName;

    private String fileType;

    private String mimeType;

    private Long fileSize;

    private String bucketName;

    private String objectKey;

    /**
     * 临时访问地址或内部文件地址。
     */
    private String fileUrl;

    private String parseStatus;

    private String parsedText;

    private String summary;

    private String errorMessage;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
