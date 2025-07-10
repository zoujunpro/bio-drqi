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

/**
 * 
 * @TableName bms_product_stock_tb
 */
@TableName(value ="bms_product_stock_tb")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BmsProductStockTb implements Serializable {
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
     * 货品类型编号
     */
    private String productTypeCode;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 商品规格
     */
    private String productSpecs;

    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 累计入库数量
     */
    private Integer totalStoreNumber;

    /**
     * 当前库存数量
     */
    private Integer currentStockNumber;

    /**
     * 累计出库数量
     */
    private Integer totalOutNumber;



    /**
     * 单位
     */
    private String unitCode;

    /**
     * 库存位置编号
     */
    private String stockLocationNumber;

    private String productInnerCode;


    private String supplierCode;

    private String supplierName;

    private String uniqueCode;

    private String produceDate;

    private String expirationDate;

    private Integer returnNumber;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;


}