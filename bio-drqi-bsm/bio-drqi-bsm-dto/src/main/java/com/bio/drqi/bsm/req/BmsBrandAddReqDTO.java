package com.bio.drqi.bsm.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BmsBrandAddReqDTO {
    /**
     * 品牌名称
     */
    private String brandName;
}
