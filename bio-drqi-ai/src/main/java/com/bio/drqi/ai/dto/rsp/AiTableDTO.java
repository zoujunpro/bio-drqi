package com.bio.drqi.ai.dto.rsp;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class AiTableDTO {

    private String title;

    private List<AiTableColumnDTO> columns = new ArrayList<>();

    private List<Map<String, Object>> data = new ArrayList<>();
}
