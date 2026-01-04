package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsProductStockOutLogListPageRspDTO {

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
     * 商品内部编号
     */
    private String productInnerCode;

    /**
     * 所属类别编号
     */
    private String productCategoryCode;
    /**
     * 所属类别编号
     */
    private String productCategoryName;

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
     * 出库数量
     */
    private Integer outNumber;

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
     * 出库备注
     */
    private String remark;

    /**
     * 出库类型 1正常出库 2退货出库
     */
    private String outType;

    /**
     * 单位编号
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
     * 唯一编号
     */
    private String uniqueCode;

    private String produceDate;

    private String expirationDate;

    private String stockCode;

    private String stockName;

    private Integer kdNumber;

    private BigDecimal productPrice;

    private BigDecimal outAmount;
}
