package com.bio.drqi.enums;

import org.springframework.util.StringUtils;

public enum GenerationEnum {
    T0("T0", "00", "T0"),
    T1("T1", "01", "T1"),
    T2("T2", "02", "T2"),
    T3("T3", "03", "T3"),
    T4("T4", "04", "T4"),
    T5("T5", "05", "T5"),
    T6("T6", "06", "T6"),
    T7("T7", "07", "T7"),
    T8("T8", "08", "T8"),
    T9("T9", "09", "T9"),
    TY("TY", "00", "原种");
    public String num;
    public String code;
    public String desc;

    GenerationEnum(String code, String num, String desc) {
        this.num = num;
        this.code = code;
        this.desc = desc;
    }


    public static String getGenerationNum(String code) {
        for (GenerationEnum generationEnum : GenerationEnum.values()) {
            if (generationEnum.code.equals(code)) {
                return generationEnum.num;
            }
        }
        return null;
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
