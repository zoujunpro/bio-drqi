package com.bio.drqi.bsm.rsp;

import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class BmsStockInBroadCountByCategoryRspDTO {

    private String dateTime;

    private List<Content> contentList = new ArrayList<>();

    @Data
    public static class Content {
        private String productCategoryCode;
        private String productCategoryName;
        private BigDecimal countAmount;

        public Content(String productCategoryCode, String productCategoryName, BigDecimal countAmount) {
            this.productCategoryCode = productCategoryCode;
            this.productCategoryName = productCategoryName;
            this.countAmount = countAmount;
        }
    }

    public void addContent(String productCategoryCode, String productCategoryName, BigDecimal countAmount) {
        this.contentList.add(new Content(productCategoryCode, productCategoryName, countAmount));
    }


}
