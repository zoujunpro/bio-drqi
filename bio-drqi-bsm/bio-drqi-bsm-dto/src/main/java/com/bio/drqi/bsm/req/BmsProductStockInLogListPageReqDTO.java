package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BmsProductStockInLogListPageReqDTO extends PageDTO {
    /**
     * 子订单编号
     */
    private String orderDetailNum;

    /**
     * 商品名称
     */
    private String productName;


    /**
     * 品牌名称
     */
    private String brandCode;



    /**
     * 研发项目
     */
    private String projectCode;



    /**
     * 订单编号
     */
    private String orderNum;



    /**
     * 单位编号
     */
    private String unitCode;
}
