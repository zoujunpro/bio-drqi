package com.bio.drqi.es.support.search.builder.seed;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.mapper.SeedStockTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class SeedStockSearchDocumentBuilder extends AbstractSeedSearchDocumentBuilder<SeedStockTb> {

    private final SeedStockTbMapper seedStockTbMapper;

    public SeedStockSearchDocumentBuilder(SeedStockTbMapper seedStockTbMapper) {
        this.seedStockTbMapper = seedStockTbMapper;
    }

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
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("species_name", speciesName(row.get("species_code")));
        row.put("breed_name", breedName(row.get("species_code"), row.get("breed_code")));
        row.put("pollination_method_name", dictName(BioDictTypeEnum.POLLINATE_TYPE, row.get("pollination_method")));
        row.put("harvest_type_name", dictName(BioDictTypeEnum.HARVEST_TYPE, row.get("harvest_type")));
        row.put("source_type_name", dictName(BioDictTypeEnum.SOURCE_CHANNEL, row.get("source_type")));
        row.put("material_type_name", dictName(BioDictTypeEnum.MATERIAL_TYPE, row.get("material_type")));
        row.put("production_location_name", produceAddressName(row.get("production_location_code")));
        return row;
    }

    @Override
    public Class<SeedStockTb> entityClass() {
        return SeedStockTb.class;
    }

    @Override
    public BaseMapper<SeedStockTb> mapper() {
        return seedStockTbMapper;
    }
}
