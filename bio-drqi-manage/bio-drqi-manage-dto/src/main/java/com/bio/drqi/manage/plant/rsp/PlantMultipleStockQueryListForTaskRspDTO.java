package com.bio.drqi.manage.plant.rsp;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class PlantMultipleStockQueryListForTaskRspDTO {



    @Data
    public static  class PlantMultipleStockQueryListForProjectTaskRspDTO extends PlantMultipleStockQueryListForTaskRspDTO {

        public String vectorTaskCode;

        private List<PlantMultipleStockDTO> plantMultipleStockDTOList;

    }

    @Data
    public static class PlantMultipleStockQueryListForCerTaskRspDTO extends PlantMultipleStockQueryListForTaskRspDTO {

        public String regionMum;

        private List<PlantMultipleStockDTO> plantMultipleStockDTOList;

    }

    @Data
    public static class PlantMultipleStockDTO{
        private Integer id;

        /**
         * 种子编号
         */
        private String seedNum;

        /**
         * 转化编号
         */
        private String transformCode;

        /**
         * 代次
         */
        private String generation;

        /**
         * 数量
         */
        private Integer plantNumber;

        /**
         * 来源
         */
        private String sourceCode;

        /**
         * 备注
         */
        private String remark;

        /**
         * 创建人
         */
        private Date createTime;

        /**
         * 创建人
         */
        private Integer createUserId;

        /**
         * 创建人名称
         */
        private String createUserName;

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
         * 取样数量
         */
        private Integer sampleNumber;

        /**
         * 剩余数量
         */
        private Integer currentNumber;

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
        private String pdNum;

    }
}
