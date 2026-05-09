package com.bio.drqi.es.support.search.project.project;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ProjectCerSubProjectSearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder{

    @Override
    public String table() {
        return "cer_sub_project_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String taskStatusName = taskStatusName(row.get("task_status"));
        return buildDoc(row,
                stringValue(row.get("sub_project_code")),
                join(row.get("project_code"), row.get("create_user_name"), taskStatusName),
                "/project/sub/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "创建人", row.get("create_user_name"), "状态", taskStatusName),
                row.values(), taskStatusName);
    }

    @Override
    public List<Map<String, Object>> buildRows() {
        return Collections.emptyList();
    }





}
