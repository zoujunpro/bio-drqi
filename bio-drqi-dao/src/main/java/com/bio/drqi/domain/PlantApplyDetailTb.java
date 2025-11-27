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
 * @TableName plant_apply_detail_tb
 */
@TableName(value ="plant_apply_detail_tb")
@Data
public class PlantApplyDetailTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * PD号
     */
    private String pdImplementCode;

    /**
     * 种植申请编号
     */
    private String plantApplyNum;

    /**
     * 区域
     */
    private String regionNum;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 品种
     */
    private String breedCode;

    /**
     * 种植编号
     */
    private String plantCode;

    /**
     * 代次编号
     */
    private String generationCode;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 播种时间
     */
    private String plantTime;

    /**
     * 播种数量
     */
    private Integer plantNumber;


    /**
     * 播种单位
     */
    private String plantUnit;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 基因型
     */
    private String geneType;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}