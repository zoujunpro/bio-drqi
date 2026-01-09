package com.bio.drqi.bsm.req;

import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

@Data
public class BmsStockPeriodCountListPageReqDTO  extends PageDTO {

    private String beginDateTime;

    private String endDateTime;

    private String productInnerCode;

    private String productCategoryCode;

    private String unitCode;
}
