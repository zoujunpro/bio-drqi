package com.bio.drqi.es.support.global.project;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerPlasmidQualityGlobalSearchDocumentBuilder extends AbstractProjectGlobalSearchDocumentBuilder {

    @Override
    public String table() {
        return "cer_plasmid_quality_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        return buildDoc(row,
                stringValue(row.get("quality_inspection_number")),
                join(row.get("plasmid_name"), row.get("quality_inspection_result"), row.get("create_user_name")),
                "/project/plasmid-quality/detail/",
                display("质检编号", row.get("quality_inspection_number"), "质粒名称", row.get("plasmid_name"), "质检结果", row.get("quality_inspection_result"), "质检人", row.get("create_user_name")),
                row.values());
    }
}
