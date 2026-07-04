package com.bio.drqi.ai.dto.llm;

import lombok.Data;

@Data
public class LlmCallOptionsDTO {

    /**
     * 调用场景：router/query/report/command/chat。
     */
    private String scene;

    /**
     * 本次调用温度。为空时使用模型默认温度。
     */
    private Double temperature;

    public static LlmCallOptionsDTO of(String scene, Double temperature) {
        LlmCallOptionsDTO options = new LlmCallOptionsDTO();
        options.setScene(scene);
        options.setTemperature(temperature);
        return options;
    }
}
