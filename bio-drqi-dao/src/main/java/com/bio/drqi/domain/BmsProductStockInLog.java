package com.bio.drqi.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import jdk.nashorn.internal.ir.annotations.Ignore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 入库记录日志表
 * @TableName bms_product_stock_in_log
 */
@TableName(value ="bms_product_stock_in_log")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BmsProductStockInLog implements Serializable {
    /**
     * 主键ID
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 子订单编号
     */
    private String orderDetailNum;


    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品内部编号
     */
    private String productInnerCode;

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
     * 商品规格
     */
    private String productSpecs;

    /**
     * 商品批次
     */
    private String batchNo;

    /**
     * 研发项目
     */
    private String projectCode;

    /**
     * 入库单价
     */
    private BigDecimal productPrice;

    /**
     * 入库数量
     */
    private BigDecimal storeNumber;

    /**
     * 入库金额
     */
    private BigDecimal storeAmount;

    /**
     * 申请人ID
     */
    private Integer applyUserId;

    /**
     * 申请人名称
     */
    private String applyUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 订单编号
     */
    private String orderNum;

    /**
     * 库存位置编号
     */
    private String stockLocationNumber;

    /**
     * 单位编号
     */
    private String unitCode;

    /**
     * 供应商编号
     */
    private String supplierCode;

    /**
     * 唯一编号
     */
    private String uniqueCode;

    private String produceDate;

    private String expirationDate;

    private String taxRate;

    private BigDecimal returnNumber;

    private String stockCode;

    private String payType;

    @TableField(exist = false)
    private String startDate;

    @TableField(exist = false)
    private String endDate;

    private String kdNumber;


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