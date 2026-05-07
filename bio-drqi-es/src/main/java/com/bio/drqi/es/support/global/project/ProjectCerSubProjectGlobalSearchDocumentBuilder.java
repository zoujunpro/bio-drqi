package com.bio.drqi.es.support.global.project;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerSubProjectGlobalSearchDocumentBuilder extends AbstractProjectGlobalSearchDocumentBuilder {

    @Override
    public String table() {
        return "cer_sub_project_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        return buildDoc(row,
                stringValue(row.get("sub_project_code")),
                join(row.get("project_code"), row.get("create_user_name"), row.get("task_status")),
                "/project/sub/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "创建人", row.get("create_user_name"), "状态", row.get("task_status")),
                row.values());
    }
}
