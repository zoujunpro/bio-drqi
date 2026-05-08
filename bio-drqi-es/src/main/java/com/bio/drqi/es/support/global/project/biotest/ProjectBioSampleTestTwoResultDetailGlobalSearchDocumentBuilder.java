package com.bio.drqi.es.support.global.project.biotest;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectBioSampleTestTwoResultDetailGlobalSearchDocumentBuilder extends AbstractBioTestGlobalSearchDocumentBuilder {

    @Override
    public String table() {
        return "bio_sample_test_two_result_detail_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String confirmStatusName = confirmStatusName(row.get("confirm_status"));
        String matchFlagName = matchFlagName(row.get("match_flag"));
        return buildDoc(row,
                stringValue(row.get("sample_code")),
                join(row.get("apply_no"), row.get("run_id"), row.get("hap_id"), row.get("var_type"), confirmStatusName, matchFlagName),
                "/project/sample-test/two-result-detail/detail/",
                display("取样编号", row.get("sample_code"), "申请编号", row.get("apply_no"), "HapID", row.get("hap_id"), "变异类型", row.get("var_type"), "确认状态", confirmStatusName, "匹配状态", matchFlagName),
                row.values(), confirmStatusName, matchFlagName);
    }
}
