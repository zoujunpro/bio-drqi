package com.bio.drqi.ai.common.enums;

/**
 * AI 文档权限主体类型。
 * 对应 MySQL 表 ai_document_permission.principal_type。
 */
public enum AiDocumentPermissionPrincipalTypeEnum {

    /**
     * 用户。principal_id 保存用户 ID。
     */
    USER("USER", "用户"),

    /**
     * 部门。principal_id 保存部门 ID。
     */
    DEPT("DEPT", "部门"),

    /**
     * 角色。principal_id 保存角色 ID。
     */
    ROLE("ROLE", "角色"),

    /**
     * 公开可读。principal_id 可为空，表示所有用户都可检索。
     */
    PUBLIC("PUBLIC", "公开可读");

    private final String code;

    private final String desc;

    AiDocumentPermissionPrincipalTypeEnum(String code, String desc) {
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
