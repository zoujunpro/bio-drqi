package com.bio.cer.enums;

public enum SampleTaskStatusEnum {
    STATUS_0("0", "数据审批中"),
    STATUS_1("1", "取样结果待上送"),
    STATUS_2("2", "检测结果待上送"),
    STATUS_3("3", "取样检测待审查"),
    STATUS_4("4", "取样检测处理完毕"),

    ;
    public String status;
    public String name;

    SampleTaskStatusEnum(String status, String name) {
        this.status = status;
        this.name = name;
    }

}
