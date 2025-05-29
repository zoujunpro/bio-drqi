package com.bio.drqi.tc.enums;

public enum ExperimentStatusEnum {
    INIT("1", "进行中"),
    OVER("2", "结束"),
    STOP("3", "暂停"),
    ;
    public String status;
    public String name;

    ExperimentStatusEnum(String status, String name) {
        this.status = status;
        this.name = name;
    }
}
