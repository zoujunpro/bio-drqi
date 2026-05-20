package com.bio.drqi.es.support.search.builder.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerVectorTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.mapper.CerVectorTbMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectCerVectorSearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder<CerVectorTb> {

    private final CerVectorTbMapper cerVectorTbMapper;
    private final CerVectorTaskTbMapper cerVectorTaskTbMapper;

    public ProjectCerVectorSearchDocumentBuilder(CerVectorTbMapper cerVectorTbMapper,
                                                 CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        this.cerVectorTbMapper = cerVectorTbMapper;
        this.cerVectorTaskTbMapper = cerVectorTaskTbMapper;
    }

    @Override
    public String table() {
        return "cer_vector_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        String qualityInspectionResultName = qualityInspectionResultName(row.get("quality_inspection_result"));
        return buildDoc(row,
                stringValue(row.get("plasmid_name")),
                join(row.get("vector_task_code"), row.get("target_gene"), row.get("target_site"), qualityInspectionResultName),
                "/project/vector/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "实施方案编号", row.get("vector_task_code"), "质粒名称", row.get("plasmid_name"), "创建人", row.get("create_user_name")),
                row.values(), qualityInspectionResultName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        row.put("quality_inspection_result_name", qualityInspectionResultName(row.get("quality_inspection_result")));
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
        row.put("quality_inspection_result_name", qualityInspectionResultName(row.get("quality_inspection_result")));
        return row;
    }

    private void fillVectorTaskInfo(List<Map<String, Object>> rows) {
        List<String> vectorTaskCodeList = rows.stream()
                .filter(row -> stringValue(row.get("project_code")).trim().isEmpty()
                        || stringValue(row.get("sub_project_code")).trim().isEmpty())
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
        if (!stringValue(row.get("project_code")).trim().isEmpty()
                && !stringValue(row.get("sub_project_code")).trim().isEmpty()) {
            return;
        }
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
    public Class<CerVectorTb> entityClass() {
        return CerVectorTb.class;
    }

    @Override
    public BaseMapper<CerVectorTb> mapper() {
        return cerVectorTbMapper;
    }

}
