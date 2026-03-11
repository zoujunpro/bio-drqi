package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class BmsProductListPageRspDTO {
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
     * 商品类别编号
     */
    private String productCategoryCode;

    private String productCategoryName;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌编号
     */
    private String brandName;

    /**
     * 商品规格
     */
    private String productSpecs;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 删除标识
     */
    private String productStatus;
    /**
     * 图片
     */
    private String pictureUrls;

    private String kdNumber;

    private String purchaseTypeCode;
}
