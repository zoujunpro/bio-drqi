package com.bio.drqi.common.enums;

public enum EsFieldTypeEnum {

    AUTO(""),
    KEYWORD("keyword"),
    TEXT("text"),
    OBJECT("object"),
    FLATTENED("flattened"),
    INTEGER("integer"),
    LONG("long"),
    FLOAT("float"),
    DOUBLE("double"),
    BOOLEAN("boolean"),
    DATE("date");

    private final String type;

    EsFieldTypeEnum(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
