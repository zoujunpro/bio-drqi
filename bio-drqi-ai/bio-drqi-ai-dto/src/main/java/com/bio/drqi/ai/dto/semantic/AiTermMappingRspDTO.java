package com.bio.drqi.ai.dto.semantic;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 业务术语映射结果。
 */
@Data
public class AiTermMappingRspDTO implements Serializable {

    private List<AiTermDTO> terms = new ArrayList<AiTermDTO>();

    private static final long serialVersionUID = 1L;
}
