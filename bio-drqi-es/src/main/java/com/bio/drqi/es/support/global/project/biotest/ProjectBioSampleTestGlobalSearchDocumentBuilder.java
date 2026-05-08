package com.bio.drqi.es.support.global.project.biotest;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectBioSampleTestGlobalSearchDocumentBuilder extends AbstractBioTestGlobalSearchDocumentBuilder {

    @Override
    public String table() {
        return "bio_sample_test_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String testResultName = testResultName(row.get("test_result"));
        String checkResultName = checkResultName(row.get("check_result"));
        return buildDoc(row,
                stringValue(row.get("sample_code")),
                join(row.get("vector_task_code"), row.get("apply_user_name"), testResultName, checkResultName),
                "/project/sample-test/detail/",
                display("取样编号", row.get("sample_code"), "载体任务", row.get("vector_task_code"), "申请人", row.get("apply_user_name"), "检测结果", testResultName, "审核结果", checkResultName),
                row.values(), testResultName, checkResultName);
    }
}
