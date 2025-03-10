package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class BmsProductCategoryListPageRspDTO {
    private Integer id;

    /**
     * 商品类型名称
     */
    private String productCategoryName;

    /**
     * 商品类别编号
     */
    private String productCategoryCode;

    /**
     * 创建时间
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
}
