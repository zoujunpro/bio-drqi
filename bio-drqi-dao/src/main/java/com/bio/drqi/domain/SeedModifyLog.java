package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * @TableName seed_modify_log
 */
@TableName(value ="seed_modify_log")
@Data
public class SeedModifyLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 修改的字段
     */
    private String fieldCode;

    /**
     * 修改的字段名字
     */
    private String fieldName;

    /**
     * 旧值
     */
    private String oldFieldValue;

    /**
     * 新值
     */
    private String newFieldName;

    /**
     * 修改时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private Integer createUserId;

    /**
     * 修改人名字
     */
    private String createUserName;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}