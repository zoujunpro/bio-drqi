package com.bio.drqi.es.support.search;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public interface SearchDocumentBuilder {

    String systemCode();

    String businessCode();

    String table();

    Class<?> entityClass();

    BaseMapper<?> mapper();

    Map<String, Object> build(Map<String, Object> row);

    List<Map<String, Object>> buildRows(String id);

    default void buildRows(String id, Consumer<List<Map<String, Object>>> batchConsumer) {
        batchConsumer.accept(buildRows(id));
    }

}
