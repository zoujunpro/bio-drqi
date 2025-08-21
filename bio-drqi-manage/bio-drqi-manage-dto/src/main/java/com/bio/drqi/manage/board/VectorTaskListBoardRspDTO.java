package com.bio.drqi.manage.board;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class VectorTaskListBoardRspDTO {

    private Integer projectId;
    private String projectCode;
    private Integer vectorTaskId;
    private String vectorTaskCode;
    private List<Step> stepList = new ArrayList<>();

    @Data
    private static class Step {

        /**
         * 步骤
         */
        private String stepCode;

        private String stepName;
        /**
         * 显示 Y亮 N不亮
         */
        private String showFlag;

        public Step(String stepCode, String stepName, String showFlag) {
            this.stepCode = stepCode;
            this.stepName = stepName;
            this.showFlag = showFlag;
        }

    }


    public void buildStepList(String stepCode, String stepName, String showFlag) {
        Step step = new Step(stepCode, stepName, showFlag);
        this.stepList.add(step);
    }

}
