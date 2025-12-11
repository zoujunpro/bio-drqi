package com.bio.drqi.common.enums;

import org.springframework.util.StringUtils;

public enum GenerationEnum {
    T0("T0", "T0"),
    T1("T1", "T1"),
    T2("T2", "T2"),
    T3("T3", "T3"),
    T4("T4", "T4"),
    T5("T5", "T5"),
    T6("T6", "T6"),
    T7("T7", "T7"),
    T8("T8", "T8"),
    T9("T9", "T9"),
    TY("TY", "原种"),
    P1("P1", "P1"),
    P2("P2", "P2"),
    P3("P3", "P3"),
    P4("P4", "P4"),
    P5("P5", "P5"),
    P6("P6", "P6"),
    P7("P7", "P7"),
    P8("P8", "P8"),
    F1("F1", "F1"),
    F2("F2", "F2"),
    F3("F3", "F3"),
    F4("F4", "F4"),
    F5("F5", "F5"),
    F6("F6", "F6"),
    F7("F7", "F7"),
    F8("F8", "F8"),
    F9("F9", "F9"),

    ;
    public String code;
    public String desc;

    GenerationEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }


    public static GenerationEnum getGeneration(String desc) {
        if (StringUtils.isEmpty(desc)) {
            return null;
        }
        for (GenerationEnum generationEnum : GenerationEnum.values()) {
            if (generationEnum.desc.equals(desc)) {
                return generationEnum;
            }
        }
        return null;
    }

    public static String getGenerationDesc(String code) {
        if (code == null) {
            return "未知";
        }
        code = code.toUpperCase();
        for (GenerationEnum generationEnum : GenerationEnum.values()) {
            if (generationEnum.code.equals(code)) {
                return generationEnum.desc;
            }
        }
        return "未知";
    }


}
