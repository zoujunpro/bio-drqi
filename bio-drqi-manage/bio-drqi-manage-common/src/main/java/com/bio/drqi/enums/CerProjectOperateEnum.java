package com.bio.drqi.enums;

public enum CerProjectOperateEnum {
    project_add("新增项目"),
    project_edit("编辑项目"),
    project_delete("删除项目"),
    project_complete("完成项目"),
    project_disable("禁用项目"),
    project_enable("启用项目"),

    vector_add("载体新增"),
    vector_edit("载体编辑"),

    plasmid_edit("质粒质检"),


    transform_add("新增转化"),
    transform_edit("编辑转化"),


    applySample("发起取样申请"),
    closeApply("关闭取样申请"),

    sampleDataListPage("取样数据上传"),

    uploadSampleTestData("检测数据上传"),


    ;
    public String desc;

    CerProjectOperateEnum( String desc ) {
        this.desc=desc;
    }
}
