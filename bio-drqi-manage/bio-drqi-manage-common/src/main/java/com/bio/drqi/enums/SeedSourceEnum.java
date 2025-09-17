package com.bio.drqi.enums;

public enum SeedSourceEnum {
    CODE_1("1", "CER"),
    CODE_2("2", "温室"),
    CODE_3("3", "外单位"),
    CODE_4("4", "大田"),
    CODE_5("5", "网购"),
    ;

    public String name;
    public String code;

    SeedSourceEnum(String code, String name) {
        this.name = name;
        this.code = code;
    }

    public static SeedSourceEnum getByCode(String code) {
        for (SeedSourceEnum seedSourceEnum : SeedSourceEnum.values()) {
            if (seedSourceEnum.code.equals(code)) {
                return seedSourceEnum;
            }
        }
        return null;
    }
    public static SeedSourceEnum getByName(String name) {
        for (SeedSourceEnum seedSourceEnum : SeedSourceEnum.values()) {
            if (seedSourceEnum.name.equals(name)) {
                return seedSourceEnum;
            }
        }
        return null;
    }
}
