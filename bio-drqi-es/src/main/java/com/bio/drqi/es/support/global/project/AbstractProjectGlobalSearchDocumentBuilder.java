package com.bio.drqi.es.support.global.project;

import com.bio.drqi.es.support.global.AbstractGlobalSearchDocumentBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractProjectGlobalSearchDocumentBuilder extends AbstractGlobalSearchDocumentBuilder {

    @Override
    public String systemCode() {
        return "project";
    }

    protected Map<String, Object> buildDoc(Map<String, Object> row,
                                           String title,
                                           String summary,
                                           String route,
                                           Map<String, Object> display,
                                           Object... searchValues) {
        String id = stringValue(row.get("id"));
        Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("system_code", systemCode());
        doc.put("biz_type", table());
        doc.put("biz_id", id);
        doc.put("title", title);
        doc.put("summary", summary);
        doc.put("search_content", join(searchValues));
        doc.put("route", route + id);
        doc.put("display", display);
        doc.put("create_time", row.get("create_time"));
        return doc;
    }

    protected Map<String, Object> display(Object... labelValues) {
        Map<String, Object> display = new LinkedHashMap<>();
        if (labelValues == null) {
            return display;
        }
        for (int i = 0; i + 1 < labelValues.length; i += 2) {
            display.put(stringValue(labelValues[i]), labelValues[i + 1]);
        }
        return display;
    }
}
