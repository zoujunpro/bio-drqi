package com.bio.drqi.bsm.dto;

import lombok.Data;

@Data
public class BmsProductInputDTO {

    private String orderDetailNum;

    /**
     * 批次号
     */
    private String batchNo;

    /**
     * 入库数量
     */
    private String number;

    /**
     * 库存编号
     */
    private String stockCode;

    /**
     * 库存位置号
     */
    private String stockLocationNumber;


}
