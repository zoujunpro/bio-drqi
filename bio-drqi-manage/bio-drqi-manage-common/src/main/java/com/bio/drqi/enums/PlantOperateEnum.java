package com.bio.drqi.enums;

public enum PlantOperateEnum {
    report("报备"),
    delete("删除")
    ;

    private String desc;

    PlantOperateEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
