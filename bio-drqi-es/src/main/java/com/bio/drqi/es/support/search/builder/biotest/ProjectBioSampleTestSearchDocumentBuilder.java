package com.bio.drqi.es.support.search.builder.biotest;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.drqi.domain.BioSampleTestTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.es.support.EsDocumentConverter;
import com.bio.drqi.mapper.BioSampleTestTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ProjectBioSampleTestSearchDocumentBuilder extends AbstractBioTestSearchDocumentBuilder<BioSampleTestTb> {

    private static final EsDocumentConverter ES_DOCUMENT_CONVERTER = new EsDocumentConverter();
    private static final int BATCH_SIZE = 2000;

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
            return buildAllRowsByBatch();
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

    private List<Map<String, Object>> buildAllRowsByBatch() {
        List<Map<String, Object>> result = new ArrayList<>();
        buildAllRowsByBatch(batch -> result.addAll(batch));
        return result;
    }

    @Override
    public void buildRows(String id, Consumer<List<Map<String, Object>>> batchConsumer) {
        if (id == null || id.trim().isEmpty()) {
            buildAllRowsByBatch(batchConsumer);
            return;
        }
        batchConsumer.accept(buildRows(id));
    }

    private void buildAllRowsByBatch(Consumer<List<Map<String, Object>>> batchConsumer) {
        int lastId = 0;
        int batchNo = 1;
        int total = 0;
        long start = System.currentTimeMillis();
        while (true) {
            long batchStart = System.currentTimeMillis();
            log.info("取样检测全量构建批次查询开始 batchNo={}, lastId={}, limit={}", batchNo, lastId, BATCH_SIZE);
            List<BioSampleTestTb> rows = bioSampleTestTbMapper.selectAllByIdGreaterThanOrderByIdAscLimit(lastId, BATCH_SIZE);
            if (rows == null || rows.isEmpty()) {
                log.info("取样检测全量构建批次查询结束，无更多数据 batchNo={}, total={}, costMs={}",
                        batchNo, total, System.currentTimeMillis() - start);
                break;
            }
            lastId = rows.get(rows.size() - 1).getId();
            List<Map<String, Object>> rowMapList = ES_DOCUMENT_CONVERTER.toMapList(rows);
            fillVectorTaskInfo(rowMapList);
            List<Map<String, Object>> batch = rowMapList.stream()
                    .map(this::enrichRowWithoutVectorTaskQuery)
                    .collect(Collectors.toList());
            total += batch.size();
            log.info("取样检测全量构建批次完成 batchNo={}, rows={}, lastId={}, total={}, costMs={}",
                    batchNo, rows.size(), lastId, total, System.currentTimeMillis() - batchStart);
            batchConsumer.accept(batch);
            if (rows.size() < BATCH_SIZE) {
                break;
            }
            batchNo++;
        }
        log.info("取样检测全量构建完成 total={}, costMs={}", total, System.currentTimeMillis() - start);
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
