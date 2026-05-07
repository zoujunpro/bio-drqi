package com.bio.drqi.es.support.global.project;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectBioSampleTestGlobalSearchDocumentBuilder extends AbstractProjectGlobalSearchDocumentBuilder {

    @Override
    public String table() {
        return "bio_sample_test_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        return buildDoc(row,
                stringValue(row.get("sample_code")),
                join(row.get("vector_task_code"), row.get("apply_user_name"), row.get("test_result")),
                "/project/sample-test/detail/",
                display("取样编号", row.get("sample_code"), "载体任务", row.get("vector_task_code"), "申请人", row.get("apply_user_name"), "检测结果", row.get("test_result")),
                row.values());
    }
}
