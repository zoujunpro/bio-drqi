package com.bio.drqi.bsm.enums;

public enum PayTypeEnum {
    TYPE_1("1", "公对公"),
    TYPE_2("2", "第三方付款"),
    TYPE_3("3", "线下付款"),

    ;
    public String type;
    public String name;

    PayTypeEnum(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public static String getNameByName(String type) {
        for (PayTypeEnum payTypeEnum : PayTypeEnum.values()) {
            if (type.equals(payTypeEnum.type)) {
                return payTypeEnum.name;
            }
        }
        return null;
    }
}
