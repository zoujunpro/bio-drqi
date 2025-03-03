package com.bio.drqi.enums;

public enum VectorTaskTypeEnum {
    type_1("1", "正常","A"),
    type_2("2", "瞬时测试","T"),
    type_3("3", "原生质体","P"),
    type_4("4", "发根","R"),
    ;
    public String code;
    public String name;
    public String method;

    VectorTaskTypeEnum(String code, String name, String method) {
        this.code = code;
        this.name = name;
        this.method = method;
    }

    public static String getMethodEnumByType(String taskType){
        for (VectorTaskTypeEnum vectorTaskTypeEnum:VectorTaskTypeEnum.values()){
            if(vectorTaskTypeEnum.code.equals(taskType)){
                return vectorTaskTypeEnum.method;
            }
        }
        return null;
    }
}
