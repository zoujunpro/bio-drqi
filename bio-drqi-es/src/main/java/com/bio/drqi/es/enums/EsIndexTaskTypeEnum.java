package com.bio.drqi.es.enums;

/**
 * ES 索引任务类型。
 */
public enum EsIndexTaskTypeEnum {

    FULL_BUILD("FULL_BUILD", "全量构建"),

    CHECK("CHECK", "状态检测"),

    DELETE("DELETE", "删除同步");

    private final String code;

    private final String desc;

    EsIndexTaskTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
