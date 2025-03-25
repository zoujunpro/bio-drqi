package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class BmsStockLocationListPageReqDTO extends PageDTO {

    private String unitCode;

    /**
     * 库房编号
     */
    private String stockCode;
    /**
     * 库位号
     */
    private String locationNumber;


}
