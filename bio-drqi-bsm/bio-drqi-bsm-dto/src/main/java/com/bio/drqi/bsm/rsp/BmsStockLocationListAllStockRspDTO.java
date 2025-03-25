
package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class BmsStockLocationListAllStockRspDTO {


    /**
     * 库房编号
     */
    private String stockCode;

    /**
     * 库房名称
     */
    private String stockName;


}
