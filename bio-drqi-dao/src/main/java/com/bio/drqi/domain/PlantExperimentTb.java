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
 * @TableName plant_experiment_tb
 */
@TableName(value ="plant_experiment_tb")
@Data
public class PlantExperimentTb implements Serializable {
    /**
     * 主键id
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 试验类型 1供试  2分离提存 3扩繁  4法规测试
     */
    private String experimentType;

    /**
     * 试验目标
     */
    private String experimentTarget;

    /**
     * 试验方案
     */
    private String designUrl;

    /**
     * 试验附件
     */
    private String fileUrl;

    /**
     * 试验编号
     */
    private String experimentNum;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 试验方案
     */
    private String vectorTaskCodes;


    private String pdNumbers;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}