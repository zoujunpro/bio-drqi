package com.bio.drqi.ai.dto.plan;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class AiReportAggregationDTO {

    /**
     * 聚合类型。当前支持 leftJoin，后续可以扩展 union/sum/count 等。
     */
    private String type;

    /**
     * 聚合结果 sheet 名称。
     */
    private String sheetName;

    /**
     * 主结果集 stepCode，输出行以它为基准。
     */
    private String baseStepCode;

    /**
     * 被合并结果集 stepCode。
     */
    private String joinStepCode;

    /**
     * 主结果集关联字段。
     */
    private String baseKey;

    /**
     * 被合并结果集关联字段。
     */
    private String joinKey;

    /**
     * 主结果集保留字段。为空时保留全部字段。
     */
    private List<String> baseFields = new ArrayList<>();

    /**
     * 被合并结果集追加字段。
     */
    private List<String> joinFields = new ArrayList<>();
}
