package com.bio.drqi.es.support.search.builder.plant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.PlantMultipleStockTb;
import com.bio.drqi.mapper.PlantMultipleStockTbMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
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
        String seedNum = stringValue(row.get("seed_num"));
        String transformCode = stringValue(row.get("transform_code"));
        String vectorTaskCode = stringValue(row.get("vector_task_code"));
        String title = seedNum.trim().isEmpty() ? transformCode : seedNum;
        return buildDoc(row,
                title,
                join(row.get("task_num"), speciesName, breedName, row.get("generation"), row.get("current_number")),
                "/plant/multiple-stock/detail/",
                buildDisplay(row, speciesName, breedName, seedNum, transformCode, vectorTaskCode),
                row.values(), speciesName, breedName);
    }

    private Map<String, Object> buildDisplay(Map<String, Object> row,
                                             String speciesName,
                                             String breedName,
                                             String seedNum,
                                             String transformCode,
                                             String vectorTaskCode) {
        Map<String, Object> display = new LinkedHashMap<>();
        if (seedNum.trim().isEmpty()) {
            display.put("转化编号", transformCode);
            display.put("实施方案编号", vectorTaskCode);
        } else {
            display.put("种子编号", seedNum);
        }
        display.put("工单编号", row.get("task_num"));
        display.put("物种", speciesName);
        display.put("品种", breedName);
        display.put("代次", row.get("generation"));
        display.put("剩余数量", row.get("current_number"));
        return display;
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
