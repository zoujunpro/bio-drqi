package com.bio.drqi.es.support.search.builder.biotest;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.BioSampleTestTb;
import com.bio.drqi.domain.BioSampleTestTwoResultDetailTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.mapper.BioSampleTestTbMapper;
import com.bio.drqi.mapper.BioSampleTestTwoResultDetailTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectBioSampleTestTwoResultDetailSearchDocumentBuilder extends AbstractBioTestSearchDocumentBuilder<BioSampleTestTwoResultDetailTb> {

    private final BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;
    private final BioSampleTestTbMapper bioSampleTestTbMapper;
    private final CerVectorTaskTbMapper cerVectorTaskTbMapper;

    public ProjectBioSampleTestTwoResultDetailSearchDocumentBuilder(BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper,
                                                                    BioSampleTestTbMapper bioSampleTestTbMapper,
                                                                    CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        this.bioSampleTestTwoResultDetailTbMapper = bioSampleTestTwoResultDetailTbMapper;
        this.bioSampleTestTbMapper = bioSampleTestTbMapper;
        this.cerVectorTaskTbMapper = cerVectorTaskTbMapper;
    }

    @Override
    public String table() {
        return "bio_sample_test_two_result_detail_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        String confirmStatusName = confirmStatusName(row.get("confirm_status"));
        String matchFlagName = matchFlagName(row.get("match_flag"));
        String sourceCodeName = sourceCodeName(row.get("source_code"));
        return buildDoc(row,
                stringValue(row.get("sample_code")),
                join(row.get("vector_task_code"), row.get("apply_no"), row.get("run_id"), row.get("hap_id"), row.get("var_type"), sourceCodeName, confirmStatusName, matchFlagName),
                "/project/sample-test/two-result-detail/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "实施方案编号", row.get("vector_task_code"), "小区编号", row.get("region_num"), "种植编号", row.get("seed_num"), "来源", sourceCodeName, "取样编号", row.get("sample_code"), "申请编号", row.get("apply_no"), "HapID", row.get("hap_id"), "变异类型", row.get("var_type"), "确认状态", confirmStatusName, "匹配状态", matchFlagName),
                row.values(), sourceCodeName, confirmStatusName, matchFlagName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        row.put("source_code_name", sourceCodeName(row.get("source_code")));
        row.put("confirm_status_name", confirmStatusName(row.get("confirm_status")));
        row.put("match_flag_name", matchFlagName(row.get("match_flag")));
        return row;
    }

    private void fillVectorTaskInfo(Map<String, Object> row) {
        String sampleCode = stringValue(row.get("sample_code"));
        if (sampleCode.trim().isEmpty()) {
            return;
        }
        BioSampleTestTb bioSampleTestTb = bioSampleTestTbMapper.selectOneBySampleCodeOrderByIdDesc(sampleCode);
        if (bioSampleTestTb == null || stringValue(bioSampleTestTb.getVectorTaskCode()).trim().isEmpty()) {
            return;
        }
        row.put("vector_task_code", bioSampleTestTb.getVectorTaskCode());
        row.put("region_num", bioSampleTestTb.getRegionNum());
        row.put("seed_num", bioSampleTestTb.getSeedNum());
        row.put("source_code", bioSampleTestTb.getSourceCode());
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(bioSampleTestTb.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            return;
        }
        row.put("project_code", cerVectorTaskTb.getProjectCode());
        row.put("sub_project_code", cerVectorTaskTb.getSubProjectCode());
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
