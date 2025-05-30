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
 * @TableName tc_harvest_seed_apply_tb
 */
@TableName(value ="tc_harvest_seed_apply_tb")
@Data
public class TcHarvestSeedApplyTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 任务编号
     */
    private String taskNum;


    /**
     * 收获批次号
     */
    private String harvestApplyNum;

    /**
     * 收获时间
     */
    private String harvestTime;

    /**
     * 创建日期
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
     * 实验编号
     */
    private String experimentNum;

    /**
     * 收获文件
     */
    private String harvestFileUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}