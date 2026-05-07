package com.bio.drqi.es.support.global.project;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerVectorGlobalSearchDocumentBuilder extends AbstractProjectGlobalSearchDocumentBuilder {

    @Override
    public String table() {
        return "cer_vector_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        return buildDoc(row,
                stringValue(row.get("plasmid_name")),
                join(row.get("vector_task_code"), row.get("target_gene"), row.get("target_site")),
                "/project/vector/detail/",
                display("质粒名称", row.get("plasmid_name"), "载体任务", row.get("vector_task_code"), "靶基因", row.get("target_gene"), "靶位点", row.get("target_site")),
                row.values());
    }
}
