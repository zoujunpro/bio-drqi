package com.bio.drqi.enums;

public enum ProjectOperateEnum {
    ADD_PROJECT("创建项目", "add_project"),
    EDIT_PROJECT("编辑项目", "edit_project"),
    DELETE_PROJECT("删除项目", "delete_project"),
    COMPLETE_PROJECT("完成项目", "complete_project"),
    DISABLE_PROJECT("禁用项目", "disable_project"),
    ENABLE_PROJECT("启用项目", "enable_project"),

    ADD_VECTOR("载体构建", "add_vector"),
    EDIT_VECTOR("编辑载体", "edit_vector"),
    DELETE_VECTOR("删除载体", "delete_vector"),

    EDIT_PLASMID("编辑质粒", "edit_plasmid"),

    ADD_TRANSFORM("新增转化", "add_transform"),
    EDIT_TRANSFORM("编辑转化", "edit_transform"),

    UPLOAD_SEED("上传CER种植结果", "upload_seed"),

    SAMPLE_TEST_APPLY("取样检测申请", "sample_test_apply"),
    SAMPLE_TEST_APPLY_CLOSE("取样检测申请关闭", "sample_test_apply_close"),
    SAMPLE_TEST_APPLY_DELETE("取样检测申请删除", "sample_test_apply_delete"),
    UPLOAD_SAMPLE_DATA("上传取样数据", "upload_sample_data"),
    UPLOAD_TEST_DATA("上传检测数据", "upload_test_data"),
    ;
    public String name;
    public String code;

    ProjectOperateEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static String getNameByCode(String code) {
        for (ProjectOperateEnum projectOperateEnum : ProjectOperateEnum.values()) {
            if (projectOperateEnum.code.equals(code)) {
                return projectOperateEnum.name;
            }
        }
        return null;
    }
}
