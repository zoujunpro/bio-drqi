package com.bio.drqi.es.support.global.project;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerProjectGlobalSearchDocumentBuilder extends AbstractProjectGlobalSearchDocumentBuilder {

    @Override
    public String table() {
        return "cer_project_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        return buildDoc(row,
                stringValue(row.get("project_name")),
                join(row.get("project_code"), row.get("owner_user_name"), row.get("project_status")),
                "/project/detail/",
                display("项目编号", row.get("project_code"), "负责人", row.get("owner_user_name"), "状态", row.get("project_status"), "任务编号", row.get("task_num")),
                row.values());
    }
}
