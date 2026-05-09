package com.bio.drqi.es.support.search.project.project;

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
        return buildDoc(row,
                stringValue(row.get("transform_code")),
                join(row.get("project_code"), row.get("vector_task_code"), row.get("plasmid_name"), taskStatusName),
                "/project/transform/detail/",
                display("转化编号", row.get("transform_code"), "项目编号", row.get("project_code"), "载体任务", row.get("vector_task_code"), "质粒名称", row.get("plasmid_name"), "状态", taskStatusName),
                row.values(), taskStatusName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("task_status_name", taskStatusName(row.get("task_status")));
        return row;
    }

    @Override
    protected BaseMapper<CerTransformTb> mapper() {
        return cerTransformTbMapper;
    }

}
