package com.bio.drqi.ai.schema;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiDomainSummaryDTO {

    private String domain;

    private String name;

    /**
     * 代表性字段中文名/字段名，用于业务域路由时辅助模型判断。
     */
    private List<String> fields = new ArrayList<>();
}
