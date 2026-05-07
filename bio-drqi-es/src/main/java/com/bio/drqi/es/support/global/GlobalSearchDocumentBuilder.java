package com.bio.drqi.es.support.global;

import java.util.Map;

public interface GlobalSearchDocumentBuilder {

    String systemCode();

    String table();

    Map<String, Object> build(Map<String, Object> row);
}
