package com.bio.drqi.manage.plant.rsp;

import lombok.Data;

@Data
public class PlantBoardCountRspDTO {

    /**
     * 种植库总苗数
     */
    private Integer plantCountNumber;
    /**
     * 临时库总苗数
     */
    private Integer tempPlantCountNumber;
    /**
     * 临时库取样苗数
     */
    private Integer  tempSampleCountNumber;
    /**
     * 临时库未取样苗数
     */
    private Integer  tempNoSampleCountNumber;

}
