package com.bio.drqi.es.support.search;

import java.util.List;
import java.util.Map;

public interface SearchDocumentBuilder {

    String systemCode();

    String businessCode();

    String table();

    Map<String, Object> build(Map<String, Object> row);

    List<Map<String, Object>> buildRows(String id);


}
