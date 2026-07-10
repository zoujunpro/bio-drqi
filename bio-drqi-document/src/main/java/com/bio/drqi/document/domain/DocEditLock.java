package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文档在线编辑锁。
 * @TableName doc_edit_lock
 */
@TableName(value = "doc_edit_lock")
@Data
public class DocEditLock implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long fileId;
    private Long versionId;
    private Long userId;
    private String lockToken;
    private String status;
    private Date expireTime;
    private Date createTime;
    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
