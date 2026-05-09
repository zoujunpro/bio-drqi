package com.bio.drqi.es.support.search.project.plant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.PlantMultipleStockTb;
import com.bio.drqi.mapper.PlantMultipleStockTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PlantMultipleStockSearchDocumentBuilder extends AbstractPlantSearchDocumentBuilder<PlantMultipleStockTb> {

    private final PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    public PlantMultipleStockSearchDocumentBuilder(PlantMultipleStockTbMapper plantMultipleStockTbMapper) {
        this.plantMultipleStockTbMapper = plantMultipleStockTbMapper;
    }

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

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("species_name", speciesName(row.get("species_code")));
        row.put("breed_name", breedName(row.get("species_code"), row.get("breed_code")));
        return row;
    }

    @Override
    public Class<PlantMultipleStockTb> entityClass() {
        return PlantMultipleStockTb.class;
    }

    @Override
    public BaseMapper<PlantMultipleStockTb> mapper() {
        return plantMultipleStockTbMapper;
    }

}
