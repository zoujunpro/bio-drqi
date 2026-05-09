package com.bio.drqi.es.support.search.builder.biotest;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.BioSampleTestTb;
import com.bio.drqi.mapper.BioSampleTestTbMapper;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ProjectBioSampleTestSearchDocumentBuilder extends AbstractBioTestSearchDocumentBuilder<BioSampleTestTb> {

    private final BioSampleTestTbMapper bioSampleTestTbMapper;

    public ProjectBioSampleTestSearchDocumentBuilder(BioSampleTestTbMapper bioSampleTestTbMapper) {
        this.bioSampleTestTbMapper = bioSampleTestTbMapper;
    }

    @Override
    public String table() {
        return "bio_sample_test_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        String testResultName = testResultName(row.get("test_result"));
        String checkResultName = checkResultName(row.get("check_result"));
        String speciesName = speciesName(row.get("species_code"));
        String breedName = breedName(row.get("species_code"), row.get("breed_code"));
        String sourceCodeName = sourceCodeName(row.get("source_code"));
        return buildDoc(row,
                stringValue(row.get("sample_code")),
                join(row.get("vector_task_code"), row.get("apply_user_name"), speciesName, breedName, sourceCodeName, testResultName, checkResultName),
                "/project/sample-test/detail/",
                display("取样编号", row.get("sample_code"), "载体任务", row.get("vector_task_code"), "申请人", row.get("apply_user_name"), "物种", speciesName, "品种", breedName, "来源", sourceCodeName, "检测结果", testResultName, "审核结果", checkResultName),
                row.values(), speciesName, breedName, sourceCodeName, testResultName, checkResultName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        row.put("test_result_name", testResultName(row.get("test_result")));
        row.put("check_result_name", checkResultName(row.get("check_result")));
        row.put("species_name", speciesName(row.get("species_code")));
        row.put("breed_name", breedName(row.get("species_code"), row.get("breed_code")));
        row.put("source_code_name", sourceCodeName(row.get("source_code")));
        return row;
    }

    @Override
    public Class<BioSampleTestTb> entityClass() {
        return BioSampleTestTb.class;
    }

    @Override
    public BaseMapper<BioSampleTestTb> mapper() {
        return bioSampleTestTbMapper;
    }

}
