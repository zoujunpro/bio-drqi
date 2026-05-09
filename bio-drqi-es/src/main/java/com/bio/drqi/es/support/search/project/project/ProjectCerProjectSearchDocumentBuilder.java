package com.bio.drqi.es.support.search.project.project;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.CerProjectTb;
import com.bio.drqi.mapper.CerProjectTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectCerProjectSearchDocumentBuilder extends AbstractProjectSearchDocumentBuilder<CerProjectTb> {

    private final CerProjectTbMapper cerProjectTbMapper;

    public ProjectCerProjectSearchDocumentBuilder(CerProjectTbMapper cerProjectTbMapper) {
        this.cerProjectTbMapper = cerProjectTbMapper;
    }

    @Override
    public String table() {
        return "cer_project_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String projectStatusName = projectStatusName(row.get("project_status"));
        String projectTypeName = projectTypeName(row.get("project_type"));
        return buildDoc(row,
                stringValue(row.get("project_name")),
                join(row.get("project_code"), row.get("owner_user_name"), projectTypeName, projectStatusName),
                "/project/detail/",
                display("项目编号", row.get("project_code"), "项目类型", projectTypeName, "负责人", row.get("owner_user_name"), "状态", projectStatusName, "任务编号", row.get("task_num")),
                row.values(), projectTypeName, projectStatusName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("project_status_name", projectStatusName(row.get("project_status")));
        row.put("project_type_name", projectTypeName(row.get("project_type")));
        return row;
    }

    @Override
    protected BaseMapper<CerProjectTb> mapper() {
        return cerProjectTbMapper;
    }

}
