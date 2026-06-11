package com.bio.drqi.es.support.search.builder.seed;

import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class AbstractSeedStockRecordSearchDocumentBuilder<T> extends AbstractSeedSearchDocumentBuilder<T> {

    private final CerVectorTaskTbMapper cerVectorTaskTbMapper;

    protected AbstractSeedStockRecordSearchDocumentBuilder(CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        this.cerVectorTaskTbMapper = cerVectorTaskTbMapper;
    }

    protected Map<String, Object> buildSeedRecordDoc(Map<String, Object> row,
                                                     String operationName,
                                                     String quantityLabel,
                                                     String route,
                                                     Object... extraSearchValues) {
        enrichSeedRecordRow(row);
        String speciesName = stringValue(row.get("species_name"));
        String breedName = stringValue(row.get("breed_name"));
        String sourceTypeName = stringValue(row.get("source_type_name"));
        String productionLocationName = stringValue(row.get("production_location_name"));
        Object quantity = row.get("seed_number");
        return buildDoc(row,
                stringValue(row.get("seed_num")),
                join(operationName, row.get("project_code"), row.get("vector_task_code"), row.get("plant_code"), speciesName, breedName, row.get("generation"), quantity, sourceTypeName, extraSearchValues),
                route,
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "实施方案编号", row.get("vector_task_code"), "种子编号", row.get("seed_num"), "种植编号", row.get("plant_code"), "物种", speciesName, "品种", breedName, "代次", row.get("generation"), quantityLabel, quantity, "来源", sourceTypeName, "生产地点", productionLocationName),
                row.values(), operationName, quantityLabel, speciesName, breedName, row.get("pollination_method_name"), row.get("harvest_type_name"), sourceTypeName, row.get("material_type_name"), productionLocationName, extraSearchValues);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        return enrichSeedRecordRowWithoutVectorTaskQuery(row);
    }

    @Override
    protected List<Map<String, Object>> enrichRows(List<Map<String, Object>> rows) {
        fillVectorTaskInfo(rows);
        return rows.stream()
                .map(this::enrichSeedRecordRowWithoutVectorTaskQuery)
                .collect(Collectors.toList());
    }

    private Map<String, Object> enrichSeedRecordRow(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        return enrichSeedRecordRowWithoutVectorTaskQuery(row);
    }

    private Map<String, Object> enrichSeedRecordRowWithoutVectorTaskQuery(Map<String, Object> row) {
        if (!row.containsKey("create_time") && row.containsKey("destruction_time")) {
            row.put("create_time", row.get("destruction_time"));
        }
        row.put("species_name", speciesName(row.get("species_code")));
        row.put("breed_name", breedName(row.get("species_code"), row.get("breed_code")));
        row.put("pollination_method_name", dictName(BioDictTypeEnum.POLLINATE_TYPE, row.get("pollination_method")));
        row.put("harvest_type_name", dictName(BioDictTypeEnum.HARVEST_TYPE, row.get("harvest_type")));
        row.put("source_type_name", dictName(BioDictTypeEnum.SOURCE_CHANNEL, row.get("source_type")));
        row.put("material_type_name", dictName(BioDictTypeEnum.MATERIAL_TYPE, row.get("material_type")));
        row.put("production_location_name", produceAddressName(row.get("production_location_code")));
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
        if (cerVectorTaskTb == null || stringValue(cerVectorTaskTb.getProjectCode()).trim().isEmpty()) {
            return;
        }
        row.put("project_code", cerVectorTaskTb.getProjectCode());
        row.put("sub_project_code", cerVectorTaskTb.getSubProjectCode());
    }
}
