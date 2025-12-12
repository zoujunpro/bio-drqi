package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class BmsBrandListPageRspDTO {

    private Integer id;

    /**
     * 品牌编号
     */
    private String brandCode;

    /**
     * 品牌名称
     */
    private String brandName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 创建人iD
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    private String brandStatus;

    private String kdNumber;
}
