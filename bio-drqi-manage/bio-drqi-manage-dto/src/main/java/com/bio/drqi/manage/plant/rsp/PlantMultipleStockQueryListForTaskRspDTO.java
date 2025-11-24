package com.bio.drqi.manage.plant.rsp;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PlantMultipleStockQueryListForTaskRspDTO {



    @Data
    public static  class PlantMultipleStockQueryListForProjectTaskRspDTO extends PlantMultipleStockQueryListForTaskRspDTO {

        public String vectorTaskCode;

        private List<String> transformCodeList;

    }

    @Data
    public static class PlantMultipleStockQueryListForCerTaskRspDTO extends PlantMultipleStockQueryListForTaskRspDTO {

        public String regionMum;

        private List<String> seedNumList;

    }
}
