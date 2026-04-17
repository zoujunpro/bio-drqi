package com.bio.drqi.enums;

public enum GeneEditTypeEnum {
    TYPE_1("基因编辑","1"),
    TYPE_2("转基因","2"),
    TYPE_3("育种","3"),
    ;
    public String code;
    public String name;
    GeneEditTypeEnum(String name,String code) {
        this.code = code;
        this.name=name;
    }
}
