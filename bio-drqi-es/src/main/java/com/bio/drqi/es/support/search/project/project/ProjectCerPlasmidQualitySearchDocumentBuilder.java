package com.bio.drqi.es.support.search.project.project;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class ProjectCerPlasmidQualitySearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder {

    @Override
    public String table() {
        return "cer_plasmid_quality_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String qualityInspectionResultName = qualityInspectionResultName(row.get("quality_inspection_result"));
        String taskStatusName = taskStatusName(row.get("task_status"));
        return buildDoc(row,
                stringValue(row.get("plasmid_name")),
                join(row.get("plasmid_name"), qualityInspectionResultName, row.get("create_user_name"), taskStatusName),
                "/project/plasmid-quality/detail/",
                display("质检编号", row.get("quality_inspection_number"), "质粒名称", row.get("plasmid_name"), "质检结果", qualityInspectionResultName, "质检人", row.get("create_user_name"), "状态", taskStatusName),
                row.values(), qualityInspectionResultName, taskStatusName);
    }

    @Override
    public List<Map<String, Object>> buildRows() {
        return Collections.emptyList();
    }


}
