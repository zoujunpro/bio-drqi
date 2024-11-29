package com.bio.drqi.enums;

public enum VectorTaskPlanEventTypeEnum {
    code_1("vector_build", "载体构建时间预估"),
    code_2("plasmid_check", "质粒质检时间预估"),
    code_3("transform", "转化时间预估"),
    code_4("sample_and_test", "取样检测时间预估"),
    code_5("cer_plant", "T0收种时间预估"),
    code_6("cer_plant_t3", "T3代时间预估"),
    ;
    private String code;
    private String desc;

    VectorTaskPlanEventTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getDescByCode(String code) {
        for (VectorTaskPlanEventTypeEnum vectorTaskPlanEventTypeEnum : VectorTaskPlanEventTypeEnum.values()) {
            if (vectorTaskPlanEventTypeEnum.code.equals(code)) {
                return vectorTaskPlanEventTypeEnum.desc;
            }
        }
        return null;
    }
}
