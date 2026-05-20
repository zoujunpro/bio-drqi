package com.bio.drqi.es.support.search.builder.biotest;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.BioSampleTestTb;
import com.bio.drqi.domain.BioSampleTestOneResultTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.es.support.EsDocumentConverter;
import com.bio.drqi.mapper.BioSampleTestTbMapper;
import com.bio.drqi.mapper.BioSampleTestOneResultTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ProjectBioSampleTestOneResultSearchDocumentBuilder extends AbstractBioTestSearchDocumentBuilder<BioSampleTestOneResultTb> {

    private static final EsDocumentConverter ES_DOCUMENT_CONVERTER = new EsDocumentConverter();

    private final BioSampleTestOneResultTbMapper bioSampleTestOneResultTbMapper;
    private final BioSampleTestTbMapper bioSampleTestTbMapper;
    private final CerVectorTaskTbMapper cerVectorTaskTbMapper;

    public ProjectBioSampleTestOneResultSearchDocumentBuilder(BioSampleTestOneResultTbMapper bioSampleTestOneResultTbMapper,
                                                              BioSampleTestTbMapper bioSampleTestTbMapper,
                                                              CerVectorTaskTbMapper cerVectorTaskTbMapper) {
        this.bioSampleTestOneResultTbMapper = bioSampleTestOneResultTbMapper;
        this.bioSampleTestTbMapper = bioSampleTestTbMapper;
        this.cerVectorTaskTbMapper = cerVectorTaskTbMapper;
    }

    @Override
    public String table() {
        return "bio_sample_test_one_result_tb";
    }

    @Override
    public Map<String, Object> build(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        String sourceCodeName = sourceCodeName(row.get("source_code"));
        return buildDoc(row,
                stringValue(row.get("sample_code")),
                join(row.get("task_num"), row.get("upload_num"), row.get("test_user_name"), sourceCodeName),
                "/project/sample-test/one-result/detail/",
                display("项目编号", row.get("project_code"), "子项目编号", row.get("sub_project_code"), "实施方案编号", row.get("vector_task_code"), "小区编号", row.get("region_num"), "种植编号", row.get("seed_num"), "来源", sourceCodeName, "取样编号", row.get("sample_code"), "任务编号", row.get("task_num"), "上传编号", row.get("upload_num"), "检测人", row.get("test_user_name")),
                row.values(), sourceCodeName);
    }

    @Override
    public List<Map<String, Object>> buildRows(String id) {
        List<BioSampleTestOneResultTb> rows;
        if (id == null || id.trim().isEmpty()) {
            rows = bioSampleTestOneResultTbMapper.selectList(null);
        } else {
            BioSampleTestOneResultTb row = bioSampleTestOneResultTbMapper.selectById(id);
            if (row == null) {
                return Collections.emptyList();
            }
            rows = Collections.singletonList(row);
        }
        List<Map<String, Object>> rowMapList = ES_DOCUMENT_CONVERTER.toMapList(rows);
        fillVectorTaskInfo(rowMapList);
        return rowMapList.stream()
                .map(this::enrichRowWithoutSampleQuery)
                .collect(Collectors.toList());
    }

    @Override
    protected Map<String, Object> enrichRow(Map<String, Object> row) {
        fillVectorTaskInfo(row);
        return enrichRowWithoutSampleQuery(row);
    }

    private Map<String, Object> enrichRowWithoutSampleQuery(Map<String, Object> row) {
        row.put("source_code_name", sourceCodeName(row.get("source_code")));
        return row;
    }

    private void fillVectorTaskInfo(List<Map<String, Object>> rowList) {
        if (rowList == null || rowList.isEmpty()) {
            return;
        }
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
        List<String> vectorTaskCodeList = sampleMap.values().stream()
                .map(BioSampleTestTb::getVectorTaskCode)
                .filter(vectorTaskCode -> !stringValue(vectorTaskCode).trim().isEmpty())
                .distinct()
                .collect(Collectors.toList());
        Map<String, CerVectorTaskTb> vectorTaskMap = Collections.emptyMap();
        if (!vectorTaskCodeList.isEmpty()) {
            List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllByVectorTaskCodeIn(vectorTaskCodeList);
            if (cerVectorTaskTbList != null && !cerVectorTaskTbList.isEmpty()) {
                vectorTaskMap = cerVectorTaskTbList.stream()
                        .filter(item -> !stringValue(item.getVectorTaskCode()).trim().isEmpty())
                        .collect(Collectors.toMap(CerVectorTaskTb::getVectorTaskCode, Function.identity(), (first, second) -> first));
            }
        }
        for (Map<String, Object> row : rowList) {
            BioSampleTestTb bioSampleTestTb = sampleMap.get(stringValue(row.get("sample_code")));
            if (bioSampleTestTb == null || stringValue(bioSampleTestTb.getVectorTaskCode()).trim().isEmpty()) {
                continue;
            }
            row.put("vector_task_code", bioSampleTestTb.getVectorTaskCode());
            row.put("region_num", bioSampleTestTb.getRegionNum());
            row.put("seed_num", bioSampleTestTb.getSeedNum());
            row.put("source_code", bioSampleTestTb.getSourceCode());
            CerVectorTaskTb cerVectorTaskTb = vectorTaskMap.get(bioSampleTestTb.getVectorTaskCode());
            if (cerVectorTaskTb == null) {
                continue;
            }
            row.put("project_code", cerVectorTaskTb.getProjectCode());
            row.put("sub_project_code", cerVectorTaskTb.getSubProjectCode());
        }
    }

    private BioSampleTestTb latestSample(BioSampleTestTb first, BioSampleTestTb second) {
        Integer firstId = first.getId() == null ? 0 : first.getId();
        Integer secondId = second.getId() == null ? 0 : second.getId();
        return firstId >= secondId ? first : second;
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
    public Class<BioSampleTestOneResultTb> entityClass() {
        return BioSampleTestOneResultTb.class;
    }

    @Override
    public BaseMapper<BioSampleTestOneResultTb> mapper() {
        return bioSampleTestOneResultTbMapper;
    }

}
