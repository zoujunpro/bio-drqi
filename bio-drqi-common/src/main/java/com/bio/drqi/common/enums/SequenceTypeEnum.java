package com.bio.drqi.common.enums;

public enum SequenceTypeEnum {
    CODE_1("1","NGS测序"),
    CODE_2("2","一代测序"),
    ;
    public String  name;
    public String code;

    SequenceTypeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
