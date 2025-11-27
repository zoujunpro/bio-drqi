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
 * @TableName plant_apply_tb
 */
@TableName(value ="plant_apply_tb")
@Data
public class PlantApplyTb implements Serializable {
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
     * 种植目标
     */
    private String plantTarget;

    /**
     * 种植明细
     */
    private String plantDetailUrl;

    /**
     * 附件地址
     */
    private String fileUrl;

    /**
     * 种植申请编号
     */
    private String plantApplyNum;

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


    private String pdNums;

    private String sampleCodePrefix;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}