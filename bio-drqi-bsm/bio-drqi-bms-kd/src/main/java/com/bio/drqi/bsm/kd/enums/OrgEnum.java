package com.bio.drqi.bsm.kd.enums;

import com.bio.common.core.dto.BusinessException;

public enum OrgEnum {
    DEV_BEIJING_ORG("dev", "beijing", "1001"),
    DEV_TIANJIN_ORG("dev", "tianjin", "1001"),
    LOCAL_BEIJING_ORG("local", "beijing", "1001"),
    LOCAL_TIANJIN_ORG("local", "tianjin", "1001"),
    PROD_TIANJIN_ORG("prod", "tianjin", ""),
    PROD_BEIJING_ORG("prod", "beijing", ""),
    PROD_DEFAULT_ORG("prod", "default", ""),
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
