package com.bio.drqi.bsm.kd.enums;

import com.bio.common.core.dto.BusinessException;

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

    public static KdFCategoryIDEnum valueOfName(String code) {
        for (KdFCategoryIDEnum kdFCategoryIDEnum : KdFCategoryIDEnum.values()) {
            if (kdFCategoryIDEnum.name().equals(code)) {
                return kdFCategoryIDEnum;
            }
        }
        throw new BusinessException("存货类别配置错误");
    }


    KdFCategoryIDEnum(String desc) {
        this.desc = desc;
    }


}
