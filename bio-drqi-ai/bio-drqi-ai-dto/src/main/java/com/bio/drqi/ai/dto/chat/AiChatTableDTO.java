package com.bio.drqi.ai.dto.chat;

import lombok.Data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * AI 聊天表格结果。
 */
@Data
public class AiChatTableDTO implements Serializable {

    /**
     * 表头定义。
     */
    private List<Column> columns = new ArrayList<Column>();

    /**
     * 表格行数据。
     */
    private List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

    /**
     * 总条数。
     */
    private Integer total;

    @Data
    public static class Column implements Serializable {

        private String key;

        private String title;

        private String dataType;

        private static final long serialVersionUID = 1L;
    }

    private static final long serialVersionUID = 1L;
}
