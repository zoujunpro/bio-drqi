package com.bio.drqi.es.support.search.builder.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerSubProjectTb;
import com.bio.drqi.mapper.CerSubProjectTbMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ProjectCerSubProjectSearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder<CerSubProjectTb> {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    private final CerSubProjectTbMapper cerSubProjectTbMapper;

    public ProjectCerSubProjectSearchDocumentBuilder(CerSubProjectTbMapper cerSubProjectTbMapper) {
        this.cerSubProjectTbMapper = cerSubProjectTbMapper;
    }

    @Override
    public String table() {
        return "cer_sub_project_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String taskStatusName = taskStatusName(row.get("task_status"));
        String speciesName = speciesNames(row.get("species_code"));
        return buildDoc(row,
                stringValue(row.get("sub_project_code")),
                join(row.get("project_code"), row.get("create_user_name"), speciesName, taskStatusName),
                "/project/sub/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "创建人", row.get("create_user_name"), "物种", speciesName, "状态", taskStatusName),
                row.values(), speciesName, taskStatusName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("task_status_name", taskStatusName(row.get("task_status")));
        row.put("species_name", speciesNames(row.get("species_code")));
        return row;
    }

    private String speciesNames(Object value) {
        List<String> speciesCodes = speciesCodes(value);
        if (speciesCodes.isEmpty()) {
            return "";
        }
        return join(speciesCodes.stream().map(this::speciesName).collect(Collectors.toList()));
    }

    private List<String> speciesCodes(Object value) {
        String text = stringValue(value);
        List<String> speciesCodes = new ArrayList<>();
        if (text.isEmpty()) {
            return speciesCodes;
        }
        if (text.startsWith("[")) {
            try {
                JsonNode node = OBJECT_MAPPER.readTree(text);
                if (node.isArray()) {
                    for (JsonNode item : node) {
                        addSpeciesCode(speciesCodes, item.asText());
                    }
                    return speciesCodes;
                }
            } catch (Exception ignored) {
                // Fall back to comma/single-value parsing below.
            }
        }
        if (text.contains(",")) {
            for (String item : text.split(",")) {
                addSpeciesCode(speciesCodes, item);
            }
            return speciesCodes;
        }
        addSpeciesCode(speciesCodes, text);
        return speciesCodes;
    }

    private void addSpeciesCode(List<String> speciesCodes, String speciesCode) {
        String value = stringValue(speciesCode).trim();
        if (!value.isEmpty()) {
            speciesCodes.add(value);
        }
    }

    @Override
    public Class<CerSubProjectTb> entityClass() {
        return CerSubProjectTb.class;
    }

    @Override
    public BaseMapper<CerSubProjectTb> mapper() {
        return cerSubProjectTbMapper;
    }
}
