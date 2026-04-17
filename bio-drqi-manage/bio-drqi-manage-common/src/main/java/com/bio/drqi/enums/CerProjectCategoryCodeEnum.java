package com.bio.drqi.enums;


public enum CerProjectCategoryCodeEnum {
    CODE_1("1","大田作物"),
    CODE_2("2","经济作物"),
    CODE_3("3","合成学作物"),
    CODE_4("4","其他"),
    ;
    public String  name;
    public String code;

    CerProjectCategoryCodeEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }
}
