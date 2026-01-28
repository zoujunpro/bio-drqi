package com.bio.drqi.bsm.kd.enums;

/**
 * 合同类型映射
 */
public enum ProjectTypeEnum {
    YF("YF", "YFXM"),
    CRO("CRO", "HTXM"),
    GOV("GOV", "ZFXM"),
    ;
    private String projectType;
    private String fId;

    ProjectTypeEnum(String projectType, String fId) {
        this.projectType = projectType;
        this.fId = fId;
    }

    public static String queryFIDByProjectType(String projectType) {
        for (ProjectTypeEnum projectTypeEnum : ProjectTypeEnum.values()) {
            if (projectTypeEnum.projectType.equals(projectType)) {
                return projectTypeEnum.fId;
            }
        }
        return null;
    }
}
