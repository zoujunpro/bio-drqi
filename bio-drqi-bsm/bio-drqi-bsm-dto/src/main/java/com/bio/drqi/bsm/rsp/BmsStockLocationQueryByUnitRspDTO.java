package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.util.List;

@Data
public class BmsStockLocationQueryByUnitRspDTO {

    private String stockName;

    private String stockCode;

    private List<String> stockLocationNumber;


}
