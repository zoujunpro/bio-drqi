package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 文档标签关联表
 * @TableName doc_file_tag_rel
 */
@TableName(value ="doc_file_tag_rel")
@Data
public class DocFileTagRel implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 文档ID
     */
    private Long fileId;

    /**
     * 标签ID
     */
    private Long tagId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}