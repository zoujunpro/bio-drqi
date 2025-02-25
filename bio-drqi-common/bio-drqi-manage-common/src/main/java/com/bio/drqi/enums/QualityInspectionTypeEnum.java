package com.bio.drqi.enums;

public enum QualityInspectionTypeEnum {
    TYPE_1("1", "质粒制备"),
    TYPE_2("2", "农杆菌转化"),
    ;
    public String type;
    public String name;

    QualityInspectionTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }
}
