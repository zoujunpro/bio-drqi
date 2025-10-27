package com.bio.drqi.enums;

public enum SampleResultFileTypeENum {
    TYPE_1("1","一代测序"),
    TYPE_2("2","NGS测序")
    ;
    public String code;
    public String name;

    SampleResultFileTypeENum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
