package com.bio.drqi.enums;

public enum SystemUserStatusEnum {

    STATUS_0("0","可用"),
    STATUS_1("1","禁用"),
    ;
    public String status;
    public String desc;

    SystemUserStatusEnum(String status, String desc) {
        this.status = status;
        this.desc = desc;
    }
}
