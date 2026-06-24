package com.bio.drqi.ai.dto.rsp;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class AiChartDTO {

    private String type;

    private String title;

    /**
     * ECharts option。
     */
    private Map<String, Object> option = new LinkedHashMap<>();
}
