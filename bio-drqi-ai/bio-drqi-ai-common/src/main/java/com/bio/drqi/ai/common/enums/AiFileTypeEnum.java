package com.bio.drqi.ai.common.enums;

/**
 * AI 聊天附件文件类型。
 */
public enum AiFileTypeEnum {

    PDF("pdf", "PDF"),

    WORD("word", "Word"),

    EXCEL("excel", "Excel"),

    TEXT("text", "文本"),

    IMAGE("image", "图片"),

    UNKNOWN("unknown", "未知");

    private final String code;

    private final String desc;

    AiFileTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }

    public static AiFileTypeEnum resolve(String fileType, String fileName) {
        String value = firstText(fileType, extension(fileName));
        if (value == null) {
            return UNKNOWN;
        }

        String lower = value.toLowerCase();
        if ("pdf".equals(lower)) {
            return PDF;
        }
        if ("doc".equals(lower) || "docx".equals(lower) || "word".equals(lower)) {
            return WORD;
        }
        if ("xls".equals(lower) || "xlsx".equals(lower) || "excel".equals(lower)) {
            return EXCEL;
        }
        if ("txt".equals(lower) || "csv".equals(lower) || "json".equals(lower)
                || "md".equals(lower) || "markdown".equals(lower)) {
            return TEXT;
        }
        if ("jpg".equals(lower) || "jpeg".equals(lower) || "png".equals(lower)
                || "gif".equals(lower) || "bmp".equals(lower) || "webp".equals(lower)
                || "image".equals(lower)) {
            return IMAGE;
        }
        return UNKNOWN;
    }

    private static String extension(String fileName) {
        if (fileName == null) {
            return null;
        }
        int index = fileName.lastIndexOf('.');
        if (index < 0 || index == fileName.length() - 1) {
            return null;
        }
        return fileName.substring(index + 1);
    }

    private static String firstText(String first, String second) {
        if (hasText(first)) {
            return first.trim();
        }
        if (hasText(second)) {
            return second.trim();
        }
        return null;
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
