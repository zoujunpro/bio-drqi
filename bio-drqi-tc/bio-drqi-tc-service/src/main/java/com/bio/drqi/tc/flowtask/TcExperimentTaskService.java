package com.bio.drqi.tc.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.ExperimentTypeEnum;
import com.bio.drqi.common.enums.SampleGroupPergixEnum;
import com.bio.drqi.common.util.LetterUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.SeedProduceAddressDictMapper;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.mapper.TcExperimentTbMapper;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.service.dto.ExperimentDesignExcelDTO;
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

            List<TcExperimentDesignTb> tcExperimentDesignTbList = new ArrayList<TcExperimentDesignTb>();
            for (ExperimentDesignExcelDTO experimentDesignExcelDTO : experimentDesignExcelDTOList) {
                TcExperimentDesignTb tcExperimentDesignTb = new TcExperimentDesignTb();
                tcExperimentDesignTb.setExperimentNum(tcExperimentTb.getExperimentNum());
                tcExperimentDesignTb.setRegionNum(experimentDesignExcelDTO.getRegionNum());
                tcExperimentDesignTb.setSeedNum(experimentDesignExcelDTO.getSeedNum());
                tcExperimentDesignTb.setVectorTaskCode(experimentDesignExcelDTO.getVectorTaskCode());
                tcExperimentDesignTb.setSpeciesCode(tcExperimentTb.getSpeciesCode());
                tcExperimentDesignTb.setBreedCode(experimentDesignExcelDTO.getBreedCode());
                tcExperimentDesignTb.setTargetCharacter(experimentDesignExcelDTO.getTargetCharacter());
                tcExperimentDesignTb.setGenerationCode(experimentDesignExcelDTO.getGenerationCode());
                tcExperimentDesignTb.setTcGene(experimentDesignExcelDTO.getTcGene());
                tcExperimentDesignTb.setRegionArea(experimentDesignExcelDTO.getRegionArea());
                tcExperimentDesignTb.setAreaUnit(experimentDesignExcelDTO.getAreaUnit());
                tcExperimentDesignTb.setPlantSpace(experimentDesignExcelDTO.getPlantSpace());
                tcExperimentDesignTb.setRowsNumber(experimentDesignExcelDTO.getRowsNumber());
                tcExperimentDesignTb.setRowsLength(experimentDesignExcelDTO.getRowsLength());
                tcExperimentDesignTb.setRowsSpace(experimentDesignExcelDTO.getRowsSpace());
                tcExperimentDesignTb.setSeedingType(experimentDesignExcelDTO.getSeedingType());
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
        List<ExperimentDesignExcelDTO> experimentDesignExcelDTOList = ExcelUtil.readExcel(tempFilePath, ExperimentDesignExcelDTO.class);
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
        }
        Map<String, List<ExperimentDesignExcelDTO>> listMap = experimentDesignExcelDTOList.stream().collect(Collectors.groupingBy(ExperimentDesignExcelDTO::getRegionNum));
        listMap.forEach((regionNun, list) -> {
            if (list.size() > 1) {
                throw new BusinessException("试验设计不符合规范，小区编号出现多次");
            }
        });


        return experimentDesignExcelDTOList;
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
        applyFields.add(buildField("试验目标", dto.getExperimentGoal()));
        applyFields.add(buildField("取样编号前缀", dto.getSampleCodePrefix()));
        applyFields.add(buildField("实施方案编号", joinValues(dto.getVectorTaskCodeList())));
        applyFields.add(buildField("PD实施方案编号", joinValues(dto.getPdImplementCodeList())));
        sections.add(buildFieldSection("申请信息", applyFields));

        TcExperimentDesignTb query = new TcExperimentDesignTb();
        query.setExperimentNum(bioTaskDtlTb.getTaskNum());
        List<TcExperimentDesignTb> designList = tcExperimentDesignTbMapper.selectSelective(query);
        if (CollectionUtil.isNotEmpty(designList)) {
            Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
            List<String> headers = Arrays.asList("区域编号", "种子编号", "实施方案编号", "PD实施方案编号", "品种", "目标性状", "世代", "基因型", "小区面积", "播种方式", "播种数量", "播种时间", "移栽时间", "备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (TcExperimentDesignTb item : designList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("区域编号", item.getRegionNum());
                row.put("种子编号", item.getSeedNum());
                row.put("实施方案编号", item.getVectorTaskCode());
                row.put("PD实施方案编号", item.getPdImplementCode());
                row.put("品种", breedNameMap.getOrDefault(item.getBreedCode(), item.getBreedCode()));
                row.put("目标性状", item.getTargetCharacter());
                row.put("世代", item.getGenerationCode());
                row.put("基因型", item.getTcGene());
                row.put("小区面积", joinText(item.getRegionArea(), item.getAreaUnit()));
                row.put("播种方式", item.getSeedingType());
                row.put("播种数量", joinText(item.getSeedingNumber() == null ? null : String.valueOf(item.getSeedingNumber()), item.getSeedingUnit()));
                row.put("播种时间", item.getSeedingTime());
                row.put("移栽时间", item.getTransplantTime());
                row.put("备注", item.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("田间设计明细", headers, rows));
            return sections;
        }

        if (StringUtils.isNotEmpty(dto.getExperimentDesignUrl())) {
            List<ExperimentDesignExcelDTO> excelList = validatorExcel(dto);
            if (CollectionUtil.isNotEmpty(excelList)) {
                List<String> headers = Arrays.asList("区域编号", "种子编号", "实施方案编号", "PD实施方案编号", "品种", "目标性状", "世代", "基因型", "小区面积", "播种方式", "播种数量", "播种时间", "移栽时间", "备注");
                List<Map<String, Object>> rows = new ArrayList<>();
                for (ExperimentDesignExcelDTO item : excelList) {
                    Map<String, Object> row = new LinkedHashMap<>();
                    row.put("区域编号", item.getRegionNum());
                    row.put("种子编号", item.getSeedNum());
                    row.put("实施方案编号", item.getVectorTaskCode());
                    row.put("PD实施方案编号", item.getPdImplementCode());
                    row.put("品种", item.getBreedName());
                    row.put("目标性状", item.getTargetCharacter());
                    row.put("世代", item.getGenerationCode());
                    row.put("基因型", item.getTcGene());
                    row.put("小区面积", joinText(item.getRegionArea(), item.getAreaUnit()));
                    row.put("播种方式", item.getSeedingType());
                    row.put("播种数量", joinText(item.getSeedingNumber() == null ? null : String.valueOf(item.getSeedingNumber()), item.getSeedingUnit()));
                    row.put("播种时间", item.getSeedingTime());
                    row.put("移栽时间", item.getTransplantTime());
                    row.put("备注", item.getRemark());
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
}
