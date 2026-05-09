package com.bio.drqi.es.support.search.project.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerVectorTaskSearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder<CerVectorTaskTb> {

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
        return buildDoc(row,
                stringValue(row.get("vector_task_code")),
                join(row.get("project_code"), row.get("sub_project_code"), row.get("acceptor_material"), taskStatusName),
                "/project/vector-task/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "载体任务", row.get("vector_task_code"), "受体材料", row.get("acceptor_material"), "状态", taskStatusName),
                row.values(), taskStatusName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("task_status_name", taskStatusName(row.get("task_status")));
        return row;
    }

    @Override
    protected BaseMapper<CerVectorTaskTb> mapper() {
        return cerVectorTaskTbMapper;
    }

}
