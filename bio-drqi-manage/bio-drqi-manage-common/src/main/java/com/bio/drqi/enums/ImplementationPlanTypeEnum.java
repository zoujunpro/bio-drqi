package com.bio.drqi.enums;

import com.alibaba.excel.util.StringUtils;

import java.util.Map;

public enum ImplementationPlanTypeEnum {
    vector_build("载体构建", 1),
    plasmid_check("质粒质检", 2),
    transform("转化再生", 3),
    //conversion_and_trans("移苗", 4),
    sample_and_test("取样检测", 4),
    cer_plant("CER", 5),
    ;
    public String desc;

    public Integer order;

    ImplementationPlanTypeEnum(String desc, Integer order) {
        this.order = order;
        this.desc = desc;
    }

    public static ImplementationPlanTypeEnum getImplementationPlanTypeEnum(String implementationPlanType) {
        for (ImplementationPlanTypeEnum implementationPlanTypeEnum : ImplementationPlanTypeEnum.values()) {
            if (implementationPlanTypeEnum.name().equals(implementationPlanType)) {
                return implementationPlanTypeEnum;
            }
        }
        return null;
    }

    public static String getDesc(String implementationPlanType) {
        if (StringUtils.isEmpty(implementationPlanType)) {
            return null;
        }
        for (ImplementationPlanTypeEnum implementationPlanTypeEnum : ImplementationPlanTypeEnum.values()) {
            if (implementationPlanTypeEnum.name().equals(implementationPlanType)) {
                return implementationPlanTypeEnum.desc;
            }
        }
        return null;
    }

}
