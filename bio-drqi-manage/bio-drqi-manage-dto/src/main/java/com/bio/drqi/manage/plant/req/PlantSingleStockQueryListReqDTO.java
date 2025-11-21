package com.bio.drqi.manage.plant.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

@Data
public class PlantSingleStockQueryListReqDTO extends PageDTO {

    /**
     * 种植编号
     */
    private String plantCode;

    /**
     * 代次
     */
    private String generation;



    /**
     * 授粉方式
     */
    private String pollinationMethod;

    /**
     * 植株状态 1正常，2异常, 3已剔除，4已收获
     */
    private String plantStatus;


    /**
     * 收获方式
     */
    private String harvestType;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 品种
     */
    private String breedCode;

    /**
     * 来源渠道 1项目，4种子库
     */
    private String sourceCode;



    /**
     * 实施方案编号
     */
    private String vectorTaskCode;
}
