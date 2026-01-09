package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import java.io.Serializable;
import lombok.Data;

/**
 * 
 * @TableName bms_product_stock_period_count_tb
 */
@TableName(value ="bms_product_stock_period_count_tb")
@Data
public class BmsProductStockPeriodCountTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品外部编号
     */
    private String productOutCode;

    /**
     * 所属类别编号
     */
    private String productCategoryCode;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 商品规格
     */
    private String productSpecs;

    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 单位
     */
    private String unitCode;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 商品内部编号
     */
    private String productInnerCode;

    /**
     * 唯一编号
     */
    private String uniqueCode;

    /**
     * 库房编号
     */
    private String stockCode;

    /**
     * 期初数据
     */
    private Integer periodBeginNumber;

    /**
     * 期末数据
     */
    private Integer periodEndNumber;

    /**
     * 入库数量
     */
    private Integer totalInNumber;

    /**
     * 出库数量
     */
    private Integer totalOutNumber;

    /**
     * 期数
     */
    private String periodTime;

    /**
     * 退货总数量
     */
    private Integer returnNumber;

    /**
     * 调入数量
     */
    private Integer moveInNumber;

    /**
     * 调出数量
     */
    private Integer moveOutNumber;

    @TableField(exist = false)
    private String beginDateTime;

    @TableField(exist = false)
    private String endDateTime;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}