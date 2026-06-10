package com.bio.drqi.tc.enums;

public enum TcDesignTypeEnum {
    SURVIVAL_COMPETITION("生存竞争类"),
    EVALUATION("评价类"),
    HYBRID("杂交类"),
    ;

    public String name;

    TcDesignTypeEnum(String name) {
        this.name = name;
    }

    public static TcDesignTypeEnum getByCode(String code) {
        for (TcDesignTypeEnum item : values()) {
            if (item.name().equals(code)) {
                return item;
            }
        }
        return null;
    }
}
