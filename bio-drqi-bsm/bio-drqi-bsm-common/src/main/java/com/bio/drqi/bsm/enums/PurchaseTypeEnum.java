package com.bio.drqi.bsm.enums;

public enum PurchaseTypeEnum {
    TYPE_1("1", "常规物料采购"),
    TYPE_2("2", "非常规物料采购"),
    TYPE_3("3", "研发申购流程");
    public String code;
    public String name;

    PurchaseTypeEnum(String code, String name) {
        this.code = code;
        this.name = name;
    }

    public static String getNameByCode(String code) {
        if (code == null || "".equals(code)) {
            return null;
        }
        for (PurchaseTypeEnum purchaseTypeEnum : PurchaseTypeEnum.values()) {
            if (purchaseTypeEnum.code.equals(code)) {
                return purchaseTypeEnum.name;
            }
        }
        return null;
    }
}
