package com.bio.drqi.ai.toolservice.service;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.ai.toolservice.dto.AiManageQueryReqDTO;
import com.bio.drqi.ai.toolservice.dto.AiToolTableRspDTO;
import com.bio.drqi.ai.toolservice.util.AiToolTableResultBuilder;
import com.bio.drqi.manage.bio.req.BioSampleTestListDetailReqDTO;
import com.bio.drqi.manage.plant.req.PlantApplyListPageReqDTO;
import com.bio.drqi.manage.project.req.ProjectListReqDTO;
import com.bio.drqi.manage.seed.SeedStockPageReqDTO;
import com.bio.drqi.manage.service.bio.BioSampleTestService;
import com.bio.drqi.manage.service.plant.PlantApplyService;
import com.bio.drqi.manage.service.project.ProjectService;
import com.bio.drqi.manage.service.project.TransformService;
import com.bio.drqi.manage.service.project.VectorTaskService;
import com.bio.drqi.manage.service.seed.SeedStoreService;
import com.bio.drqi.manage.transform.req.TransformListPageReqDTO;
import com.bio.drqi.manage.vector.req.QueryPageVectorReqDTO;
import com.bio.drqi.manage.vector.rsp.CerImplementationPlanFullInfoRspDTO;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class AiManageQueryToolService {

    private final ProjectService projectService;

    private final VectorTaskService vectorTaskService;

    private final TransformService transformService;

    private final BioSampleTestService bioSampleTestService;

    private final PlantApplyService plantApplyService;

    private final SeedStoreService seedStoreService;

    public AiManageQueryToolService(ProjectService projectService,
                                    VectorTaskService vectorTaskService,
                                    TransformService transformService,
                                    BioSampleTestService bioSampleTestService,
                                    PlantApplyService plantApplyService,
                                    SeedStoreService seedStoreService) {
        this.projectService = projectService;
        this.vectorTaskService = vectorTaskService;
        this.transformService = transformService;
        this.bioSampleTestService = bioSampleTestService;
        this.plantApplyService = plantApplyService;
        this.seedStoreService = seedStoreService;
    }

    public AiToolTableRspDTO queryProjects(AiManageQueryReqDTO req) {
        ProjectListReqDTO query = new ProjectListReqDTO();
        applyPage(query, req);
        query.setProjectCode(firstNotBlank(req.getProjectCode(), req.getKeyword()));
        query.setProjectName(req.getProjectName());
        query.setBeginDate(req.getBeginDate());
        query.setEndDate(req.getEndDate());
        query.setProjectStatus(req.getStatus());
        query.setGeneEditMethod(req.getGeneEditMethod());
        return AiToolTableResultBuilder.page("项目查询结果", projectService.listPage(query),
                AiToolTableResultBuilder.columns(
                        "projectCode", "项目编码",
                        "projectName", "项目名称",
                        "projectStatus", "状态",
                        "projectType", "项目类型",
                        "ownerUserName", "负责人"
                ));
    }

    public AiToolTableRspDTO queryImplementationPlans(AiManageQueryReqDTO req) {
        QueryPageVectorReqDTO query = new QueryPageVectorReqDTO();
        applyPage(query, req);
        query.setVectorTaskCode(firstNotBlank(req.getVectorTaskCode(), req.getKeyword()));
        query.setProjectCode(req.getProjectCode());
        query.setSubProjectCode(req.getSubProjectCode());
        query.setTaskNum(req.getTaskNum());
        query.setTaskStatus(req.getStatus());
        query.setSpeciesCode(req.getSpeciesCode());
        query.setBreedCode(req.getBreedCode());
        return AiToolTableResultBuilder.page("实施方案查询结果", vectorTaskService.listPage(query),
                AiToolTableResultBuilder.columns(
                        "vectorTaskCode", "实施方案编码",
                        "projectCode", "项目编码",
                        "subProjectCode", "子项目编码",
                        "taskNum", "任务号",
                        "taskStatus", "状态",
                        "speciesName", "物种"
                ));
    }

    public AiToolTableRspDTO queryImplementationExecutionDetail(AiManageQueryReqDTO req) {
        String vectorTaskCode = firstNotBlank(req.getVectorTaskCode(), req.getKeyword());
        if (!hasText(vectorTaskCode)) {
            return emptyDetail("请提供实施方案编码。");
        }

        CerImplementationPlanFullInfoRspDTO fullInfo = vectorTaskService.fullInfo(vectorTaskCode);
        if (fullInfo == null || fullInfo.getPlanInfo() == null) {
            return emptyDetail("未查询到实施方案 " + vectorTaskCode + " 的执行详情。");
        }

        List<CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo> detailList =
                fullInfo.getTransformSampleSeedList() == null
                        ? new ArrayList<CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo>()
                        : fullInfo.getTransformSampleSeedList();

        int sampleCount = 0;
        int checkedSampleCount = 0;
        int harvestedCount = 0;
        int seedRecordCount = 0;
        BigDecimal seedTotal = BigDecimal.ZERO;
        BigDecimal stockOutTotal = BigDecimal.ZERO;
        List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();

        for (CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo detail : detailList) {
            if (detail == null) {
                continue;
            }
            if (hasText(detail.getSampleCode())) {
                sampleCount++;
            }
            if (isYes(detail.getTestFlag()) || hasText(detail.getTestResult())) {
                checkedSampleCount++;
            }
            if (isYes(detail.getHarvestFlag()) || hasText(detail.getSeedNum())) {
                harvestedCount++;
            }
            if (hasText(detail.getSeedNum())) {
                seedRecordCount++;
            }
            seedTotal = seedTotal.add(safeNumber(detail.getSeedNumber()));
            stockOutTotal = stockOutTotal.add(safeNumber(detail.getStockOutNumber()));
            rows.add(toMap(detail));
        }

        Map<String, Object> planInfo = toMap(fullInfo.getPlanInfo());
        Map<String, Object> summary = new LinkedHashMap<String, Object>();
        summary.put("vectorTaskCode", fullInfo.getPlanInfo().getVectorTaskCode());
        summary.put("projectCode", fullInfo.getPlanInfo().getProjectCode());
        summary.put("subProjectCode", fullInfo.getPlanInfo().getSubProjectCode());
        summary.put("taskNum", fullInfo.getPlanInfo().getTaskNum());
        summary.put("currentStepCode", fullInfo.getPlanInfo().getCurrentStepCode());
        summary.put("currentStepName", ImplementationPlanTypeEnum.getDesc(fullInfo.getPlanInfo().getCurrentStepCode()));
        summary.put("sampled", sampleCount > 0);
        summary.put("sampleCount", sampleCount);
        summary.put("checkedSampleCount", checkedSampleCount);
        summary.put("harvested", harvestedCount > 0);
        summary.put("harvestCount", harvestedCount);
        summary.put("seedRecordCount", seedRecordCount);
        summary.put("seedTotal", seedTotal);
        summary.put("stockOutTotal", stockOutTotal);

        Map<String, Object> data = new LinkedHashMap<String, Object>();
        data.put("summary", summary);
        data.put("planInfo", planInfo);
        data.put("plasmidPrimerList", fullInfo.getPlasmidPrimerList());
        data.put("transformSampleSeedList", detailList);

        String answer = buildExecutionDetailAnswer(vectorTaskCode, summary);
        return AiToolTableRspDTO.builder()
                .resultType("TABLE")
                .answer(answer)
                .summary(answer)
                .table(AiToolTableRspDTO.Table.builder()
                        .columns(AiToolTableResultBuilder.columns(
                                "transformCode", "转化编码",
                                "sampleCode", "取样/种植编号",
                                "testFlag", "是否检测",
                                "testResult", "检测结果",
                                "harvestFlag", "是否收获",
                                "seedNum", "种子编号",
                                "generation", "世代",
                                "seedNumber", "种子数量",
                                "unit", "单位"
                        ))
                        .rows(rows)
                        .total((long) rows.size())
                        .build())
                .data(data)
                .build();
    }

    public AiToolTableRspDTO queryTransforms(AiManageQueryReqDTO req) {
        TransformListPageReqDTO query = new TransformListPageReqDTO();
        applyPage(query, req);
        query.setTransformCode(firstNotBlank(req.getTransformCode(), req.getKeyword()));
        query.setVectorTaskCode(req.getVectorTaskCode());
        query.setProjectCode(req.getProjectCode());
        query.setSubProjectCode(req.getSubProjectCode());
        query.setTaskNum(req.getTaskNum());
        query.setSpeciesCode(req.getSpeciesCode());
        return AiToolTableResultBuilder.page("转化查询结果", transformService.listPage(query),
                AiToolTableResultBuilder.columns(
                        "transformCode", "转化编码",
                        "vectorTaskCode", "实施方案编码",
                        "projectCode", "项目编码",
                        "subProjectCode", "子项目编码",
                        "taskNum", "任务号",
                        "speciesName", "物种"
                ));
    }

    public AiToolTableRspDTO querySampleTests(AiManageQueryReqDTO req) {
        BioSampleTestListDetailReqDTO query = new BioSampleTestListDetailReqDTO();
        applyPage(query, req);
        query.setSampleCode(firstNotBlank(req.getSampleCode(), req.getKeyword()));
        query.setApplyNo(req.getApplyNo());
        query.setVectorTaskCode(req.getVectorTaskCode());
        query.setTransformCode(req.getTransformCode());
        query.setSeedNum(req.getSeedNum());
        query.setTestResult(req.getStatus());
        return AiToolTableResultBuilder.page("取样检测查询结果", bioSampleTestService.listPage(query),
                AiToolTableResultBuilder.columns(
                        "sampleCode", "样品编码",
                        "applyNo", "申请单号",
                        "vectorTaskCode", "实施方案编码",
                        "transformCode", "转化编码",
                        "seedNum", "种子编号",
                        "testResult", "检测结果"
                ));
    }

    public AiToolTableRspDTO queryPlantApplies(AiManageQueryReqDTO req) {
        PlantApplyListPageReqDTO query = PlantApplyListPageReqDTO.builder()
                .plantApplyNum(firstNotBlank(req.getPlantApplyNum(), req.getKeyword()))
                .vectorTaskCode(req.getVectorTaskCode())
                .pdImplementCode(req.getPdImplementCode())
                .speciesCode(req.getSpeciesCode())
                .experimentType(req.getExperimentType())
                .build();
        applyPage(query, req);
        return AiToolTableResultBuilder.page("种植查询结果", plantApplyService.listPage(query),
                AiToolTableResultBuilder.columns(
                        "plantApplyNum", "种植申请号",
                        "vectorTaskCode", "实施方案编码",
                        "pdImplementCode", "生产实施编码",
                        "speciesName", "物种",
                        "experimentType", "实验类型",
                        "applyStatus", "状态"
                ));
    }

    public AiToolTableRspDTO querySeedStocks(AiManageQueryReqDTO req) {
        SeedStockPageReqDTO query = new SeedStockPageReqDTO();
        applyPage(query, req);
        query.setSeedNum(firstNotBlank(req.getSeedNum(), req.getKeyword()));
        query.setProjectCode(req.getProjectCode());
        query.setVectorTaskCode(req.getVectorTaskCode());
        query.setPdImplementCode(req.getPdImplementCode());
        query.setBreedCode(req.getBreedCode());
        query.setSpecies(req.getSpeciesCode());
        query.setBeginDate(req.getBeginDate());
        query.setEndDate(req.getEndDate());
        query.setSeedType(req.getSeedType());
        query.setGeneration(req.getGeneration());
        return AiToolTableResultBuilder.page("种子库查询结果", seedStoreService.listPage(query),
                AiToolTableResultBuilder.columns(
                        "seedNum", "种子编号",
                        "projectCode", "项目编码",
                        "vectorTaskCode", "实施方案编码",
                        "pdImplementCode", "生产实施编码",
                        "species", "物种",
                        "breedName", "品种",
                        "generation", "世代"
                ));
    }

    private void applyPage(com.bio.drqi.manage.base.PageDTO target, AiManageQueryReqDTO req) {
        target.setPageNum(req.safePageNum());
        target.setPageSize(req.safePageSize());
    }

    private void applyPage(com.bio.drqi.common.dto.PageDTO target, AiManageQueryReqDTO req) {
        target.setPageNum(req.safePageNum());
        target.setPageSize(req.safePageSize());
    }

    private String firstNotBlank(String first, String second) {
        if (first != null && first.trim().length() > 0) {
            return first;
        }
        return second;
    }

    private AiToolTableRspDTO emptyDetail(String answer) {
        return AiToolTableRspDTO.builder()
                .resultType("TABLE")
                .answer(answer)
                .summary(answer)
                .table(AiToolTableRspDTO.Table.builder()
                        .columns(AiToolTableResultBuilder.columns(
                                "transformCode", "转化编码",
                                "sampleCode", "取样/种植编号",
                                "testResult", "检测结果",
                                "seedNum", "种子编号"
                        ))
                        .rows(new ArrayList<Map<String, Object>>())
                        .total(0L)
                        .build())
                .data(new LinkedHashMap<String, Object>())
                .build();
    }

    private String buildExecutionDetailAnswer(String vectorTaskCode, Map<String, Object> summary) {
        StringBuilder builder = new StringBuilder();
        builder.append("实施方案 ").append(vectorTaskCode).append(" 执行详情：");
        Object currentStepName = summary.get("currentStepName");
        if (currentStepName instanceof String && hasText((String) currentStepName)) {
            builder.append("当前执行到").append(currentStepName).append("；");
        }
        builder.append("取样 ").append(Boolean.TRUE.equals(summary.get("sampled")) ? "已发生" : "未发生");
        builder.append("，取样记录 ").append(summary.get("sampleCount")).append(" 条");
        builder.append("，已检测 ").append(summary.get("checkedSampleCount")).append(" 条");
        builder.append("；收获 ").append(Boolean.TRUE.equals(summary.get("harvested")) ? "已发生" : "未发生");
        builder.append("，种子记录 ").append(summary.get("seedRecordCount")).append(" 条");
        builder.append("，种子数量合计 ").append(summary.get("seedTotal")).append("。");
        return builder.toString();
    }

    private Map<String, Object> toMap(Object value) {
        if (value == null) {
            return new LinkedHashMap<String, Object>();
        }
        return JSON.parseObject(JSON.toJSONString(value), new TypeReference<Map<String, Object>>() {
        });
    }

    private boolean isYes(String value) {
        if (!hasText(value)) {
            return false;
        }
        String normalized = value.trim();
        return "Y".equalsIgnoreCase(normalized)
                || "YES".equalsIgnoreCase(normalized)
                || "1".equals(normalized)
                || "是".equals(normalized)
                || "已".equals(normalized)
                || "true".equalsIgnoreCase(normalized);
    }

    private BigDecimal safeNumber(BigDecimal value) {
        return value == null ? BigDecimal.ZERO : value;
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
