package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName seed_in_store_log
 */
@TableName(value ="seed_stock_in_log")
@Data
public class SeedStockInLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 上级种子编号
     */
    private String seedNum;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 计量单位g/kg/粒
     */
    private String unit;

    /**
     * 种子数量
     */
    private BigDecimal seedNumber;

    /**
     * 种子来源（CER/温室/大田/外单位）
     */
    private String sourceType;

    /**
     * 申请任务编号
     */
    private String taskNum;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人姓名
     */
    private String applyUserName;

    private Date createTime;

    @TableField(exist = false)
    private SeedStockTb seedStockTb;

    private String uniqueCode;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}