package com.bio.drqi.es.support.search.project.plant;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PlantApplySearchDocumentBuilder extends AbstractPlantSearchDocumentBuilder {

    @Override
    public String table() {
        return "plant_apply_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String experimentTypeName = experimentTypeName(row.get("experiment_type"));
        String speciesName = speciesName(row.get("species_code"));
        return buildDoc(row,
                stringValue(row.get("plant_apply_num")),
                join(speciesName, experimentTypeName, row.get("create_user_name"), row.get("vector_task_codes")),
                "/plant/apply/detail/",
                display("种植申请编号", row.get("plant_apply_num"), "物种", speciesName, "试验类型", experimentTypeName, "创建人", row.get("create_user_name"), "实施方案", row.get("vector_task_codes")),
                row.values(), speciesName, experimentTypeName);
    }

    @Override
    public List<Map<String, Object>> buildRows(String id) {
        return Collections.emptyList();
    }
}
