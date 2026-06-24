package com.bio.drqi.ai.schema;

import lombok.Data;

import java.util.Map;

@Data
public class AiFieldPromptDTO {

    private String field;

    private String label;

    private String type;

    private Map<String, String> enumValues;
}
