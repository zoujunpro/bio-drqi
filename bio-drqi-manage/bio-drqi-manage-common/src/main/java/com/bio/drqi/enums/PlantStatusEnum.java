package com.bio.drqi.enums;

public enum PlantStatusEnum {
    STATUS_1("1","正常"),
    STATUS_2("2","异常"),
    STATUS_3("3","已剔除"),
    STATUS_4("4","已收获"),
    ;
    public String code;
    public String desc;

    PlantStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
