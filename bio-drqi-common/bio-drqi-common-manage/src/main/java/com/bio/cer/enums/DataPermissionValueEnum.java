package com.bio.cer.enums;

public enum DataPermissionValueEnum {
    ALL(1),
    OWNER(2),
    ;
    public Integer value;

    DataPermissionValueEnum(Integer value) {
        this.value = value;
    }
}
