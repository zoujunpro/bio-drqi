package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 数量解析结果。
 */
@Data
public class AiNumberParseRspDTO implements Serializable {

    /**
     * 数量表达列表。
     */
    private List<AiNumberItemDTO> numbers = new ArrayList<AiNumberItemDTO>();

    private static final long serialVersionUID = 1L;
}
