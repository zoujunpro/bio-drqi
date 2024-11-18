package com.bio.cer.enums;


public enum SeedMaterialTypeEnum {
    TYPE_1("1", "基因编辑材料", "yellow_seed_print"),
    TYPE_2("2", "转基因材料", "blue_seed_print"),
    TYPE_3("3", "常规材料", "Godex GE330"),
    ;
    public String name;
    public String type;
    public String printName;

    SeedMaterialTypeEnum(String type, String name, String printName) {
        this.name = name;
        this.type = type;
        this.printName = printName;
    }

    public static SeedMaterialTypeEnum getSeedMaterialTypeEnumByType(String type) {
        for (SeedMaterialTypeEnum seedMaterialTypeEnum : SeedMaterialTypeEnum.values()) {
            if (seedMaterialTypeEnum.type.equals(type)) {
                return seedMaterialTypeEnum;
            }
        }
        return TYPE_3;

    }
}
