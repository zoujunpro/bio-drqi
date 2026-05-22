package com.bio.drqi.common.enums;

import com.bio.common.core.dto.BusinessException;
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
    T10("T10", "T10"),
    T11("T11", "T11"),
    T12("T12", "T12"),
    T13("T13", "T9"),
    T14("T14", "T14"),
    T15("T15", "T15"),
    T16("T16", "T16"),
    T17("T17", "T17"),
    TY("TY", "原种"),
    P1("P1", "P1"),
    P2("P2", "P2"),
    P3("P3", "P3"),
    P4("P4", "P4"),
    P5("P5", "P5"),
    P6("P6", "P6"),
    P7("P7", "P7"),
    P8("P8", "P8"),
    P9("P9", "P9"),
    P10("P10", "P10"),
    P11("P11", "P11"),
    P12("P12", "P12"),
    P13("P13", "P13"),
    P14("P14", "P14"),
    P15("P15", "P8"),
    P16("P16", "P16"),
    P17("P17", "P17"),
    F1("F1", "F1"),
    F2("F2", "F2"),
    F3("F3", "F3"),
    F4("F4", "F4"),
    F5("F5", "F5"),
    F6("F6", "F6"),
    F7("F7", "F7"),
    F8("F8", "F8"),
    F9("F9", "F9"),
    F10("F10", "F10"),
    F11("F11", "F11"),
    F12("F12", "F12"),
    F13("F13", "F13"),
    F14("F14", "F14"),
    F15("F15", "F15"),
    F16("F16", "F16"),
    F17("F17", "F17"),
    BC1("BC1", "BC1"),
    BC2("BC2", "BC2"),
    BC3("BC3", "BC3"),
    BC4("BC4", "BC4"),
    BC4F1("BC4F1", "BC4F1"),
    BC4F2("BC4F2", "BC4F2"),
    BC4F3("BC4F3", "BC4F3"),
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

    public static String nextGenerationCode(String generationCode) {
        if (TY.code.equals(generationCode)) {
            return generationCode;
        }
        String nextCode = generationCode.substring(0, 1) + (Integer.valueOf(generationCode.substring(1)) + 1);
        for (GenerationEnum generationEnum : GenerationEnum.values()) {
            if (generationEnum.code.equals(nextCode)) {
                return generationEnum.code;
            }
        }
        throw new BusinessException("不支持此品种，请联系人员添加");
    }

}
