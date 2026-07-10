package com.bio.drqi.document.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 文档编号流水表
 * @TableName doc_code_sequence
 */
@TableName(value = "doc_code_sequence")
@Data
public class DocCodeSequence implements Serializable {
    @TableId(type = IdType.AUTO)
    private Long id;

    private String categoryCode;

    private String yearMonth;

    private Integer currentSeq;

    private Date createTime;

    private Date updateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
