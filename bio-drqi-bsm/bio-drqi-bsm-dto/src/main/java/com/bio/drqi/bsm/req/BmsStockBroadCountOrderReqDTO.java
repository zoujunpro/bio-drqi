package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsStockBroadCountOrderReqDTO extends PageDTO {

    private String countType;

    private String beginDateTime;

    private String endDateTime;

    private String productInnerCode;

    private String productCategoryCode;

    private String applyUnitCode;

    private String reportFlag;
}
