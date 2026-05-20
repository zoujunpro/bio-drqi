package com.bio.drqi.es.support.search.builder.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerPlasmidQualityTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.mapper.CerPlasmidQualityTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectCerPlasmidQualitySearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder<CerPlasmidQualityTb> {

    private final CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;
    private final CerVectorTaskTbMapper cerVectorTaskTbMapper;

    public ProjectCerPlasmidQualitySearchDocumentBuilder(CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper,
                                                         CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        this.cerPlasmidQualityTbMapper = cerPlasmidQualityTbMapper;
        this.cerVectorTaskTbMapper = cerVectorTaskTbMapper;
    }

    @Override
    public String table() {
        return "cer_plasmid_quality_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        String qualityInspectionResultName = qualityInspectionResultName(row.get("quality_inspection_result"));
        String taskStatusName = taskStatusName(row.get("task_status"));
        return buildDoc(row,
                stringValue(row.get("plasmid_name")),
                join(row.get("plasmid_name"), qualityInspectionResultName, row.get("create_user_name"), taskStatusName),
                "/project/plasmid-quality/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "实施方案编号", row.get("vector_task_code"), "质粒名称", row.get("plasmid_name"), "创建人", row.get("create_user_name")),
                row.values(), qualityInspectionResultName, taskStatusName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        row.put("quality_inspection_result_name", qualityInspectionResultName(row.get("quality_inspection_result")));
        row.put("task_status_name", taskStatusName(row.get("task_status")));
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
        row.put("task_status_name", taskStatusName(row.get("task_status")));
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
    public Class<CerPlasmidQualityTb> entityClass() {
        return CerPlasmidQualityTb.class;
    }

    @Override
    public BaseMapper<CerPlasmidQualityTb> mapper() {
        return cerPlasmidQualityTbMapper;
    }

}
