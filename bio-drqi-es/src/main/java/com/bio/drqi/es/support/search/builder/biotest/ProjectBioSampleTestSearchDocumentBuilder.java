package com.bio.drqi.es.support.search.builder.biotest;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.BioSampleTestTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.es.support.EsDocumentConverter;
import com.bio.drqi.mapper.BioSampleTestTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectBioSampleTestSearchDocumentBuilder extends AbstractBioTestSearchDocumentBuilder<BioSampleTestTb> {

    private static final EsDocumentConverter ES_DOCUMENT_CONVERTER = new EsDocumentConverter();

    private final BioSampleTestTbMapper bioSampleTestTbMapper;
    private final CerVectorTaskTbMapper cerVectorTaskTbMapper;

    public ProjectBioSampleTestSearchDocumentBuilder(BioSampleTestTbMapper bioSampleTestTbMapper,
                                                     CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        this.bioSampleTestTbMapper = bioSampleTestTbMapper;
        this.cerVectorTaskTbMapper = cerVectorTaskTbMapper;
    }

    @Override
    public String table() {
        return "bio_sample_test_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        String testResultName = testResultName(row.get("test_result"));
        String checkResultName = checkResultName(row.get("check_result"));
        String speciesName = speciesName(row.get("species_code"));
        String breedName = breedName(row.get("species_code"), row.get("breed_code"));
        String sourceCodeName = sourceCodeName(row.get("source_code"));
        return buildDoc(row,
                stringValue(row.get("sample_code")),
                join(row.get("vector_task_code"), row.get("apply_user_name"), speciesName, breedName, sourceCodeName, testResultName, checkResultName),
                "/project/sample-test/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "实施方案编号", row.get("vector_task_code"), "小区编号", row.get("region_num"), "种植编号", row.get("seed_num"), "取样编号", row.get("sample_code"), "申请人", row.get("apply_user_name"), "物种", speciesName, "品种", breedName, "来源", sourceCodeName, "检测结果", testResultName, "审核结果", checkResultName),
                row.values(), speciesName, breedName, sourceCodeName, testResultName, checkResultName);
    }

    @Override
    public List<Map<String, Object>> buildRows(String id) {
        List<BioSampleTestTb> rows;
        if (id == null || id.trim().isEmpty()) {
            rows = bioSampleTestTbMapper.selectList(null);
        } else {
            BioSampleTestTb row = bioSampleTestTbMapper.selectById(id);
            if (row == null) {
                return Collections.emptyList();
            }
            rows = Collections.singletonList(row);
        }
        List<Map<String, Object>> rowMapList = ES_DOCUMENT_CONVERTER.toMapList(rows);
        fillVectorTaskInfo(rowMapList);
        return rowMapList.stream()
                .map(this::enrichRowWithoutVectorTaskQuery)
                .collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        return enrichRowWithoutVectorTaskQuery(row);
    }

    private Map<String, Object> enrichRowWithoutVectorTaskQuery(Map<String, Object> row) {
        row.put("test_result_name", testResultName(row.get("test_result")));
        row.put("check_result_name", checkResultName(row.get("check_result")));
        row.put("species_name", speciesName(row.get("species_code")));
        row.put("breed_name", breedName(row.get("species_code"), row.get("breed_code")));
        row.put("source_code_name", sourceCodeName(row.get("source_code")));
        return row;
    }

    private void fillVectorTaskInfo(List<Map<String, Object>> rowList) {
        if (rowList == null || rowList.isEmpty()) {
            return;
        }
        List<String> vectorTaskCodeList = rowList.stream()
                .map(row -> stringValue(row.get("vector_task_code")))
                .filter(vectorTaskCode -> !vectorTaskCode.trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
        if (vectorTaskCodeList.isEmpty()) {
            return;
        }
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllByVectorTaskCodeIn(vectorTaskCodeList);
        if (cerVectorTaskTbList == null || cerVectorTaskTbList.isEmpty()) {
            return;
        }
        Map<String, CerVectorTaskTb> vectorTaskMap = cerVectorTaskTbList.stream()
                .filter(item -> !stringValue(item.getVectorTaskCode()).trim().isEmpty())
                .collect(Collectors.toMap(CerVectorTaskTb::getVectorTaskCode, Function.identity(), (first, second) -> first));
        for (Map<String, Object> row : rowList) {
            CerVectorTaskTb cerVectorTaskTb = vectorTaskMap.get(stringValue(row.get("vector_task_code")));
            if (cerVectorTaskTb == null) {
                continue;
            }
            row.put("project_code", cerVectorTaskTb.getProjectCode());
            row.put("sub_project_code", cerVectorTaskTb.getSubProjectCode());
        }
    }

    private void fillVectorTaskInfo(Map<String, Object> row) {
        String vectorTaskCode = stringValue(row.get("vector_task_code"));
        if (vectorTaskCode.trim().isEmpty()) {
            return;
        }
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb == null) {
            return;
        }
        row.put("project_code", cerVectorTaskTb.getProjectCode());
        row.put("sub_project_code", cerVectorTaskTb.getSubProjectCode());
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
