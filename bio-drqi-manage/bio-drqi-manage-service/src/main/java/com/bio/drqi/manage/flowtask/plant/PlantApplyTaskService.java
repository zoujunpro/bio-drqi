package com.bio.drqi.manage.flowtask.plant;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.ExperimentTypeEnum;
import com.bio.drqi.common.enums.SampleGroupPergixEnum;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.common.util.LetterUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.plant.ExperimentExcelDTO;
import com.bio.drqi.manage.dto.plant.task.PlantExperimentTaskDTO;
import com.bio.drqi.mapper.*;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("plant_apply_task")
@Slf4j
public class PlantApplyTaskService extends AbstractPlantBaseTaskService {

    @Resource
    private OssService ossService;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private PlantApplyTbMapper plantApplyTbMapper;


    @Resource
    private PlantApplyDetailTbMapper plantApplyDetailTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Resource
    private BioSampleCodePrefixTbMapper bioSampleCodePrefixTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        PlantExperimentTaskDTO plantExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantExperimentTaskDTO.class);
        ValidatorUtil.validator(plantExperimentTaskDTO);
        //校验内容
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(plantExperimentTaskDTO.getSpeciesCode());
        if (cerSpeciesConf == null) {
            throw new BusinessException("物种找不到");
        }
        List<ExperimentExcelDTO> experimentExcelDTOList = getExperimentExcelDTOS(plantExperimentTaskDTO);
        List<String> checkReginCodeAndSeedNumList = new ArrayList<>();
        for (ExperimentExcelDTO experimentExcelDTO : experimentExcelDTOList) {
            BeanUtils.trimFiledSpace(experimentExcelDTO);
            ValidatorUtil.validator(experimentExcelDTO);
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(experimentExcelDTO.getSeedNum());
            if (seedStockTb == null) {
                throw new BusinessException("上传数据在种子库中找不到材料信息" + experimentExcelDTO.getSeedNum());
            }
            if (!cerSpeciesConf.getSpeciesCode().equals(seedStockTb.getSpeciesCode())) {
                throw new BusinessException("所选种子物种不匹配");
            }
            List<PlantApplyDetailTb> plantExperimentDetailTbList = plantApplyDetailTbMapper.selectAllByRegionNum(experimentExcelDTO.getRegionNum());
            if (CollectionUtil.isNotEmpty(plantExperimentDetailTbList)) {
                throw new BusinessException("小区编号" + experimentExcelDTO.getRegionNum() + "已经存在其他试验中");
            }
            if (checkReginCodeAndSeedNumList.contains(experimentExcelDTO.getRegionNum() + experimentExcelDTO.getSeedNum())) {
                throw new BusinessException("CER试验小区" + experimentExcelDTO.getRegionNum() + "中存在重复种子编号" + experimentExcelDTO.getSeedNum());
            }
            checkReginCodeAndSeedNumList.add(experimentExcelDTO.getRegionNum() + experimentExcelDTO.getSeedNum());
        }
        plantExperimentTaskDTO.setVectorTaskCodeList(experimentExcelDTOList.stream().map(ExperimentExcelDTO::getVectorTaskCode).filter(vectorTaskCode -> StringUtils.isNotEmpty(vectorTaskCode)).distinct().collect(Collectors.toList()));
        plantExperimentTaskDTO.setPdImplementCodeList(experimentExcelDTOList.stream().map(ExperimentExcelDTO::getPdImplementCode).filter(pdImplementCode -> StringUtils.isNotEmpty(pdImplementCode)).distinct().collect(Collectors.toList()));
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plantExperimentTaskDTO));
    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            PlantExperimentTaskDTO plantExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantExperimentTaskDTO.class);
            List<ExperimentExcelDTO> experimentExcelDTOList = getExperimentExcelDTOS(plantExperimentTaskDTO);
            PlantApplyTb plantApplyTb = new PlantApplyTb();
            plantApplyTb.setSpeciesCode(plantExperimentTaskDTO.getSpeciesCode());
            plantApplyTb.setExperimentType(JSONUtil.toJsonStr(plantExperimentTaskDTO.getExperimentType()));
            plantApplyTb.setPlantTarget(plantExperimentTaskDTO.getPlantTarget());
            plantApplyTb.setPlantDetailUrl(plantExperimentTaskDTO.getPlantDetailUrl());
            plantApplyTb.setFileUrl(plantExperimentTaskDTO.getFileUrl());
            plantApplyTb.setPlantApplyNum(bioTaskDtlTb.getTaskNum());
            plantApplyTb.setCreateTime(new Date());
            plantApplyTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
            plantApplyTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
            plantApplyTb.setExperimentAddressCode(plantExperimentTaskDTO.getExperimentAddressCode());
            plantApplyTb.setSampleCodePrefix(createSampleCode());
            plantApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(experimentExcelDTOList.stream().map(ExperimentExcelDTO::getVectorTaskCode).filter(vectorTaskCode -> StringUtils.isNotEmpty(vectorTaskCode)).distinct().collect(Collectors.toList())));
            plantApplyTb.setPdImplementCodes(JSONUtil.toJsonStr(experimentExcelDTOList.stream().map(ExperimentExcelDTO::getPdImplementCode).filter(pdImplementCode -> StringUtils.isNotEmpty(pdImplementCode)).distinct().collect(Collectors.toList())));
            List<PlantApplyDetailTb> plantExperimentDetailTbList = new ArrayList<>();
            for (ExperimentExcelDTO experimentExcelDTO : experimentExcelDTOList) {
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(experimentExcelDTO.getSeedNum());
                PlantApplyDetailTb plantApplyDetailTb = new PlantApplyDetailTb();
                plantApplyDetailTb.setPdImplementCode(experimentExcelDTO.getPdImplementCode());
                plantApplyDetailTb.setPlantApplyNum(plantApplyTb.getPlantApplyNum());
                plantApplyDetailTb.setRegionNum(experimentExcelDTO.getRegionNum());
                plantApplyDetailTb.setVectorTaskCode(experimentExcelDTO.getVectorTaskCode());
                plantApplyDetailTb.setSeedNum(experimentExcelDTO.getSeedNum());
                plantApplyDetailTb.setPlantCode(seedStockTb.getPlantCode());
                plantApplyDetailTb.setGenerationCode(seedStockTb.getGeneration());
                plantApplyDetailTb.setSpeciesCode(seedStockTb.getSpeciesCode());
                plantApplyDetailTb.setBreedCode(seedStockTb.getBreedCode());
                plantApplyDetailTb.setPlantTime(experimentExcelDTO.getPlantTime());
                plantApplyDetailTb.setPlantNumber(experimentExcelDTO.getPlantNumber());
                plantApplyDetailTb.setPlantUnit("粒");
                plantApplyDetailTb.setRemarks(experimentExcelDTO.getRemark());
                plantApplyDetailTb.setGeneType(seedStockTb.getGeneType());
                plantApplyDetailTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
                plantApplyDetailTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
                plantApplyDetailTb.setCreateTime(new Date());
                plantExperimentDetailTbList.add(plantApplyDetailTb);
            }
            List<PlantMultipleStockTb> plantMultipleStockTbList = plantExperimentDetailTbList.stream().map(plantExperimentDetailTb -> PlantMultipleStockTb.of(plantExperimentDetailTb, bioTaskDtlTb, SourceCodeEnum.cer)).collect(Collectors.toList());
            plantApplyTbMapper.insert(plantApplyTb);
            plantApplyDetailTbMapper.insertBatch(plantExperimentDetailTbList);
            plantMultipleStockTbMapper.insertBatch(plantMultipleStockTbList);
            bioSampleCodePrefixTbMapper.insert(new BioSampleCodePrefixTb(plantApplyTb.getSampleCodePrefix(),null, plantApplyTb.getPlantApplyNum()));
            plantExperimentTaskDTO.setSampleCodePrefix(plantApplyTb.getSampleCodePrefix());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plantExperimentTaskDTO));
        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @NotNull
    private List<ExperimentExcelDTO> getExperimentExcelDTOS(PlantExperimentTaskDTO plantExperimentTaskDTO) {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + plantExperimentTaskDTO.getPlantDetailUrl();
        try {
            ossService.downloadPath(tempFilePath, plantExperimentTaskDTO.getPlantDetailUrl());
        } catch (Exception e) {
            log.error("【CER试验申请】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<ExperimentExcelDTO> experimentExcelDTOList = ExcelUtil.readExcel(tempFilePath, ExperimentExcelDTO.class);
        return experimentExcelDTOList;
    }

    private String createSampleCode() {
        String maxSampleCodePrefix = plantApplyTbMapper.selectMaxSampleCodePrefix();
        if (StringUtils.isEmpty(maxSampleCodePrefix)) {
            return "CAA";
        } else {
            return SampleGroupPergixEnum.C.name() + LetterUtil.nextLetterForInstantVerify(maxSampleCodePrefix.substring(1));
        }
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        PlantExperimentTaskDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantExperimentTaskDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        CerSpeciesConf speciesConf = StringUtils.isEmpty(dto.getSpeciesCode()) ? null : cerSpeciesConfMapper.selectOneBySpeciesCode(dto.getSpeciesCode());
        SeedProduceAddressDict addressDict = StringUtils.isEmpty(dto.getExperimentAddressCode()) ? null : seedProduceAddressDictMapper.selectOneByAddressCode(dto.getExperimentAddressCode());

        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("物种", speciesConf == null ? dto.getSpeciesCode() : speciesConf.getSpeciesName()));
        applyFields.add(buildField("试验类型", experimentTypeNames(dto.getExperimentType())));
        applyFields.add(buildField("种植目标", dto.getPlantTarget()));
        applyFields.add(buildField("试验地点", addressDict == null ? dto.getExperimentAddressCode() : addressDict.getAddressName()));
        applyFields.add(buildField("取样编号前缀", dto.getSampleCodePrefix()));
        applyFields.add(buildField("实施方案编号", joinValues(dto.getVectorTaskCodeList())));
        applyFields.add(buildField("PD实施方案编号", joinValues(dto.getPdImplementCodeList())));
        applyFields.add(buildField("种植明细文件", dto.getPlantDetailUrl()));
        applyFields.add(buildField("附件", dto.getFileUrl()));
        sections.add(buildFieldSection("申请信息", applyFields));

        PlantApplyDetailTb query = new PlantApplyDetailTb();
        query.setPlantApplyNum(bioTaskDtlTb.getTaskNum());
        List<PlantApplyDetailTb> detailList = plantApplyDetailTbMapper.selectSelective(query);
        if (CollectionUtil.isNotEmpty(detailList)) {
            Map<String, String> speciesNameMap = cerSpeciesConfMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName, (left, right) -> left));
            Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
            List<String> headers = java.util.Arrays.asList("小区编号", "种子编号", "实施方案编号", "PD实施方案编号", "种植编号", "物种", "品种", "代次", "基因型", "种植时间", "种植数量", "单位", "备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (PlantApplyDetailTb detail : detailList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("小区编号", detail.getRegionNum());
                row.put("种子编号", detail.getSeedNum());
                row.put("实施方案编号", detail.getVectorTaskCode());
                row.put("PD实施方案编号", detail.getPdImplementCode());
                row.put("种植编号", detail.getPlantCode());
                row.put("物种", speciesNameMap.getOrDefault(detail.getSpeciesCode(), detail.getSpeciesCode()));
                row.put("品种", breedNameMap.getOrDefault(detail.getBreedCode(), detail.getBreedCode()));
                row.put("代次", detail.getGenerationCode());
                row.put("基因型", detail.getGeneType());
                row.put("种植时间", detail.getPlantTime());
                row.put("种植数量", detail.getPlantNumber());
                row.put("单位", detail.getPlantUnit());
                row.put("备注", detail.getRemarks());
                rows.add(row);
            }
            sections.add(buildTableSection("种植明细", headers, rows));
            return sections;
        }

        List<ExperimentExcelDTO> excelDTOList = getExperimentExcelDTOS(dto);
        if (CollectionUtil.isNotEmpty(excelDTOList)) {
            List<String> headers = java.util.Arrays.asList("小区编号", "种子编号", "实施方案编号", "PD实施方案编号", "种植数量", "种植时间", "备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (ExperimentExcelDTO item : excelDTOList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("小区编号", item.getRegionNum());
                row.put("种子编号", item.getSeedNum());
                row.put("实施方案编号", item.getVectorTaskCode());
                row.put("PD实施方案编号", item.getPdImplementCode());
                row.put("种植数量", item.getPlantNumber());
                row.put("种植时间", item.getPlantTime());
                row.put("备注", item.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("种植明细", headers, rows));
        }

        return sections;
    }

    private String experimentTypeNames(List<String> experimentTypeList) {
        if (CollectionUtil.isEmpty(experimentTypeList)) {
            return "";
        }
        return experimentTypeList.stream()
                .map(code -> {
                    String desc = ExperimentTypeEnum.getDescByCode(code);
                    return StringUtils.isEmpty(desc) ? code : desc;
                })
                .collect(Collectors.joining("、"));
    }

    private String joinValues(List<String> values) {
        if (CollectionUtil.isEmpty(values)) {
            return "";
        }
        return values.stream().filter(StringUtils::isNotEmpty).distinct().collect(Collectors.joining("、"));
    }
}
