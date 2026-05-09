package com.bio.drqi.es.support.search.project.plant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.PlantApplyTb;
import com.bio.drqi.mapper.PlantApplyTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PlantApplySearchDocumentBuilder extends AbstractPlantSearchDocumentBuilder<PlantApplyTb> {

    private final PlantApplyTbMapper plantApplyTbMapper;

    public PlantApplySearchDocumentBuilder(PlantApplyTbMapper plantApplyTbMapper) {
        this.plantApplyTbMapper = plantApplyTbMapper;
    }

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
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("experiment_type_name", experimentTypeName(row.get("experiment_type")));
        row.put("species_name", speciesName(row.get("species_code")));
        return row;
    }

    @Override
    public Class<PlantApplyTb> entityClass() {
        return PlantApplyTb.class;
    }

    @Override
    public BaseMapper<PlantApplyTb> mapper() {
        return plantApplyTbMapper;
    }
}
