package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 实体抽取结果。
 */
@Data
public class AiEntityExtractRspDTO implements Serializable {

    /**
     * 抽取到的实体列表。
     */
    private List<AiEntityDTO> entities = new ArrayList<AiEntityDTO>();

    private static final long serialVersionUID = 1L;
}
