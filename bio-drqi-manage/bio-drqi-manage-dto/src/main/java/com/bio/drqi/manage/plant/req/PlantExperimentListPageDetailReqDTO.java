package com.bio.drqi.manage.plant.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NotBlank
public class PlantExperimentListPageDetailReqDTO extends PageDTO {


    /**
     * PD号
     */
    private String pdNum;

    /**
     * 试验编号
     */
    private String experimentNum;

    /**
     * 区域
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
