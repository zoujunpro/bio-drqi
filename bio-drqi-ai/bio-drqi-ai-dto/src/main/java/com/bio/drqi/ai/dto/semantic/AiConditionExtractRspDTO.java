package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 条件抽取结果。
 */
@Data
public class AiConditionExtractRspDTO implements Serializable {

    private List<AiConditionDTO> conditions = new ArrayList<AiConditionDTO>();

    private static final long serialVersionUID = 1L;
}
