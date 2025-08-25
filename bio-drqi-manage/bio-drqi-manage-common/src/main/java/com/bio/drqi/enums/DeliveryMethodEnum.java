package com.bio.drqi.enums;

public enum DeliveryMethodEnum {
    METHOD_A("A","农杆菌转化"),
    METHOD_B("B","基因枪"),
    METHOD_P("P","原生质体转化"),
    METHOD_V("V","病毒载体"),
    ;
    private String name;
    public String code;

    DeliveryMethodEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
