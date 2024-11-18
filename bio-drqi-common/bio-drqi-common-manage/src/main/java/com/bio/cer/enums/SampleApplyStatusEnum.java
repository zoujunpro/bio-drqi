package com.bio.cer.enums;

public enum SampleApplyStatusEnum {
    APPLY_STATUS_1("1","发起中"),
    APPLY_STATUS_2("2","关闭")
    ;
    public String code;
    public String name;

    SampleApplyStatusEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
