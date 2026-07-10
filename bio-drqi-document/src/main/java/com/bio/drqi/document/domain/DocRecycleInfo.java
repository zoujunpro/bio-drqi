package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import java.util.Date;
import lombok.Data;

/**
 * 文档回收站信息表
 * @TableName doc_recycle_info
 */
@TableName(value ="doc_recycle_info")
@Data
public class DocRecycleInfo implements Serializable {
    /**
     * 回收站ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档ID
     */
    private Long fileId;

    /**
     * 删除人ID
     */
    private Long deleteUserId;

    /**
     * 删除时间
     */
    private Date deleteTime;

    /**
     * 自动清理时间
     */
    private Date expireTime;

    /**
     * 恢复时间
     */
    private Date restoreTime;

    /**
     * 状态：RECYCLED回收站 RESTORED已恢复 CLEARED已清理
     */
    private String status;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}