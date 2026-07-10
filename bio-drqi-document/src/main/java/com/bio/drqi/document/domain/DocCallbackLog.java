package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * OnlyOffice 回调处理日志。
 * @TableName doc_callback_log
 */
@TableName(value = "doc_callback_log")
@Data
public class DocCallbackLog implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long fileId;
    private Long versionId;
    private Integer callbackStatus;
    private String callbackPayload;
    private String downloadUrl;
    private String result;
    private String failReason;
    private Integer retryCount;
    private Date createTime;
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
