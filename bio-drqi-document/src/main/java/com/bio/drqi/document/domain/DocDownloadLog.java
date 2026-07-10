package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文档下载日志表
 * @TableName doc_download_log
 */
@TableName(value ="doc_download_log")
@Data
public class DocDownloadLog implements Serializable {
    /**
     * 下载记录ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档ID
     */
    private Long fileId;

    /**
     * 版本ID
     */
    private Long versionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * IP地址
     */
    private String ipAddress;

    /**
     * 浏览器信息
     */
    private String userAgent;

    /**
     * 下载时间
     */
    private Date downloadTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}