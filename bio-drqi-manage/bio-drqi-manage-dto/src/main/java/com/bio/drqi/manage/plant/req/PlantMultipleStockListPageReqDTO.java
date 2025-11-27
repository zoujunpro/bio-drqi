package com.bio.drqi.manage.plant.req;

import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class PlantMultipleStockListPageReqDTO extends PageDTO {
    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 来源
     */
    private String sourceCode;



    /**
     * 工单编号(试验编号或者移苗编号)
     */
    private String taskNum;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 品种
     */
    private String breedCode;


    /**
     * 小区编号
     */
    private String regionNum;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * PD编号
     */
    private String pdImplementCode;


    private String stockNumberNotNullFlag;
}
