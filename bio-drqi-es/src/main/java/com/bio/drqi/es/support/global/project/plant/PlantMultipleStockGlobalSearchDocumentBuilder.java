package com.bio.drqi.es.support.global.project.plant;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PlantMultipleStockGlobalSearchDocumentBuilder extends AbstractPlantGlobalSearchDocumentBuilder {

    @Override
    public String table() {
        return "plant_multiple_stock_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String speciesName = speciesName(row.get("species_code"));
        String breedName = breedName(row.get("species_code"), row.get("breed_code"));
        return buildDoc(row,
                stringValue(row.get("seed_num")),
                join(row.get("task_num"), speciesName, breedName, row.get("generation"), row.get("current_number")),
                "/plant/multiple-stock/detail/",
                display("种子编号", row.get("seed_num"), "工单编号", row.get("task_num"), "物种", speciesName, "品种", breedName, "代次", row.get("generation"), "剩余数量", row.get("current_number")),
                row.values(), speciesName, breedName);
    }
}
