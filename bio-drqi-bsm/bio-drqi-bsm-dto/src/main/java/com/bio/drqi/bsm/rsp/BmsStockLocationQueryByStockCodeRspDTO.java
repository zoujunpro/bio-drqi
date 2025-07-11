package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.Date;
@Data
public class BmsStockLocationQueryByStockCodeRspDTO {
    private Integer id;

    /**
     * 单位编号
     */
    private String unitCode;

    /**
     * 库房编号
     */
    private String stockCode;

    /**
     * 库房名称
     */
    private String stockName;

    /**
     * 库位号
     */
    private String locationNumber;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人名称
     */
    private String createUserName;

    /**
     * 创还时间
     */
    private Date createTime;
}
