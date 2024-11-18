package com.bio.drqi.enums;

public enum ImplementationPlanTypeEnum {
    vector_build("载体构建"),
    plasmid_check("质粒质检"),
    transform("转化再生"),
    conversion_and_trans("移苗"),
    sample_and_test("取样检测"),
    cer_plant("CER"),
    ;
    public String desc;

    ImplementationPlanTypeEnum(String desc) {
        this.desc = desc;
    }

    public static String getDesc(String implementationPlanType) {
        for (ImplementationPlanTypeEnum implementationPlanTypeEnum : ImplementationPlanTypeEnum.values()) {
            if (implementationPlanTypeEnum.name().equals(implementationPlanType)) {
                return implementationPlanTypeEnum.desc;
            }
        }
        return null;
    }
}
