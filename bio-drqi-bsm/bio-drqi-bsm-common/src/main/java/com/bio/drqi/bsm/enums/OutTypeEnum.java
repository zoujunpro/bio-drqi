package com.bio.drqi.bsm.enums;

public enum OutTypeEnum {

    TYPE_1("1","正常出库"),
    TYPE_2("2","退货出库"),

    ;
    public String code;
    public String name;

    OutTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }
}
