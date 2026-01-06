package com.bio.drqi.bsm.req;

import lombok.Data;

@Data
public class BmsStockBroadCountStockReqDTO {

   private String countType;

   private String dateTime;

   private String productInnerCode;

   private String productCategoryCode;

   private String unitCode;

}
