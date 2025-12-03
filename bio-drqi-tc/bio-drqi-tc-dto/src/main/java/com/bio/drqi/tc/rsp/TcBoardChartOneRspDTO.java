package com.bio.drqi.tc.rsp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class TcBoardChartOneRspDTO {

    private String pdImplementCode;

    private String vectorTaskCode;

    private List<ExperimentType> experimentTypeList = new ArrayList<>();

    @Data
    private static class ExperimentType {
        /**
         * 步骤
         */
        private String experimentTypeCode;


        private String experimentTypeName;
        /**
         * 显示 Y亮 N不亮
         */
        private String showFlag;

        public ExperimentType(String experimentTypeCode, String experimentTypeName, String showFlag) {
            this.experimentTypeCode = experimentTypeCode;
            this.experimentTypeName = experimentTypeName;
            this.showFlag = showFlag;
        }

    }

    public void buildExperimentTypeList(String experimentTypeCode, String experimentTypeName, String showFlag) {
        experimentTypeList.add(new ExperimentType(experimentTypeCode, experimentTypeName, showFlag));

    }
}
