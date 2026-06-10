package com.bio.drqi.tc.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.excel.EasyExcel;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.ExperimentTypeEnum;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.common.enums.SampleGroupPergixEnum;
import com.bio.drqi.common.util.LetterUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.SeedProduceAddressDictMapper;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.mapper.TcExperimentTbMapper;
import com.bio.drqi.tc.enums.TcDesignTypeEnum;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.service.dto.EvaluationExperimentDesignExcelDTO;
import com.bio.drqi.tc.service.dto.ExperimentDesignExcelDTO;
import com.bio.drqi.tc.service.dto.HybridExperimentDesignExcelDTO;
import com.bio.drqi.tc.service.dto.SurvivalCompetitionExperimentDesignExcelDTO;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service("tc_experiment_task_apply")
@Slf4j
public class TcExperimentTaskService extends AbstractTcBaseTaskService {


    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private OssService ossService;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
        ValidatorUtil.validator(tcExperimentTaskDTO);
        List<ExperimentDesignExcelDTO> experimentDesignExcelDTOList = null;
        if (StringUtils.isNotEmpty(tcExperimentTaskDTO.getExperimentDesignUrl())) {
            experimentDesignExcelDTOList = validatorExcel(tcExperimentTaskDTO);
        }

        tcExperimentTaskDTO.setVectorTaskCodeList(experimentDesignExcelDTOList.stream().map(ExperimentDesignExcelDTO::getVectorTaskCode).filter(vectorTaskCode -> StringUtils.isNotEmpty(vectorTaskCode)).distinct().collect(Collectors.toList()));
        tcExperimentTaskDTO.setPdImplementCodeList(experimentDesignExcelDTOList.stream().map(ExperimentDesignExcelDTO::getPdImplementCode).filter(pdImplementCode -> StringUtils.isNotEmpty(pdImplementCode)).distinct().collect(Collectors.toList()));
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcExperimentTaskDTO));
    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
        List<ExperimentDesignExcelDTO> experimentDesignExcelDTOList = null;
        if (StringUtils.isNotEmpty(tcExperimentTaskDTO.getExperimentDesignUrl())) {
            experimentDesignExcelDTOList = validatorExcel(tcExperimentTaskDTO);
        }
        SeedProduceAddressDict seedProduceAddressDict = seedProduceAddressDictMapper.selectOneByAddressCode(tcExperimentTaskDTO.getExperimentAddressCode());
        if (seedProduceAddressDict == null) {
            throw new BusinessException("试验地点不正确");
        }
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            if (CollectionUtil.isEmpty(experimentDesignExcelDTOList)) {
                throw new BusinessException("大田试验田间设计缺失");
            }

            TcExperimentTb tcExperimentTb = new TcExperimentTb();
            tcExperimentTb.setSpeciesCode(tcExperimentTaskDTO.getSpeciesCode());
            tcExperimentTb.setSpeciesName(tcExperimentTaskDTO.getSpeciesName());
            tcExperimentTb.setFileUrl(tcExperimentTaskDTO.getFileUrl());
            tcExperimentTb.setExperimentGoal(tcExperimentTaskDTO.getExperimentGoal());
            tcExperimentTb.setExperimentAddressCode(tcExperimentTaskDTO.getExperimentAddressCode());
            tcExperimentTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
            tcExperimentTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
            tcExperimentTb.setCreateTime(new Date());
            tcExperimentTb.setExperimentNum(bioTaskDtlTb.getTaskNum());
            tcExperimentTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            tcExperimentTb.setDesignUrl(tcExperimentTaskDTO.getExperimentDesignUrl());
            tcExperimentTb.setSampleCodePrefix(createSampleCode());
            tcExperimentTb.setNextSampleNumber(1);
            tcExperimentTb.setExperimentStatus(ExperimentStatusEnum.INIT.status);
            tcExperimentTb.setExperimentType(JSONUtil.toJsonStr(tcExperimentTaskDTO.getExperimentType()));
            tcExperimentTb.setDesignType(tcExperimentTaskDTO.getDesignType());

            List<TcExperimentDesignTb> tcExperimentDesignTbList = new ArrayList<TcExperimentDesignTb>();
            for (ExperimentDesignExcelDTO experimentDesignExcelDTO : experimentDesignExcelDTOList) {
                TcExperimentDesignTb tcExperimentDesignTb = new TcExperimentDesignTb();
                tcExperimentDesignTb.setExperimentNum(tcExperimentTb.getExperimentNum());
                tcExperimentDesignTb.setRegionNum(experimentDesignExcelDTO.getRegionNum());
                tcExperimentDesignTb.setSeedNum(experimentDesignExcelDTO.getSeedNum());
                tcExperimentDesignTb.setVectorTaskCode(experimentDesignExcelDTO.getVectorTaskCode());
                tcExperimentDesignTb.setSpeciesCode(tcExperimentTb.getSpeciesCode());
                tcExperimentDesignTb.setBreedCode(experimentDesignExcelDTO.getBreedCode());
                tcExperimentDesignTb.setStrainName(experimentDesignExcelDTO.getStrainName());
                tcExperimentDesignTb.setTargetCharacter(experimentDesignExcelDTO.getTargetCharacter());
                tcExperimentDesignTb.setGenerationCode(experimentDesignExcelDTO.getGenerationCode());
                tcExperimentDesignTb.setTcGene(experimentDesignExcelDTO.getTcGene());
                tcExperimentDesignTb.setDensity(experimentDesignExcelDTO.getDensity());
                tcExperimentDesignTb.setGroupName(experimentDesignExcelDTO.getGroupName());
                tcExperimentDesignTb.setRepeatNum(experimentDesignExcelDTO.getRepeat());
                tcExperimentDesignTb.setRegionArea(experimentDesignExcelDTO.getRegionArea());
                tcExperimentDesignTb.setAreaUnit(experimentDesignExcelDTO.getAreaUnit());
                tcExperimentDesignTb.setPlantSpace(experimentDesignExcelDTO.getPlantSpace());
                tcExperimentDesignTb.setRowsNumber(experimentDesignExcelDTO.getRowsNumber());
                tcExperimentDesignTb.setRowsLength(experimentDesignExcelDTO.getRowsLength());
                tcExperimentDesignTb.setRowsSpace(experimentDesignExcelDTO.getRowsSpace());
                tcExperimentDesignTb.setSeedingType(experimentDesignExcelDTO.getSeedingType());
                tcExperimentDesignTb.setPerHoleSeedingNumber(experimentDesignExcelDTO.getPerHoleSeedingNumber());
                tcExperimentDesignTb.setRowSeedingNumber(experimentDesignExcelDTO.getRowSeedingNumber());
                tcExperimentDesignTb.setSeedingNumber(experimentDesignExcelDTO.getSeedingNumber());
                tcExperimentDesignTb.setSeedingUnit(experimentDesignExcelDTO.getSeedingUnit());
                tcExperimentDesignTb.setSeedingTime(experimentDesignExcelDTO.getSeedingTime());
                tcExperimentDesignTb.setRemark(experimentDesignExcelDTO.getRemark());
                tcExperimentDesignTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                tcExperimentDesignTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
                tcExperimentDesignTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
                tcExperimentDesignTb.setCreateTime(new Date());
                tcExperimentDesignTb.setEmergenceRate(experimentDesignExcelDTO.getEmergenceRate());
                tcExperimentDesignTb.setTransplantTime(experimentDesignExcelDTO.getTransplantTime());
                tcExperimentDesignTb.setPdImplementCode(experimentDesignExcelDTO.getPdImplementCode());
                if (experimentDesignExcelDTO instanceof SurvivalCompetitionExperimentDesignExcelDTO) {
                    tcExperimentDesignTb.setPeriod(((SurvivalCompetitionExperimentDesignExcelDTO) experimentDesignExcelDTO).getPeriod());
                }
                if (experimentDesignExcelDTO instanceof HybridExperimentDesignExcelDTO) {
                    HybridExperimentDesignExcelDTO hybridExperimentDesignExcelDTO = (HybridExperimentDesignExcelDTO) experimentDesignExcelDTO;
                    tcExperimentDesignTb.setParentType(hybridExperimentDesignExcelDTO.getParentType());
                    tcExperimentDesignTb.setStaggeredDesign(hybridExperimentDesignExcelDTO.getStaggeredDesign());
                }
                tcExperimentDesignTbList.add(tcExperimentDesignTb);
            }
            tcExperimentTb.setPdImplementCodes(JSONUtil.toJsonStr(tcExperimentDesignTbList.stream().map(TcExperimentDesignTb::getPdImplementCode).filter(pdImplementCode -> StringUtils.isNotEmpty(pdImplementCode)).distinct().collect(Collectors.toList())));
            tcExperimentTb.setVectorTaskCodes(JSONUtil.toJsonStr(tcExperimentDesignTbList.stream().map(TcExperimentDesignTb::getVectorTaskCode).filter(vectorTaskCode -> StringUtils.isNotEmpty(vectorTaskCode)).distinct().collect(Collectors.toList())));
            tcExperimentTbMapper.insert(tcExperimentTb);
            tcExperimentDesignTbMapper.insertBatch(tcExperimentDesignTbList);
            tcExperimentTaskDTO.setSampleCodePrefix(tcExperimentTb.getSampleCodePrefix());
            tcExperimentTaskDTO.setVectorTaskCodeList(JSONUtil.toList(tcExperimentTb.getVectorTaskCodes(), String.class));
            tcExperimentTaskDTO.setPdImplementCodeList(JSONUtil.toList(tcExperimentTb.getPdImplementCodes(), String.class));
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcExperimentTaskDTO));
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }


    private List<ExperimentDesignExcelDTO> validatorExcel(TcExperimentTaskDTO tcExperimentTaskDTO) {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + tcExperimentTaskDTO.getExperimentDesignUrl();
        try {
            ossService.downloadPath(tempFilePath, tcExperimentTaskDTO.getExperimentDesignUrl());
        } catch (Exception e) {
            log.error("【大田试验田间设计】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<ExperimentDesignExcelDTO> experimentDesignExcelDTOList = readExperimentDesignExcel(tempFilePath, tcExperimentTaskDTO.getDesignType());
        if (CollectionUtil.isEmpty(experimentDesignExcelDTOList)) {
            throw new BusinessException("大田试验田间设计没有具体内容");
        }
        List<CerBreedDict> breedDictList = cerBreedDictMapper.selectAllBySpeciesCode(tcExperimentTaskDTO.getSpeciesCode());
        if (CollectionUtil.isEmpty(breedDictList)) {
            throw new BusinessException("该物种下无品种配置项");
        }

        Map<String, String> breedNameCodeMap = breedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedName, CerBreedDict::getBreedCode));
        for (ExperimentDesignExcelDTO experimentDesignExcelDTO : experimentDesignExcelDTOList) {
            ValidatorUtil.validator(experimentDesignExcelDTO);
            if (breedNameCodeMap.get(experimentDesignExcelDTO.getBreedName()) == null) {
                throw new BusinessException("物种下无此品种配置" + experimentDesignExcelDTO.getBreedName());
            } else {
                experimentDesignExcelDTO.setBreedCode(breedNameCodeMap.get(experimentDesignExcelDTO.getBreedName()));
            }
            List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByRegionNum(experimentDesignExcelDTO.getRegionNum());
            if (CollectionUtil.isNotEmpty(tcExperimentDesignTbList)) {
                throw new BusinessException("系统中其他试验已经存在此小区编号：" + tcExperimentDesignTbList.get(0).getExperimentNum());
            }
            TcExperimentDesignTb tcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByRegionNumAndSeedNum(experimentDesignExcelDTO.getRegionNum(), experimentDesignExcelDTO.getSeedNum());
            if (tcExperimentDesignTb != null) {
                throw new BusinessException("系统中已经存在此小区编号" + tcExperimentDesignTb.getRegionNum() + "和此种子编号：" + tcExperimentDesignTb.getSeedNum());
            }
            if(GenerationEnum.getGeneration(experimentDesignExcelDTO.getGenerationName())==null){
                throw new BusinessException("代次填写错误");
            }else {
                    experimentDesignExcelDTO.setGenerationCode(GenerationEnum.getGeneration(experimentDesignExcelDTO.getGenerationName()).code);
            }
        }
        Map<String, List<ExperimentDesignExcelDTO>> listMap = experimentDesignExcelDTOList.stream().collect(Collectors.groupingBy(ExperimentDesignExcelDTO::getRegionNum));
        listMap.forEach((regionNun, list) -> {
            if (list.size() > 1) {
                throw new BusinessException("试验设计不符合规范，小区编号出现多次");
            }
        });


        return experimentDesignExcelDTOList;
    }

    private List<ExperimentDesignExcelDTO> readExperimentDesignExcel(String tempFilePath, String designType) {
        TcDesignTypeEnum tcDesignTypeEnum = TcDesignTypeEnum.getDesignTypeEnum(designType);
        if (tcDesignTypeEnum == null) {
            throw new BusinessException("田间设计类型填写错误");
        }
        Class<? extends ExperimentDesignExcelDTO> excelClass;
        switch (tcDesignTypeEnum) {
            case SURVIVAL_COMPETITION:
                excelClass = SurvivalCompetitionExperimentDesignExcelDTO.class;
                break;
            case EVALUATION:
                excelClass = EvaluationExperimentDesignExcelDTO.class;
                break;
            case HYBRID:
                excelClass = HybridExperimentDesignExcelDTO.class;
                break;
            default:
                throw new BusinessException("田间设计类型填写错误");
        }
        return EasyExcel.read(tempFilePath, excelClass, null).sheet(tcDesignTypeEnum.name).doReadSync();
    }


    private String createSampleCode() {
        String maxSampleCodePrefix = tcExperimentTbMapper.selectMaxSampleCodePerfix();
        if (StringUtils.isEmpty(maxSampleCodePrefix)) {
            return "TAA";
        } else {
            return SampleGroupPergixEnum.T.name() + LetterUtil.nextLetterForInstantVerify(maxSampleCodePrefix.substring(1));
        }
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        TcExperimentTaskDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        SeedProduceAddressDict addressDict = StringUtils.isEmpty(dto.getExperimentAddressCode()) ? null : seedProduceAddressDictMapper.selectOneByAddressCode(dto.getExperimentAddressCode());

        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("试验编号", bioTaskDtlTb.getTaskNum()));
        applyFields.add(buildField("物种", dto.getSpeciesName()));
        applyFields.add(buildField("试验地点", addressDict == null ? dto.getExperimentAddressCode() : addressDict.getAddressName()));
        applyFields.add(buildField("试验类型", experimentTypeNames(dto.getExperimentType())));
        applyFields.add(buildField("田间设计类型", dto.getDesignType()));
        applyFields.add(buildField("试验目标", dto.getExperimentGoal()));
        applyFields.add(buildField("取样编号前缀", dto.getSampleCodePrefix()));
        applyFields.add(buildField("实施方案编号", joinValues(dto.getVectorTaskCodeList())));
        applyFields.add(buildField("PD实施方案编号", joinValues(dto.getPdImplementCodeList())));
        sections.add(buildFieldSection("申请信息", applyFields));

        TcExperimentDesignTb query = new TcExperimentDesignTb();
        query.setExperimentNum(bioTaskDtlTb.getTaskNum());
        List<TcExperimentDesignTb> designList = tcExperimentDesignTbMapper.selectSelective(query);
        if (CollectionUtil.isNotEmpty(designList)) {
            String designType = dto.getDesignType();
            if (StringUtils.isEmpty(designType)) {
                TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(bioTaskDtlTb.getTaskNum());
                designType = tcExperimentTb == null ? null : tcExperimentTb.getDesignType();
            }
            Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
            List<String> headers = designHeaders(designType);
            List<Map<String, Object>> rows = new ArrayList<>();
            for (TcExperimentDesignTb item : designList) {
                Map<String, Object> row = new LinkedHashMap<>();
                fillDesignRow(row, item, breedNameMap.getOrDefault(item.getBreedCode(), item.getBreedCode()), designType);
                rows.add(row);
            }
            sections.add(buildTableSection("田间设计明细", headers, rows));
            return sections;
        }

        if (StringUtils.isNotEmpty(dto.getExperimentDesignUrl())) {
            List<ExperimentDesignExcelDTO> excelList = validatorExcel(dto);
            if (CollectionUtil.isNotEmpty(excelList)) {
                List<String> headers = designHeaders(dto.getDesignType());
                List<Map<String, Object>> rows = new ArrayList<>();
                for (ExperimentDesignExcelDTO item : excelList) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    fillDesignRow(row, item, dto.getDesignType());
                    rows.add(row);
                }
                sections.add(buildTableSection("田间设计明细", headers, rows));
            }
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

    private String joinText(String first, String second) {
        if (StringUtils.isEmpty(first)) {
            return "";
        }
        return StringUtils.isEmpty(second) ? first : first + second;
    }

    private List<String> designHeaders(String designType) {
        if (isDesignType(designType, TcDesignTypeEnum.SURVIVAL_COMPETITION)) {
            List<String> headers = new ArrayList<>(commonDesignHeaders());
            headers.add("期次");
            headers.add("备注");
            return headers;
        }
        if (isDesignType(designType, TcDesignTypeEnum.HYBRID)) {
            List<String> headers = new ArrayList<>(Arrays.asList("区域编号", "种子编号", "株系名称", "品种", "亲本类型",
                    "实施方案编号", "PD实施方案编号", "目标性状", "世代", "基因型", "错期设计", "密度",
                    "组别", "重复", "小区面积", "面积单位", "小区行数", "小区行长(m)", "行距(cm)", "株距(cm)",
                    "播种方式", "每穴播种粒数", "每行播种数量", "小区播种数量", "播种单位", "备注"));
            return headers;
        }
        List<String> headers = new ArrayList<>(commonDesignHeaders());
        headers.add("备注");
        return headers;
    }

    private List<String> commonDesignHeaders() {
        return Arrays.asList("区域编号", "种子编号", "株系名称", "品种", "实施方案编号", "PD实施方案编号",
                "目标性状", "世代", "基因型", "密度", "组别", "重复", "小区面积", "面积单位",
                "小区行数", "小区行长(m)", "行距(cm)", "株距(cm)", "播种方式", "每穴播种粒数",
                "每行播种数量", "小区播种数量", "播种单位");
    }

    private void fillDesignRow(Map<String, Object> row, TcExperimentDesignTb item, String breedName, String designType) {
        row.put("区域编号", item.getRegionNum());
        row.put("种子编号", item.getSeedNum());
        row.put("株系名称", item.getStrainName());
        row.put("品种", breedName);
        if (isDesignType(designType, TcDesignTypeEnum.HYBRID)) {
            row.put("亲本类型", item.getParentType());
        }
        row.put("实施方案编号", item.getVectorTaskCode());
        row.put("PD实施方案编号", item.getPdImplementCode());
        row.put("目标性状", item.getTargetCharacter());
        row.put("世代", item.getGenerationCode());
        row.put("基因型", item.getTcGene());
        if (isDesignType(designType, TcDesignTypeEnum.HYBRID)) {
            row.put("错期设计", item.getStaggeredDesign());
        }
        row.put("密度", item.getDensity());
        row.put("组别", item.getGroupName());
        row.put("重复", item.getRepeatNum());
        row.put("小区面积", item.getRegionArea());
        row.put("面积单位", item.getAreaUnit());
        row.put("小区行数", item.getRowsNumber());
        row.put("小区行长(m)", item.getRowsLength());
        row.put("行距(cm)", item.getRowsSpace());
        row.put("株距(cm)", item.getPlantSpace());
        row.put("播种方式", item.getSeedingType());
        row.put("每穴播种粒数", item.getPerHoleSeedingNumber());
        row.put("每行播种数量", item.getRowSeedingNumber());
        row.put("小区播种数量", item.getSeedingNumber());
        row.put("播种单位", item.getSeedingUnit());
        if (isDesignType(designType, TcDesignTypeEnum.SURVIVAL_COMPETITION)) {
            row.put("期次", item.getPeriod());
        }
        row.put("备注", item.getRemark());
    }

    private void fillDesignRow(Map<String, Object> row, ExperimentDesignExcelDTO item, String designType) {
        row.put("区域编号", item.getRegionNum());
        row.put("种子编号", item.getSeedNum());
        row.put("株系名称", item.getStrainName());
        row.put("品种", item.getBreedName());
        if (isDesignType(designType, TcDesignTypeEnum.HYBRID)) {
            HybridExperimentDesignExcelDTO hybridItem = item instanceof HybridExperimentDesignExcelDTO
                    ? (HybridExperimentDesignExcelDTO) item : null;
            row.put("亲本类型", hybridItem == null ? null : hybridItem.getParentType());
        }
        row.put("实施方案编号", item.getVectorTaskCode());
        row.put("PD实施方案编号", item.getPdImplementCode());
        row.put("目标性状", item.getTargetCharacter());
        row.put("世代", item.getGenerationCode());
        row.put("基因型", item.getTcGene());
        if (isDesignType(designType, TcDesignTypeEnum.HYBRID)) {
            HybridExperimentDesignExcelDTO hybridItem = item instanceof HybridExperimentDesignExcelDTO
                    ? (HybridExperimentDesignExcelDTO) item : null;
            row.put("错期设计", hybridItem == null ? null : hybridItem.getStaggeredDesign());
        }
        row.put("密度", item.getDensity());
        row.put("组别", item.getGroupName());
        row.put("重复", item.getRepeat());
        row.put("小区面积", item.getRegionArea());
        row.put("面积单位", item.getAreaUnit());
        row.put("小区行数", item.getRowsNumber());
        row.put("小区行长(m)", item.getRowsLength());
        row.put("行距(cm)", item.getRowsSpace());
        row.put("株距(cm)", item.getPlantSpace());
        row.put("播种方式", item.getSeedingType());
        row.put("每穴播种粒数", item.getPerHoleSeedingNumber());
        row.put("每行播种数量", item.getRowSeedingNumber());
        row.put("小区播种数量", item.getSeedingNumber());
        row.put("播种单位", item.getSeedingUnit());
        if (item instanceof SurvivalCompetitionExperimentDesignExcelDTO
                || isDesignType(designType, TcDesignTypeEnum.SURVIVAL_COMPETITION)) {
            row.put("期次", item instanceof SurvivalCompetitionExperimentDesignExcelDTO
                    ? ((SurvivalCompetitionExperimentDesignExcelDTO) item).getPeriod() : null);
        }
        row.put("备注", item.getRemark());
    }

    private boolean isDesignType(String designType, TcDesignTypeEnum expected) {
        return expected.equals(TcDesignTypeEnum.getDesignTypeEnum(designType));
    }


}
