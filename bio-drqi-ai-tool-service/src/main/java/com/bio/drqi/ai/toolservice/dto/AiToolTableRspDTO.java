package com.bio.drqi.ai.toolservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AiToolTableRspDTO {

    private String resultType;

    private String answer;

    private String summary;

    private Table table;

    private Object data;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Table {

        private List<Column> columns;

        private List<Map<String, Object>> rows;

        private Long total;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Column {

        private String key;

        private String title;
    }
}
