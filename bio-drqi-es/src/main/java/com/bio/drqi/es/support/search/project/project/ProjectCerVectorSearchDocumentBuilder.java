package com.bio.drqi.es.support.search.project.project;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ProjectCerVectorSearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder {

    @Override
    public String table() {
        return "cer_vector_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String qualityInspectionResultName = qualityInspectionResultName(row.get("quality_inspection_result"));
        return buildDoc(row,
                stringValue(row.get("plasmid_name")),
                join(row.get("vector_task_code"), row.get("target_gene"), row.get("target_site"), qualityInspectionResultName),
                "/project/vector/detail/",
                display("质粒名称", row.get("plasmid_name"), "载体任务", row.get("vector_task_code"), "靶基因", row.get("target_gene"), "靶位点", row.get("target_site"), "质检结果", qualityInspectionResultName),
                row.values(), qualityInspectionResultName);
    }

    @Override
    public List<Map<String, Object>> buildRows(String id) {
        return Collections.emptyList();
    }




}
