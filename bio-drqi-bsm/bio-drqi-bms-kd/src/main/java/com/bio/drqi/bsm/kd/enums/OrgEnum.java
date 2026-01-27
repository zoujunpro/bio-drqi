package com.bio.drqi.bsm.kd.enums;

import com.bio.common.core.dto.BusinessException;

public enum OrgEnum {

    DEV_BEIJING_ORG("dev", "beijing", "1002"),
    DEV_TIANJIN_ORG("dev", "tianjin", "1003"),
    DEV_DEFAULT_ORG("dev", "default_", "1001"),


    TEST_BEIJING_ORG("test", "beijing", "1002"),
    TEST_TIANJIN_ORG("test", "tianjin", "1003"),
    TEST_DEFAULT_ORG("test", "default_", "1001"),



    LOCAL_BEIJING_ORG("local", "beijing", "1002"),
    LOCAL_TIANJIN_ORG("local", "tianjin", "1003"),
    LOCAL_DEFAULT_ORG("local", "default_", "1001"),

    PROD_TIANJIN_ORG("prod", "tianjin", "1002"),
    PROD_BEIJING_ORG("prod", "beijing", "1003"),
    PROD_DEFAULT_ORG("prod", "default_", "1001"),
    ;

    public String active;
    public String unitCode;
    public String orgCode;

    OrgEnum(String active, String unitCode, String orgCode) {
        this.active = active;
        this.unitCode = unitCode;
        this.orgCode = orgCode;
    }

    public static String getOrgByActiveAndUnitCode(String active, String unitCode) {
        for (OrgEnum orgEnum : OrgEnum.values()) {
            if (orgEnum.active.equals(active) && orgEnum.unitCode.equals(unitCode)) {
                return orgEnum.orgCode;
            }
        }
        throw new BusinessException("未配置齐禾和金蝶之间单位转换");
    }

}
