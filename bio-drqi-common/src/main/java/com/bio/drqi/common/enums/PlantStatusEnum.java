package com.bio.drqi.common.enums;

public enum PlantStatusEnum {
    STATUS_1("1", "正常"),
    STATUS_2("2", "异常"),
    STATUS_3("3", "已剔除"),
    STATUS_4("4", "已收获"),
    STATUS_5("5", "已死亡"),
    STATUS_6("6", "生育期结束"),
    ;
    public String code;
    public String desc;

    PlantStatusEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public static String getCodeByDesc(String desc) {
        for (PlantStatusEnum plantStatusEnum : PlantStatusEnum.values()) {
            if (plantStatusEnum.desc.equals(desc)) {
                return plantStatusEnum.code;
            }
        }
        return null;
    }
}
