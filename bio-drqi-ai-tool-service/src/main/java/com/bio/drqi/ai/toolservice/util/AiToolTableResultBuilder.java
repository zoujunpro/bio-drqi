package com.bio.drqi.ai.toolservice.util;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.bio.drqi.ai.toolservice.dto.AiToolTableRspDTO;
import com.github.pagehelper.PageInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class AiToolTableResultBuilder {

    private AiToolTableResultBuilder() {
    }

    public static AiToolTableRspDTO page(String answer,
                                         PageInfo<?> pageInfo,
                                         List<AiToolTableRspDTO.Column> columns) {
        List<Map<String, Object>> rows = new ArrayList<>();
        if (pageInfo != null && pageInfo.getList() != null) {
            for (Object item : pageInfo.getList()) {
                rows.add(JSON.parseObject(JSON.toJSONString(item), new TypeReference<Map<String, Object>>() {
                }));
            }
        }
        long total = pageInfo == null ? 0L : pageInfo.getTotal();
        return AiToolTableRspDTO.builder()
                .resultType("TABLE")
                .answer(answer)
                .summary(answer + "，共 " + total + " 条")
                .table(AiToolTableRspDTO.Table.builder()
                        .columns(columns)
                        .rows(rows)
                        .total(total)
                        .build())
                .data(pageInfo)
                .build();
    }

    public static List<AiToolTableRspDTO.Column> columns(String... pairs) {
        List<AiToolTableRspDTO.Column> columns = new ArrayList<>();
        for (int i = 0; i + 1 < pairs.length; i += 2) {
            columns.add(AiToolTableRspDTO.Column.builder()
                    .key(pairs[i])
                    .title(pairs[i + 1])
                    .build());
        }
        return columns;
    }
}
