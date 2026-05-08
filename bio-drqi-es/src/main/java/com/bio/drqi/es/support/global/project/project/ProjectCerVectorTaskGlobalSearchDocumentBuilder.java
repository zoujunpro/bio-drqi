package com.bio.drqi.es.support.global.project.project;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerVectorTaskGlobalSearchDocumentBuilder extends AbstractProjectGlobalSearchDocumentBuilder {

    @Override
    public String table() {
        return "cer_vector_task_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String taskStatusName = taskStatusName(row.get("task_status"));
        return buildDoc(row,
                stringValue(row.get("vector_task_code")),
                join(row.get("project_code"), row.get("sub_project_code"), row.get("acceptor_material"), taskStatusName),
                "/project/vector-task/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "载体任务", row.get("vector_task_code"), "受体材料", row.get("acceptor_material"), "状态", taskStatusName),
                row.values(), taskStatusName);
    }
}
