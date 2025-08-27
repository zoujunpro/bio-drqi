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
 * @TableName bms_move_order_detail_tb
 */
@TableName(value ="bms_move_order_detail_tb")
@Data
public class BmsMoveOrderDetailTb implements Serializable {
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
     * 单位
     */
    private String unitCode;

    /**
     * 供应商名称
     */
    private String supplierName;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 商品内部编号
     */
    private String productInnerCode;

    /**
     * 生产日期
     */
    private String produceDate;

    /**
     * 过期时间
     */
    private String expirationDate;

    /**
     * from库房编号
     */
    private String fromStockCode;

    /**
     * from库存位置编号
     */
    private String fromStockLocationNumber;

    /**
     * to库房编号
     */
    private String toStockCode;

    /**
     * to库存位置编号
     */
    private String toStockLocationNumber;

    /**
     * 移库数量
     */
    private Integer moveNumber;

    /**
     * 创建人
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

    private String uniqueCode;

    private String kdNumber;

    @TableField(exist = false)
    private String startDate;

    @TableField(exist = false)
    private String endDate;



    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}