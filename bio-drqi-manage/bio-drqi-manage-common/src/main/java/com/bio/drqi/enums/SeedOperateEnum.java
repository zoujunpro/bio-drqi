package com.bio.drqi.enums;

public enum SeedOperateEnum {
    in("in","入库"),
    out("out","出库"),
    ;
    public String code;
    public String desc;

    SeedOperateEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
