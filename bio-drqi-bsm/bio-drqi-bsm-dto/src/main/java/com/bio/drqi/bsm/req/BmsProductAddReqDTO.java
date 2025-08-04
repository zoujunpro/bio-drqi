package com.bio.drqi.bsm.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BmsProductAddReqDTO {
    /**
     * 商品名称
     */
    @NotBlank(message = "材料名称必填")
    private String productName;

    /**
     * 商品外部编号
     */
    private String productOutCode;
    /**
     * 商品类别编号
     */
    @NotBlank(message = "材料类别必填")
    private String productCategoryCode;

    /**
     * 品牌编号
     */
    @NotBlank(message = "品牌必填")
    private String brandCode;
    /**
     * 商品规格
     */
    @NotBlank(message = "规格必填")
    private String productSpecs;

}
