package com.bio.drqi.es.support.search.builder.biotest;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.BioSampleTestTb;
import com.bio.drqi.domain.BioSampleTestTwoResultTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.mapper.BioSampleTestTbMapper;
import com.bio.drqi.mapper.BioSampleTestTwoResultTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectBioSampleTestTwoResultSearchDocumentBuilder extends AbstractBioTestSearchDocumentBuilder<BioSampleTestTwoResultTb> {

    private final BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;
    private final BioSampleTestTbMapper bioSampleTestTbMapper;
    private final CerVectorTaskTbMapper cerVectorTaskTbMapper;

    public ProjectBioSampleTestTwoResultSearchDocumentBuilder(BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper,
                                                              BioSampleTestTbMapper bioSampleTestTbMapper,
                                                              CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        this.bioSampleTestTwoResultTbMapper = bioSampleTestTwoResultTbMapper;
        this.bioSampleTestTbMapper = bioSampleTestTbMapper;
        this.cerVectorTaskTbMapper = cerVectorTaskTbMapper;
    }

    @Override
    public String table() {
        return "bio_sample_test_two_result_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        String synResultName = synResultName(row.get("syn_result"));
        String sourceCodeName = sourceCodeName(row.get("source_code"));
        return buildDoc(row,
                stringValue(row.get("sample_code")),
                join(row.get("vector_task_code"), row.get("apply_no"), row.get("run_id"), sourceCodeName, synResultName),
                "/project/sample-test/two-result/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "实施方案编号", row.get("vector_task_code"), "小区编号", row.get("region_num"), "种植编号", row.get("seed_num"), "来源", sourceCodeName, "取样编号", row.get("sample_code"), "申请编号", row.get("apply_no"), "测序编号", row.get("run_id"), "同步结果", synResultName),
                row.values(), sourceCodeName, synResultName);
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        row.put("source_code_name", sourceCodeName(row.get("source_code")));
        row.put("syn_result_name", synResultName(row.get("syn_result")));
        return row;
    }

    @Override
    protected List<Map<String, Object>> enrichRows(List<Map<String, Object>> rows) {
        fillVectorTaskInfo(rows);
        return rows.stream()
                .map(this::enrichRowWithoutSampleQuery)
                .collect(Collectors.toList());
    }

    private Map<String, Object> enrichRowWithoutSampleQuery(Map<String, Object> row) {
        row.put("source_code_name", sourceCodeName(row.get("source_code")));
        row.put("syn_result_name", synResultName(row.get("syn_result")));
        return row;
    }

    private void fillVectorTaskInfo(List<Map<String, Object>> rowList) {
        List<String> sampleCodeList = rowList.stream()
                .map(row -> stringValue(row.get("sample_code")))
                .filter(sampleCode -> !sampleCode.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
        if (sampleCodeList.isEmpty()) {
            return;
        }
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCodeIn(sampleCodeList);
        if (bioSampleTestTbList == null || bioSampleTestTbList.isEmpty()) {
            return;
        }
        Map<String, BioSampleTestTb> sampleMap = bioSampleTestTbList.stream()
                .filter(item -> !stringValue(item.getSampleCode()).trim().isEmpty())
                .collect(Collectors.toMap(BioSampleTestTb::getSampleCode, Function.identity(), this::latestSample));
        Map<String, CerVectorTaskTb> vectorTaskMap = buildVectorTaskMap(sampleMap.values().stream()
                .map(BioSampleTestTb::getVectorTaskCode)
                .collect(Collectors.toList()));
        for (Map<String, Object> row : rowList) {
            fillSampleInfo(row, sampleMap.get(stringValue(row.get("sample_code"))), vectorTaskMap);
        }
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

    private void fillSampleInfo(Map<String, Object> row, BioSampleTestTb bioSampleTestTb, Map<String, CerVectorTaskTb> vectorTaskMap) {
        if (bioSampleTestTb == null || stringValue(bioSampleTestTb.getVectorTaskCode()).trim().isEmpty()) {
            return;
        }
        row.put("vector_task_code", bioSampleTestTb.getVectorTaskCode());
        row.put("region_num", bioSampleTestTb.getRegionNum());
        row.put("seed_num", bioSampleTestTb.getSeedNum());
        row.put("source_code", bioSampleTestTb.getSourceCode());
        CerVectorTaskTb cerVectorTaskTb = vectorTaskMap.get(bioSampleTestTb.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            return;
        }
        row.put("project_code", cerVectorTaskTb.getProjectCode());
        row.put("sub_project_code", cerVectorTaskTb.getSubProjectCode());
    }

    private Map<String, CerVectorTaskTb> buildVectorTaskMap(List<String> vectorTaskCodeList) {
        List<String> distinctVectorTaskCodeList = vectorTaskCodeList.stream()
                .filter(vectorTaskCode -> !stringValue(vectorTaskCode).trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
        if (distinctVectorTaskCodeList.isEmpty()) {
            return Collections.emptyMap();
        }
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllByVectorTaskCodeIn(distinctVectorTaskCodeList);
        if (cerVectorTaskTbList == null || cerVectorTaskTbList.isEmpty()) {
            return Collections.emptyMap();
        }
        return cerVectorTaskTbList.stream()
                .filter(item -> !stringValue(item.getVectorTaskCode()).trim().isEmpty())
                .collect(Collectors.toMap(CerVectorTaskTb::getVectorTaskCode, Function.identity(), (first, second) -> first));
    }

    private BioSampleTestTb latestSample(BioSampleTestTb first, BioSampleTestTb second) {
        Integer firstId = first.getId() == null ? 0 : first.getId();
        Integer secondId = second.getId() == null ? 0 : second.getId();
        return firstId >= secondId ? first : second;
    }

    @Override
    public Class<BioSampleTestTwoResultTb> entityClass() {
        return BioSampleTestTwoResultTb.class;
    }

    @Override
    public BaseMapper<BioSampleTestTwoResultTb> mapper() {
        return bioSampleTestTwoResultTbMapper;
    }
}
