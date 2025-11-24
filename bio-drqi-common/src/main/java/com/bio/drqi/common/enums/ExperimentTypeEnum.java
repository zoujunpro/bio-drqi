package com.bio.drqi.common.enums;

public enum ExperimentTypeEnum {
    CODE_1("1", "扩繁"),
    CODE_2("2", "性状测试"),
    CODE_3("3", "法规试验"),
    CODE_4("4", "品种审定/等级试验"),
    CODE_5("5", "制种"),
    ;
    public String code;
    public String desc;

    ExperimentTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
