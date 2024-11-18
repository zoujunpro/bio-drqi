package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * @TableName seed_out_store_log
 */
@TableName(value = "seed_stock_out_log")
@Data
public class SeedStockOutLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 种子号
     */
    private String seedNum;

    /**
     * 用途code
     */
    private String useToCode;

    /**
     * 计量单位g/kg/粒
     */
    private String unit;

    /**
     * 种子数量
     */
    private BigDecimal seedNumber;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 出库时间
     */
    private Date createTime;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 任务号
     */
    private String taskNum;

    /**
     * 出库号
     */
    private String outTaskNum;

    /**
     * 用途描述
     */
    private String useToDesc;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}