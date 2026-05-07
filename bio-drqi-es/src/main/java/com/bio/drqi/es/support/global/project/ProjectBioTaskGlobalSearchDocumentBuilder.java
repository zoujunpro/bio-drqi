package com.bio.drqi.es.support.global.project;

import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class ProjectBioTaskGlobalSearchDocumentBuilder extends AbstractProjectGlobalSearchDocumentBuilder {

    private static final String TABLE = "bio_task_dtl_tb";

    @Override
    public String table() {
        return TABLE;
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String id = stringValue(row.get("id"));
        String taskFormValues = extractJsonValues(row.get("task_form"));

        Map<String, Object> display = new LinkedHashMap<>();
        display.put("任务类型", row.get("task_type_name"));
        display.put("申请人", row.get("apply_user_name"));
        display.put("状态", row.get("task_status"));
        display.put("申请时间", row.get("apply_time"));

        Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("system_code", systemCode());
        doc.put("biz_type", table());
        doc.put("biz_id", id);
        doc.put("title", row.get("task_num"));
        doc.put("summary", join(row.get("task_type_name"), row.get("apply_user_name"), row.get("task_status")));
        doc.put("search_content", join(
                row.get("task_num"),
                row.get("task_type_code"),
                row.get("task_type_name"),
                row.get("task_status"),
                row.get("task_desc"),
                row.get("apply_user_name"),
                row.get("task_category"),
                taskFormValues
        ));
        doc.put("route", "/task/detail/" + id);
        doc.put("display", display);
        doc.put("create_time", row.get("create_time"));
        return doc;
    }
}
