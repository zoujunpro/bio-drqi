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

    KdFBillTypeIDEnum(String code, String name) {
        this.name = name;
        this.code = code;
    }

    public static KdFBillTypeIDEnum ofKdParentGroupEnum(KdParentGroupEnum kdParentGroupEnum) {
        if (KdParentGroupEnum.DEV_CODE_1.equals(kdParentGroupEnum)
                || KdParentGroupEnum.TEST_CODE_1.equals(kdParentGroupEnum)
                || KdParentGroupEnum.LOCAL_CODE_1.equals(kdParentGroupEnum)
                || KdParentGroupEnum.PROD_CODE_1.equals(kdParentGroupEnum)) {
            return KdFBillTypeIDEnum.TYPE_1;
        } else if (KdParentGroupEnum.DEV_CODE_2.equals(kdParentGroupEnum)
                || KdParentGroupEnum.TEST_CODE_2.equals(kdParentGroupEnum)
                || KdParentGroupEnum.LOCAL_CODE_2.equals(kdParentGroupEnum)
                || KdParentGroupEnum.PROD_CODE_2.equals(kdParentGroupEnum)) {
            return KdFBillTypeIDEnum.TYPE_2;
        } else if (KdParentGroupEnum.DEV_CODE_3.equals(kdParentGroupEnum)
                || KdParentGroupEnum.TEST_CODE_3.equals(kdParentGroupEnum)
                || KdParentGroupEnum.LOCAL_CODE_3.equals(kdParentGroupEnum)
                || KdParentGroupEnum.PROD_CODE_3.equals(kdParentGroupEnum)) {
            return KdFBillTypeIDEnum.TYPE_3;
        } else if (KdParentGroupEnum.DEV_CODE_4.equals(kdParentGroupEnum)
                || KdParentGroupEnum.TEST_CODE_4.equals(kdParentGroupEnum)
                || KdParentGroupEnum.LOCAL_CODE_4.equals(kdParentGroupEnum)
                || KdParentGroupEnum.PROD_CODE_4.equals(kdParentGroupEnum)) {
            return KdFBillTypeIDEnum.TYPE_4;
        } else {
            throw new BusinessException("单据类型转换异常");
        }

    }
}
