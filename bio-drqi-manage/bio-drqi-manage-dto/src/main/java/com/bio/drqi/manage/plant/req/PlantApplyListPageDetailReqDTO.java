package com.bio.drqi.manage.plant.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@AllArgsConstructor
@NotBlank
public class PlantApplyListPageDetailReqDTO extends PageDTO {


    /**
     * PD号
     */
    private String pdImplementCode;

    /**
     * 种植申请编号
     */
    private String plantApplyNum;

    /**
     * 小区编号
     */
    private String regionNum;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 品种
     */
    private String breedCode;

    /**
     * 种植编号
     */
    private String plantCode;

    /**
     * 代次编号
     */
    private String generationCode;

    /**
     * 物种
     */
    private String speciesCode;


}
