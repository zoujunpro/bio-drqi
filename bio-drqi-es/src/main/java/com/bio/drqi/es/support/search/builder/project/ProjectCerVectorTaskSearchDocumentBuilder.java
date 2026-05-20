package com.bio.drqi.es.support.search.builder.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectCerVectorTaskSearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder<CerVectorTaskTb> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final CerVectorTaskTbMapper cerVectorTaskTbMapper;

    public ProjectCerVectorTaskSearchDocumentBuilder(CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        this.cerVectorTaskTbMapper = cerVectorTaskTbMapper;
    }

    @Override
    public String table() {
        return "cer_vector_task_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String taskStatusName = taskStatusName(row.get("task_status"));
        String speciesName = speciesNames(row.get("species_code"));
        String breedName = breedNames(row.get("species_code"), row.get("breed_code"));
        String acceptorMaterialName = breedNames(row.get("species_code"), row.get("acceptor_material"));
        return buildDoc(row,
                stringValue(row.get("vector_task_code")),
                join(row.get("project_code"), row.get("sub_project_code"), speciesName, breedName, acceptorMaterialName, taskStatusName),
                "/project/vector-task/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "载体任务", row.get("vector_task_code"), "物种", speciesName, "品种", breedName, "受体材料", acceptorMaterialName, "创建人", row.get("create_user_name"), "状态", taskStatusName),
                row.values(), speciesName, breedName, acceptorMaterialName, taskStatusName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("task_status_name", taskStatusName(row.get("task_status")));
        row.put("species_name", speciesNames(row.get("species_code")));
        row.put("breed_name", breedNames(row.get("species_code"), row.get("breed_code")));
        row.put("acceptor_material_name", breedNames(row.get("species_code"), row.get("acceptor_material")));
        return row;
    }

    private String speciesNames(Object value) {
        List<String> speciesCodes = values(value);
        if (speciesCodes.isEmpty()) {
            return "";
        }
        return join(speciesCodes.stream().map(this::speciesName).collect(Collectors.toList()));
    }

    private String breedNames(Object speciesValue, Object breedValue) {
        List<String> breedCodes = values(breedValue);
        if (breedCodes.isEmpty()) {
            return "";
        }
        List<String> speciesCodes = values(speciesValue);
        if (speciesCodes.size() == 1) {
            String speciesCode = speciesCodes.get(0);
            return join(breedCodes.stream().map(breedCode -> breedName(speciesCode, breedCode)).collect(Collectors.toList()));
        }
        return join(breedCodes.stream().map(this::breedName).collect(Collectors.toList()));
    }

    private List<String> values(Object value) {
        String text = stringValue(value);
        List<String> values = new ArrayList<>();
        if (text.isEmpty()) {
            return values;
        }
        if (text.startsWith("[")) {
            try {
                JsonNode node = OBJECT_MAPPER.readTree(text);
                if (node.isArray()) {
                    for (JsonNode item : node) {
                        addValue(values, item.asText());
                    }
                    return values;
                }
            } catch (Exception ignored) {
                // Fall back to comma/single-value parsing below.
            }
        }
        if (text.contains(",")) {
            for (String item : text.split(",")) {
                addValue(values, item);
            }
            return values;
        }
        addValue(values, text);
        return values;
    }

    private void addValue(List<String> values, String value) {
        String text = stringValue(value).trim();
        if (!text.isEmpty()) {
            values.add(text);
        }
    }

    @Override
    public Class<CerVectorTaskTb> entityClass() {
        return CerVectorTaskTb.class;
    }

    @Override
    public BaseMapper<CerVectorTaskTb> mapper() {
        return cerVectorTaskTbMapper;
    }

}
