package com.bio.drqi.manage.project.rsp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class ProjectQueryBySpeciesCodeRspDTO {

    private String projectCode;

    private String projectName;

    private List<ImplementationPlan> implementationPlanList=new ArrayList<>();


    @Data
    public static class ImplementationPlan{

        private String vectorTaskCode;

        private String vectorTaskName;

        public ImplementationPlan(String vectorTaskCode, String vectorTaskName) {
            this.vectorTaskCode = vectorTaskCode;
            this.vectorTaskName = vectorTaskName;
        }
    }

    public void addImplementationPlanToList(String vectorTaskCode, String vectorTaskName){
        this.implementationPlanList.add(new ImplementationPlan(vectorTaskCode,vectorTaskName));
    }




}
