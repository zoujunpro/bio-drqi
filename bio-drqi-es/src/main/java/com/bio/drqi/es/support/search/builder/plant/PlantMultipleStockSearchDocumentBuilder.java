package com.bio.drqi.es.support.search.builder.plant;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.domain.PlantMultipleStockTb;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.mapper.PlantMultipleStockTbMapper;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PlantMultipleStockSearchDocumentBuilder extends AbstractPlantSearchDocumentBuilder<PlantMultipleStockTb> {

    private final PlantMultipleStockTbMapper plantMultipleStockTbMapper;
    private final CerVectorTaskTbMapper cerVectorTaskTbMapper;

    public PlantMultipleStockSearchDocumentBuilder(PlantMultipleStockTbMapper plantMultipleStockTbMapper,
                                                   CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        this.plantMultipleStockTbMapper = plantMultipleStockTbMapper;
        this.cerVectorTaskTbMapper = cerVectorTaskTbMapper;
    }

    @Override
    public String table() {
        return "plant_multiple_stock_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        String speciesName = speciesName(row.get("species_code"));
        String breedName = breedName(row.get("species_code"), row.get("breed_code"));
        String seedNum = stringValue(row.get("seed_num"));
        String transformCode = stringValue(row.get("transform_code"));
        String vectorTaskCode = stringValue(row.get("vector_task_code"));
        String title = seedNum.trim().isEmpty() ? transformCode : seedNum;
        return buildDoc(row,
                title,
                join(row.get("project_code"), row.get("sub_project_code"), row.get("vector_task_code"), row.get("task_num"), speciesName, breedName, row.get("generation"), row.get("current_number")),
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
        display.put("项目编号", row.get("project_code"));
        display.put("子项目编号", row.get("sub_project_code"));
        display.put("实施方案编号", vectorTaskCode);
        if (seedNum.trim().isEmpty()) {
            display.put("转化编号", transformCode);
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
    public Class<PlantMultipleStockTb> entityClass() {
        return PlantMultipleStockTb.class;
    }

    @Override
    public BaseMapper<PlantMultipleStockTb> mapper() {
        return plantMultipleStockTbMapper;
    }

}
