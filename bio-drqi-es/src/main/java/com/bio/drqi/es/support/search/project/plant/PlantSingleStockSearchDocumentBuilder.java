package com.bio.drqi.es.support.search.project.plant;

import com.bio.drqi.common.enums.BioDictTypeEnum;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
public class PlantSingleStockSearchDocumentBuilder extends AbstractPlantSearchDocumentBuilder {

    @Override
    public String table() {
        return "plant_single_stock_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String plantStatusName = plantStatusName(row.get("plant_status"));
        String speciesName = speciesName(row.get("species_code"));
        String breedName = breedName(row.get("species_code"), row.get("breed_code"));
        String pollinationMethodName = dictName(BioDictTypeEnum.POLLINATE_TYPE, row.get("pollination_method"));
        String harvestTypeName = dictName(BioDictTypeEnum.HARVEST_TYPE, row.get("harvest_type"));
        String sourceCodeName = dictName(BioDictTypeEnum.SOURCE_CHANNEL, row.get("source_code"));
        return buildDoc(row,
                stringValue(row.get("plant_code")),
                join(row.get("task_num"), row.get("sample_code"), speciesName, breedName, row.get("generation"), plantStatusName),
                "/plant/single-stock/detail/",
                display("种植编号", row.get("plant_code"), "任务编号", row.get("task_num"), "取样编号", row.get("sample_code"), "物种", speciesName, "品种", breedName, "代次", row.get("generation"), "植株状态", plantStatusName, "授粉方式", pollinationMethodName, "收获方式", harvestTypeName),
                row.values(), plantStatusName, speciesName, breedName, pollinationMethodName, harvestTypeName, sourceCodeName);
    }

    @Override
    public List<Map<String, Object>> buildRows() {
        return Collections.emptyList();
    }


}
