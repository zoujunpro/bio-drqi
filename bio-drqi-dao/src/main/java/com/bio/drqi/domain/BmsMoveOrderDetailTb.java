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
 * @TableName bms_move_order_detail_tb
 */
@TableName(value ="bms_move_order_detail_tb")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
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


    private String kdNumber;



    private BigDecimal moveAmount;


    private BigDecimal productPrice;

    @TableField(exist = false)
    private String startDate;

    @TableField(exist = false)
    private String endDate;

    @TableField(exist = false)
    private String countType;

    @TableField(exist = false)
    private String beginDateTime;

    @TableField(exist = false)
    private String endDateTime;

    @TableField(exist = false)
    private String dateTime;


    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}