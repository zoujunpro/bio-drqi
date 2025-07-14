package com.bio.drqi.bsm.kd.enums;

import com.bio.common.core.dto.BusinessException;

public enum KdFBillTypeIDEnum {
    TYPE_1("RKD01_SYS", "标准采购入库"),
    TYPE_2("RKD_FWFY", "服务费用入库单"),
    TYPE_3("RKD_WX", "维修入库单"),
    TYPE_4("RKD_WF", "危费入库单"),

    ;
    public String name;
    public String code;

    KdFBillTypeIDEnum(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static KdFBillTypeIDEnum ofKdParentGroupEnum(KdParentGroupEnum kdParentGroupEnum) {
        switch (kdParentGroupEnum) {
            case CODE_1:
                return KdFBillTypeIDEnum.TYPE_1;
            case CODE_2:
                return KdFBillTypeIDEnum.TYPE_2;
            case CODE_3:
                return KdFBillTypeIDEnum.TYPE_3;
            case CODE_4:
                return KdFBillTypeIDEnum.TYPE_4;
            default:
                throw new BusinessException("单据类型转换异常");
        }
    }
}
