package com.bio.drqi.es.enums;

import lombok.Getter;

@Getter
public enum EsSyncRecordSourceEnum {

    CANAL("CANAL", "Canal增量同步"),
    MANUAL_CHECK("MANUAL_CHECK", "人工单条检测");

    private final String code;
    private final String desc;

    EsSyncRecordSourceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
