package com.bio.drqi.es.support.search.project.task;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectBioTaskSearchDocumentBuilder extends AbstractTaskSearchDocumentBuilder<BioTaskDtlTb> {

    private static final String TABLE = "bio_task_dtl_tb";

    private final BioTaskDtlTbMapper bioTaskDtlTbMapper;

    public ProjectBioTaskSearchDocumentBuilder(BioTaskDtlTbMapper bioTaskDtlTbMapper) {
        this.bioTaskDtlTbMapper = bioTaskDtlTbMapper;
    }

    @Override
    public String table() {
        return TABLE;
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String taskFormValues = extractJsonValues(row.get("task_form"));
        String taskStatusName = taskStatusName(row.get("task_status"));
        return buildDoc(row,
                stringValue(row.get("task_num")),
                join(row.get("task_type_name"), row.get("apply_user_name"), taskStatusName, row.get("task_desc")),
                "/task/detail/",
                display("任务类型", row.get("task_type_name"), "申请人", row.get("apply_user_name"), "状态", taskStatusName, "申请时间", row.get("apply_time")),
                row.values(), taskStatusName, row.get("task_desc"), taskFormValues);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("task_status_name", taskStatusName(row.get("task_status")));
        row.put("task_form_values", extractJsonValues(row.get("task_form")));
        return row;
    }

    @Override
    protected BaseMapper<BioTaskDtlTb> mapper() {
        return bioTaskDtlTbMapper;
    }

}
