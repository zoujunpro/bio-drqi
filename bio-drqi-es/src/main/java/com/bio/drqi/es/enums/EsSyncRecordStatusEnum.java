package com.bio.drqi.es.enums;

import lombok.Getter;

@Getter
public enum EsSyncRecordStatusEnum {

    SUCCESS("SUCCESS", "成功"),
    FAILED("FAILED", "失败"),
    SKIPPED("SKIPPED", "跳过");

    private final String code;
    private final String desc;

    EsSyncRecordStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
