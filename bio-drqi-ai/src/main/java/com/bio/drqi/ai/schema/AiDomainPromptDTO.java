package com.bio.drqi.ai.schema;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiDomainPromptDTO {

    private String domain;

    private String name;

    private List<AiFieldPromptDTO> fields = new ArrayList<>();

    private List<AiFieldPromptDTO> dimensions = new ArrayList<>();

    private List<AiMetricPromptDTO> metrics = new ArrayList<>();
}
