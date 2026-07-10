package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 文档分享对象关联表
 * @TableName doc_share_target_rel
 */
@TableName(value ="doc_share_target_rel")
@Data
public class DocShareTargetRel implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 分享ID
     */
    private Long shareId;

    /**
     * 分享对象类型：USER/ROLE/DEPT
     */
    private String targetType;

    /**
     * 分享对象ID
     */
    private Long targetId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}