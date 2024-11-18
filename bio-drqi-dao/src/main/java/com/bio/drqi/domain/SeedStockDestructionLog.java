package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 
 * @TableName seed_destruction_log
 */
@TableName(value ="seed_stock_destruction_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeedStockDestructionLog implements Serializable {
    /**
     * 
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 销毁地点
     */
    private String destructionLocation;

    /**
     * 种子号
     */
    private String seedNum;

    /**
     * 销毁方法
     */
    private String destructionMethod;

    /**
     * 单位
     */
    private String unit;

    /**
     * 数量
     */
    private BigDecimal seedNumber;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 申请任务编号
     */
    private String taskNum;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 销毁证据
     */
    private String destructionEvidence;


    private Date destructionTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}