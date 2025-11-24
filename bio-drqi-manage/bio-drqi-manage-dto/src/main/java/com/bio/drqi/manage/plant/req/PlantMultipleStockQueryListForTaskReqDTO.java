package com.bio.drqi.manage.plant.req;

import lombok.Data;

@Data
public class PlantMultipleStockQueryListForTaskReqDTO {


    /**
     * 来源
     */
    private String sourceCode;

    /**
     * 物种
     */
    private String speciesCode;

}
