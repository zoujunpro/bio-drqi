package com.bio.drqi.ai.dto.plan;

import lombok.Data;

@Data
public class AiQueryOrderDTO {

    /**
     * 排序字段，可以是维度字段，也可以是指标字段。
     */
    private String field;

    /**
     * 排序方向：asc/desc。
     */
    private String direction;
}
