package com.bio.drqi.es.support.global.project.biotest;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectBioSampleTestTwoResultGlobalSearchDocumentBuilder extends AbstractBioTestGlobalSearchDocumentBuilder {

    @Override
    public String table() {
        return "bio_sample_test_two_result_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String synResultName = synResultName(row.get("syn_result"));
        return buildDoc(row,
                stringValue(row.get("sample_code")),
                join(row.get("apply_no"), row.get("run_id"), synResultName),
                "/project/sample-test/two-result/detail/",
                display("取样编号", row.get("sample_code"), "申请编号", row.get("apply_no"), "测序编号", row.get("run_id"), "同步结果", synResultName),
                row.values(), synResultName);
    }
}
