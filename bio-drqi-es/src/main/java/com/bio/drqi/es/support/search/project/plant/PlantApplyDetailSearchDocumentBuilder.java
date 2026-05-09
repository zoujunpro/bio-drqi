package com.bio.drqi.es.support.search.project.plant;

import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PlantApplyDetailSearchDocumentBuilder extends AbstractPlantSearchDocumentBuilder {

    @Override
    public String table() {
        return "plant_apply_detail_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String speciesName = speciesName(row.get("species_code"));
        String breedName = breedName(row.get("species_code"), row.get("breed_code"));
        return buildDoc(row,
                stringValue(row.get("plant_code")),
                join(row.get("plant_apply_num"), row.get("pd_implement_code"), row.get("vector_task_code"), speciesName, breedName, row.get("plant_number")),
                "/plant/apply-detail/detail/",
                display("种植编号", row.get("plant_code"), "申请编号", row.get("plant_apply_num"), "PD号", row.get("pd_implement_code"), "实施方案", row.get("vector_task_code"), "物种", speciesName, "品种", breedName, "播种数量", row.get("plant_number")),
                row.values(), speciesName, breedName);
    }

    @Override
    public List<Map<String, Object>> buildRows() {
        return Collections.emptyList();
    }


}
