package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsMoveOrderDetailListPageRspDTO {

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
     * from库房
     */
    private String fromStockName;

    /**
     * from库存位置编号
     */
    private String fromStockLocationNumber;

    /**
     * to库房编号
     */
    private String toStockCode;

    /**
     * to库房
     */
    private String toStockName;

    /**
     * to库存位置编号
     */
    private String toStockLocationNumber;

    /**
     * 移库数量
     */
    private BigDecimal moveNumber;

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

    private BigDecimal productPrice;

    private BigDecimal moveAmount;
}
