package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 耗材管理项目表
 * @TableName bms_project_dict
 */
@TableName(value ="bms_project_dict")
@Data
public class BmsProjectDict implements Serializable {
    /**
     * ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 项目名称
     */
    private String projectCode;

    /**
     * 项目编号
     */
    private String projectName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人姓名
     */
    private String createUserName;

    private Integer kdNumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}