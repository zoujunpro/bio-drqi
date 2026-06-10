package com.bio.drqi.tc.enums;

public enum TcDesignTypeEnum {
    SURVIVAL_COMPETITION("生存竞争类"),
    EVALUATION("评价类"),
    HYBRID("杂交类"),
    ;

    public String code;
    public String name;

    TcDesignTypeEnum(String name) {
        this.code = name();
        this.name = name;
    }

    private static TcDesignTypeEnum getByCode(String code) {
        for (TcDesignTypeEnum item : values()) {
            if (item.code.equals(code)) {
                return item;
            }
        }
        return null;
    }

    private static TcDesignTypeEnum getByName(String name) {
        for (TcDesignTypeEnum item : values()) {
            if (item.name.equals(name)) {
                return item;
            }
        }
        return null;
    }

    public static TcDesignTypeEnum getDesignTypeEnum(String designType) {
        TcDesignTypeEnum designTypeEnum = TcDesignTypeEnum.getByCode(designType);
        return designTypeEnum == null ? TcDesignTypeEnum.getByName(designType) : designTypeEnum;
    }
}
