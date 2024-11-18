package com.bio.cer.enums;

public enum QueryTypeEnum {
    TYPE_1(1,"查询所有"),
    TYPE_2(2,"查询待办"),
    TYPE_3(3,"查询我发起"),
    TYPE_4(4,"查询已办"),
    ;
    public Integer value;

    public String name;

    QueryTypeEnum(Integer value,String name) {
        this.value = value;
        this.name=name;
    }
}
