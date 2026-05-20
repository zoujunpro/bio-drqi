package com.bio.drqi.es.support.search.builder.plant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.domain.PlantApplyDetailTb;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.mapper.PlantApplyDetailTbMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PlantApplyDetailSearchDocumentBuilder extends AbstractPlantSearchDocumentBuilder<PlantApplyDetailTb> {

    private final PlantApplyDetailTbMapper plantApplyDetailTbMapper;
    private final CerVectorTaskTbMapper cerVectorTaskTbMapper;

    public PlantApplyDetailSearchDocumentBuilder(PlantApplyDetailTbMapper plantApplyDetailTbMapper,
                                                 CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        this.plantApplyDetailTbMapper = plantApplyDetailTbMapper;
        this.cerVectorTaskTbMapper = cerVectorTaskTbMapper;
    }

    @Override
    public String table() {
        return "plant_apply_detail_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        String speciesName = speciesName(row.get("species_code"));
        String breedName = breedName(row.get("species_code"), row.get("breed_code"));
        return buildDoc(row,
                stringValue(row.get("plant_code")),
                join(row.get("project_code"), row.get("sub_project_code"), row.get("plant_apply_num"), row.get("pd_implement_code"), row.get("vector_task_code"), speciesName, breedName, row.get("plant_number")),
                "/plant/apply-detail/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "实施方案编号", row.get("vector_task_code"), "种植编号", row.get("plant_code"), "申请编号", row.get("plant_apply_num"), "PD号", row.get("pd_implement_code"), "物种", speciesName, "品种", breedName, "播种数量", row.get("plant_number")),
                row.values(), speciesName, breedName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        row.put("species_name", speciesName(row.get("species_code")));
        row.put("breed_name", breedName(row.get("species_code"), row.get("breed_code")));
        return row;
    }

    @Override
    protected List<Map<String, Object>> enrichRows(List<Map<String, Object>> rows) {
        fillVectorTaskInfo(rows);
        return rows.stream()
                .map(this::enrichRowWithoutVectorTaskQuery)
                .collect(Collectors.toList());
    }

    private Map<String, Object> enrichRowWithoutVectorTaskQuery(Map<String, Object> row) {
        row.put("species_name", speciesName(row.get("species_code")));
        row.put("breed_name", breedName(row.get("species_code"), row.get("breed_code")));
        return row;
    }

    private void fillVectorTaskInfo(List<Map<String, Object>> rows) {
        List<String> vectorTaskCodeList = rows.stream()
                .map(row -> stringValue(row.get("vector_task_code")))
                .filter(vectorTaskCode -> !vectorTaskCode.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
        if (vectorTaskCodeList.isEmpty()) {
            return;
        }
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllByVectorTaskCodeIn(vectorTaskCodeList);
        if (cerVectorTaskTbList == null || cerVectorTaskTbList.isEmpty()) {
            return;
        }
        Map<String, CerVectorTaskTb> vectorTaskMap = cerVectorTaskTbList.stream()
                .filter(item -> !stringValue(item.getVectorTaskCode()).trim().isEmpty())
                .collect(Collectors.toMap(CerVectorTaskTb::getVectorTaskCode, Function.identity(), (first, second) -> first));
        for (Map<String, Object> row : rows) {
            CerVectorTaskTb cerVectorTaskTb = vectorTaskMap.get(stringValue(row.get("vector_task_code")));
            if (cerVectorTaskTb == null) {
                continue;
            }
            row.put("project_code", cerVectorTaskTb.getProjectCode());
            row.put("sub_project_code", cerVectorTaskTb.getSubProjectCode());
        }
    }

    private void fillVectorTaskInfo(Map<String, Object> row) {
        String vectorTaskCode = stringValue(row.get("vector_task_code"));
        if (vectorTaskCode.trim().isEmpty()) {
            return;
        }
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb == null) {
            return;
        }
        row.put("project_code", cerVectorTaskTb.getProjectCode());
        row.put("sub_project_code", cerVectorTaskTb.getSubProjectCode());
    }

    @Override
    public Class<PlantApplyDetailTb> entityClass() {
        return PlantApplyDetailTb.class;
    }

    @Override
    public BaseMapper<PlantApplyDetailTb> mapper() {
        return plantApplyDetailTbMapper;
    }

}
