package com.bio.drqi.es.support.search.builder.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerTransformTb;
import com.bio.drqi.mapper.CerTransformTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerTransformSearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder<CerTransformTb> {

    private final CerTransformTbMapper cerTransformTbMapper;

    public ProjectCerTransformSearchDocumentBuilder(CerTransformTbMapper cerTransformTbMapper) {
        this.cerTransformTbMapper = cerTransformTbMapper;
    }

    @Override
    public String table() {
        return "cer_transform_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String taskStatusName = taskStatusName(row.get("task_status"));
        String speciesName = speciesName(row.get("species_code"));
        String breedName = breedName(row.get("species_code"), row.get("breed_code"));
        return buildDoc(row,
                stringValue(row.get("transform_code")),
                join(row.get("project_code"), row.get("vector_task_code"), row.get("plasmid_name"), speciesName, breedName, taskStatusName),
                "/project/transform/detail/",
                display( "项目编号", row.get("project_code"), "子项目编号",row.get("subProjectCode"),"实施方案编号", row.get("vector_task_code"),"转化编号", row.get("transform_code"), "质粒名称", row.get("plasmid_name"), "物种", speciesName, "品种", breedName, "创建人", row.get("create_user_name")),
                row.values(), speciesName, breedName, taskStatusName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("task_status_name", taskStatusName(row.get("task_status")));
        row.put("species_name", speciesName(row.get("species_code")));
        row.put("breed_name", breedName(row.get("species_code"), row.get("breed_code")));
        return row;
    }

    @Override
    public Class<CerTransformTb> entityClass() {
        return CerTransformTb.class;
    }

    @Override
    public BaseMapper<CerTransformTb> mapper() {
        return cerTransformTbMapper;
    }

}
