package com.bio.drqi.es.support.search.builder.biotest;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.BioSampleTestTwoResultDetailTb;
import com.bio.drqi.mapper.BioSampleTestTwoResultDetailTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectBioSampleTestTwoResultDetailSearchDocumentBuilder extends AbstractBioTestSearchDocumentBuilder<BioSampleTestTwoResultDetailTb> {

    private final BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;

    public ProjectBioSampleTestTwoResultDetailSearchDocumentBuilder(BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper) {
        this.bioSampleTestTwoResultDetailTbMapper = bioSampleTestTwoResultDetailTbMapper;
    }

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

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("confirm_status_name", confirmStatusName(row.get("confirm_status")));
        row.put("match_flag_name", matchFlagName(row.get("match_flag")));
        return row;
    }

    @Override
    public Class<BioSampleTestTwoResultDetailTb> entityClass() {
        return BioSampleTestTwoResultDetailTb.class;
    }

    @Override
    public BaseMapper<BioSampleTestTwoResultDetailTb> mapper() {
        return bioSampleTestTwoResultDetailTbMapper;
    }
}
