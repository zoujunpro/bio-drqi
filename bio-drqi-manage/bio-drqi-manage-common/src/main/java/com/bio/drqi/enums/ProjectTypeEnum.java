package com.bio.drqi.enums;

public enum ProjectTypeEnum {
    TYPE_1("常规项目", "1"),
    TYPE_2("自研项目", "2"),
    ;

    public String code;
    public String name;

    ProjectTypeEnum(String name, String code) {
        this.code = code;
        this.name = name;
    }
}
