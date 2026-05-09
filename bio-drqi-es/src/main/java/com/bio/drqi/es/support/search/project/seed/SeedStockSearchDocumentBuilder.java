package com.bio.drqi.es.support.search.project.seed;

import com.bio.drqi.common.enums.BioDictTypeEnum;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class SeedStockSearchDocumentBuilder extends AbstractSeedSearchDocumentBuilder{

    @Override
    public String table() {
        return "seed_stock_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String speciesName = speciesName(row.get("species_code"));
        String breedName = breedName(row.get("species_code"), row.get("breed_code"));
        String pollinationMethodName = dictName(BioDictTypeEnum.POLLINATE_TYPE, row.get("pollination_method"));
        String harvestTypeName = dictName(BioDictTypeEnum.HARVEST_TYPE, row.get("harvest_type"));
        String sourceTypeName = dictName(BioDictTypeEnum.SOURCE_CHANNEL, row.get("source_type"));
        String materialTypeName = dictName(BioDictTypeEnum.MATERIAL_TYPE, row.get("material_type"));
        String productionLocationName = produceAddressName(row.get("production_location_code"));
        return buildDoc(row,
                stringValue(row.get("seed_num")),
                join(row.get("plant_code"), speciesName, breedName, row.get("generation"), row.get("seed_number"), sourceTypeName),
                "/seed/stock/detail/",
                display("种子编号", row.get("seed_num"), "种植编号", row.get("plant_code"), "物种", speciesName, "品种", breedName, "代次", row.get("generation"), "种子数量", row.get("seed_number"), "来源", sourceTypeName, "生产地点", productionLocationName),
                row.values(), speciesName, breedName, pollinationMethodName, harvestTypeName, sourceTypeName, materialTypeName, productionLocationName);
    }

    @Override
    public List<Map<String, Object>> buildRows(String id) {
        return Collections.emptyList();
    }




}
