package com.bio.drqi.es.enums;

/**
 * ES 索引任务状态。
 */
public enum EsIndexTaskStatusEnum {

    RUNNING("RUNNING", "执行中"),

    SUCCESS("SUCCESS", "成功"),

    FAILED("FAILED", "失败"),

    PARTIAL_SUCCESS("PARTIAL_SUCCESS", "部分成功");

    private final String code;

    private final String desc;

    EsIndexTaskStatusEnum(String code, String desc) {
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
