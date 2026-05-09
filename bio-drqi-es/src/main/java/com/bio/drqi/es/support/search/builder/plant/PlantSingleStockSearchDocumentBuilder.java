package com.bio.drqi.es.support.search.builder.plant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.PlantSingleStockTb;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.mapper.PlantSingleStockTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PlantSingleStockSearchDocumentBuilder extends AbstractPlantSearchDocumentBuilder<PlantSingleStockTb> {

    private final PlantSingleStockTbMapper plantSingleStockTbMapper;

    public PlantSingleStockSearchDocumentBuilder(PlantSingleStockTbMapper plantSingleStockTbMapper) {
        this.plantSingleStockTbMapper = plantSingleStockTbMapper;
    }

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
        String sourceCodeName = sourceCodeName(row.get("source_code"));
        return buildDoc(row,
                stringValue(row.get("plant_code")),
                join(row.get("task_num"), row.get("sample_code"), speciesName, breedName, row.get("generation"), plantStatusName),
                "/plant/single-stock/detail/",
                display("种植编号", row.get("plant_code"), "任务编号", row.get("task_num"), "取样编号", row.get("sample_code"), "物种", speciesName, "品种", breedName, "代次", row.get("generation"), "植株状态", plantStatusName, "授粉方式", pollinationMethodName, "收获方式", harvestTypeName),
                row.values(), plantStatusName, speciesName, breedName, pollinationMethodName, harvestTypeName, sourceCodeName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("plant_status_name", plantStatusName(row.get("plant_status")));
        row.put("species_name", speciesName(row.get("species_code")));
        row.put("breed_name", breedName(row.get("species_code"), row.get("breed_code")));
        row.put("pollination_method_name", dictName(BioDictTypeEnum.POLLINATE_TYPE, row.get("pollination_method")));
        row.put("harvest_type_name", dictName(BioDictTypeEnum.HARVEST_TYPE, row.get("harvest_type")));
        row.put("source_code_name", sourceCodeName(row.get("source_code")));
        return row;
    }

    @Override
    public Class<PlantSingleStockTb> entityClass() {
        return PlantSingleStockTb.class;
    }

    @Override
    public BaseMapper<PlantSingleStockTb> mapper() {
        return plantSingleStockTbMapper;
    }

}
