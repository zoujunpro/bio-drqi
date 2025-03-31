package com.bio.drqi.bsm.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BmsProductAddReqDTO {
    /**
     * 商品名称
     */
    private String productName;

    /**
     * 商品外部编号
     */
    private String productOutCode;
    /**
     * 商品类别编号
     */
    private String productCategoryCode;
    /**
     * 商品类型编号
     */
    private String productTypeCode;
    /**
     * 品牌编号
     */
    private String brandCode;
    /**
     * 商品规格
     */
    private String productSpecs;

}
