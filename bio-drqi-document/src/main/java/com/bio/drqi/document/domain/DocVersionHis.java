package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文档版本历史表
 * @TableName doc_version_his
 */
@TableName(value ="doc_version_his")
@Data
public class DocVersionHis implements Serializable {
    /**
     * 版本ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档ID
     */
    private Long fileId;

    /**
     * 版本号：v1.0/v1.1/v2.0
     */
    private String versionNo;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 文件存储路径
     */
    private String filePath;

    /**
     * 文件MD5
     */
    private String fileMd5;

    /**
     * 文件SHA256
     */
    private String fileSha256;

    /**
     * 文件大小
     */
    private Long fileSize;

    /**
     * 文件类型
     */
    private String fileType;

    /**
     * 版本说明
     */
    private String changeLog;

    /**
     * 是否当前版本：1是 0否
     */
    private Integer isCurrent;

    /**
     * 创建人
     */
    private Long createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}