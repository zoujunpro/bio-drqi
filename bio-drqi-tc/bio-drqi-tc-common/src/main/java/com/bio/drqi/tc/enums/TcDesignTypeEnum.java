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

    public static TcDesignTypeEnum getByName(String name) {
        for (TcDesignTypeEnum item : values()) {
            if (item.name.equals(name)) {
                return item;
            }
        }
        return null;
    }
}
