package com.bio.drqi.es.enums;

import lombok.Getter;

@Getter
public enum EsSyncRecordStageEnum {

    RECEIVE("RECEIVE", "接收事件"),
    RESOLVE_BUILDER("RESOLVE_BUILDER", "匹配索引构建器"),
    DB_READ("DB_READ", "读取数据库"),
    BUILD_DOC("BUILD_DOC", "构建ES文档"),
    TABLE_INDEX_WRITE("TABLE_INDEX_WRITE", "写入业务表索引"),
    GLOBAL_INDEX_WRITE("GLOBAL_INDEX_WRITE", "写入全局索引"),
    TABLE_INDEX_DELETE("TABLE_INDEX_DELETE", "删除业务表索引文档"),
    GLOBAL_INDEX_DELETE("GLOBAL_INDEX_DELETE", "删除全局索引文档"),
    DONE("DONE", "同步完成");

    private final String code;
    private final String desc;

    EsSyncRecordStageEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }
}
