package com.bio.cer.enums;

public enum BioTaskStatusEnum {
    TASK_STATUS_1("1","审批中(执行中)"),
    TASK_STATUS_2("2","审批通过"),
    TASK_STATUS_3("3","审批拒绝"),
    TASK_STATUS_4("4","任务取消"),
    ;
    public String status;
    public String name;

    BioTaskStatusEnum(String status, String name) {
        this.status = status;
        this.name = name;
    }

    public static String getNameByStatus(String status){
        for (BioTaskStatusEnum projectTaskStatusEnum: BioTaskStatusEnum.values()){
            if(projectTaskStatusEnum.status.equals(status)){
                return projectTaskStatusEnum.name;
            }
        }
        return null;
    }
}
