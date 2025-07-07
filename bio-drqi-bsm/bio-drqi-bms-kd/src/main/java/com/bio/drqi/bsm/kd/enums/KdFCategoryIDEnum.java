package com.bio.drqi.bsm.kd.enums;

public enum KdFCategoryIDEnum {
    CHLB01_SYS("原材料"),
    CHLB02_SYS("辅料"),
    CHLB03_SYS("自制半成品"),
    CHLB04_SYS("委外半成品"),
    CHLB05_SYS("产成品"),
    CHLB06_SYS("服务"),
    CHLB07_SYS("资产"),
    ;

    public String desc;

    KdFCategoryIDEnum(String desc) {
        this.desc = desc;
    }
}
