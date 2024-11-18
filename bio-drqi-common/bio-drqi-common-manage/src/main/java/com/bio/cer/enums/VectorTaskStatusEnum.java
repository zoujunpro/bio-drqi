package com.bio.cer.enums;

public enum VectorTaskStatusEnum {
    TASK_STATUS_1("1","审批中"),
    TASK_STATUS_2("2","执行中（审批通过）"),
    TASK_STATUS_3("3","终止（审批拒绝）"),
    TASK_STATUS_4("4","暂停"),
    TASK_STATUS_5("5","完成"),
    ;
    public String status;
    public String name;

    VectorTaskStatusEnum(String status, String name) {
        this.status = status;
        this.name = name;
    }

    public static String getNameByStatus(String status){
        for (VectorTaskStatusEnum projectTaskStatusEnum: VectorTaskStatusEnum.values()){
            if(projectTaskStatusEnum.status.equals(status)){
                return projectTaskStatusEnum.name;
            }
        }
        return null;
    }
}
