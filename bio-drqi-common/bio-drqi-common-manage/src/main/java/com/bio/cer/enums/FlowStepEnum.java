package com.bio.cer.enums;

public enum FlowStepEnum {
    project_create("项目立项"),
    implementation_plan("实施方案"),
    sub_project_create("子项目构建"),
    vector_build("载体构建"),
    plasmid_check("质粒质检"),
    transfrom("转化再生"),
    sample_test("取样检测"),
    cer_seed("cer种植"),
    conversion_and_trans("转化移苗"),
    protoplast_reservation("原生质体预约"),
    ;
    public String desc;

    FlowStepEnum(String desc) {
        this.desc = desc;
    }


    public static String getFlowStepNameByCode(String code) {
        for (FlowStepEnum flowStepEnum : FlowStepEnum.values()) {
            if (flowStepEnum.name().equals(code)) {
                return flowStepEnum.desc;
            }
        }
        return null;
    }

}
