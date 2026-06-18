package com.bio.drqi.tc.flowtask;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.service.dto.TcHarvestExcelDTO;
import com.bio.drqi.tc.service.dto.TcHarvestTaskDTO;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


@Service("tc_harvest_task_apply")
@Slf4j
public class TcHarvestTaskService extends AbstractTcBaseTaskService {

    @Resource
    private OssService ossService;

    @Resource
    private TcHarvestSeedApplyTbMapper tcHarvestSeedApplyTbMapper;


    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private TcHarvestSeedTbMapper tcHarvestSeedTbMapper;

    @Resource
    private BioDictMapper bioDictMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcHarvestTaskDTO tcHarvestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcHarvestTaskDTO.class);
        ValidatorUtil.validator(tcHarvestTaskDTO);
        BeanUtils.trimFiledSpace(tcHarvestTaskDTO);

        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcHarvestTaskDTO.getExperimentNum());
        if (tcExperimentTb == null) {
            throw new BusinessException("不存在此试验");
        }
        if (!ExperimentStatusEnum.INIT.status.equals(tcExperimentTb.getExperimentStatus())) {
            throw new BusinessException("非进行中试验，无法进行任何操作");
        }
        if (!tcHarvestTaskDTO.getHarvestFileUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + tcHarvestTaskDTO.getHarvestFileUrl();
        try {
            ossService.downloadPath(tempFilePath, tcHarvestTaskDTO.getHarvestFileUrl());
        } catch (Exception e) {
            log.error("【任务工单】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<TcHarvestExcelDTO> tcHarvestExcelDTOList = ExcelUtil.readExcel(tempFilePath, TcHarvestExcelDTO.class);
        Map<String, BioDict> materialTypeDictMap = bioDictMapper.selectAllByDictType(BioDictTypeEnum.MATERIAL_TYPE.name()).stream()
                .collect(Collectors.toMap(BioDict::getDictValueName, bioDict -> bioDict, (left, right) -> left));
        for (TcHarvestExcelDTO tcHarvestExcelDTO : tcHarvestExcelDTOList) {
            validateHarvestTimeFormat(tcHarvestExcelDTO.getHarvestTime());
            BioDict materialTypeBioDict = materialTypeDictMap.get(tcHarvestExcelDTO.getMaterialTypeName());
            if (materialTypeBioDict == null) {
                throw new BusinessException("材料类型填写错误：" + tcHarvestExcelDTO.getMaterialTypeName());
            }
            tcHarvestExcelDTO.setMaterialType(materialTypeBioDict.getDictValueCode());

            TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSingleNumberAndMSingleNumber
                    (tcExperimentTb.getExperimentNum(),
                            tcHarvestExcelDTO.getFatherRegionNum(),
                            tcHarvestExcelDTO.getMotherRegionNum(),
                            tcHarvestExcelDTO.getFatherSeedNum(),
                            tcHarvestExcelDTO.getMotherSeedNum(),
                            tcHarvestExcelDTO.getFatherSingleNumber(),
                            tcHarvestExcelDTO.getMotherSingleNumber());
            if (tcPollinationTb == null) {
                throw new BusinessException("无此授粉记录：" + JSONUtil.toJsonStr(tcHarvestExcelDTO));
            }
        }
        tcHarvestTaskDTO.setTcHarvestExcelDTOList(tcHarvestExcelDTOList);
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcHarvestTaskDTO));
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcHarvestTaskDTO tcHarvestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcHarvestTaskDTO.class);
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcHarvestTaskDTO.getExperimentNum());
            tcExperimentTb.setHarvestApplyNum(bioTaskDtlTb.getTaskNum());
            tcExperimentTbMapper.updateById(tcExperimentTb);

            TcHarvestSeedApplyTb tcHarvestSeedApplyTb = new TcHarvestSeedApplyTb();
            tcHarvestSeedApplyTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            tcHarvestSeedApplyTb.setHarvestApplyNum(bioTaskDtlTb.getTaskNum());
            tcHarvestSeedApplyTb.setCreateTime(new Date());
            tcHarvestSeedApplyTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
            tcHarvestSeedApplyTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
            tcHarvestSeedApplyTb.setExperimentNum(tcExperimentTb.getExperimentNum());
            tcHarvestSeedApplyTb.setHarvestFileUrl(tcHarvestTaskDTO.getHarvestFileUrl());
            tcHarvestSeedApplyTbMapper.insert(tcHarvestSeedApplyTb);

            List<TcHarvestSeedTb> tcHarvestSeedTbList = new ArrayList<>();
            for (TcHarvestExcelDTO tcHarvestExcelDTO : tcHarvestTaskDTO.getTcHarvestExcelDTOList()) {
                TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSingleNumberAndMSingleNumber
                        (tcExperimentTb.getExperimentNum(),
                                tcHarvestExcelDTO.getFatherRegionNum(),
                                tcHarvestExcelDTO.getMotherRegionNum(),
                                tcHarvestExcelDTO.getFatherSeedNum(),
                                tcHarvestExcelDTO.getMotherSeedNum(),
                                tcHarvestExcelDTO.getFatherSingleNumber(),
                                tcHarvestExcelDTO.getMotherSingleNumber());
                if (tcPollinationTb == null) {
                    throw new BusinessException("无此授粉记录：" + JSONUtil.toJsonStr(tcHarvestExcelDTO));
                }
                TcHarvestSeedTb tcHarvestSeedTb = new TcHarvestSeedTb();
                tcHarvestSeedTb.setExperimentNum(tcPollinationTb.getExperimentNum());
                tcHarvestSeedTb.setSampleApplyNum(tcPollinationTb.getSampleApplyNum());
                tcHarvestSeedTb.setPollinationApplyNum(tcPollinationTb.getPollinationApplyNum());
                tcHarvestSeedTb.setMRegionNum(tcPollinationTb.getMRegionNum());
                tcHarvestSeedTb.setFRegionNum(tcPollinationTb.getFRegionNum());
                tcHarvestSeedTb.setMSampleCode(tcPollinationTb.getMSampleCode());
                tcHarvestSeedTb.setFSampleCode(tcPollinationTb.getFSampleCode());
                tcHarvestSeedTb.setMSeedNum(tcPollinationTb.getMSeedNum());
                tcHarvestSeedTb.setFSeedNum(tcPollinationTb.getFSeedNum());
                tcHarvestSeedTb.setFBreedCode(tcPollinationTb.getFBreedCode());
                tcHarvestSeedTb.setMBreedCode(tcPollinationTb.getMBreedCode());
                tcHarvestSeedTb.setMVectorTaskCode(tcPollinationTb.getMVectorTaskCode());
                tcHarvestSeedTb.setFVectorTaskCode(tcPollinationTb.getFVectorTaskCode());
                tcHarvestSeedTb.setMGenerationCode(tcPollinationTb.getMGenerationCode());
                tcHarvestSeedTb.setFGenerationCode(tcPollinationTb.getFGenerationCode());
                tcHarvestSeedTb.setMTcGene(tcPollinationTb.getMTcGene());
                tcHarvestSeedTb.setFTcGene(tcPollinationTb.getFTcGene());
                tcHarvestSeedTb.setPollinationDate(tcPollinationTb.getPollinationDate());
                tcHarvestSeedTb.setPollinationMethodCode(tcPollinationTb.getPollinationMethodCode());
                tcHarvestSeedTb.setHarvestTypeCode(tcPollinationTb.getHarvestTypeCode());
                tcHarvestSeedTb.setRemark(tcHarvestExcelDTO.getRemark());
                tcHarvestSeedTb.setHarvestTime(tcHarvestExcelDTO.getHarvestTime());
                tcHarvestSeedTb.setSeedNumber(new BigDecimal(StringUtils.isEmpty(tcHarvestExcelDTO.getSeedNumber()) ? "0" : tcHarvestExcelDTO.getSeedNumber()));
                tcHarvestSeedTb.setUnit(tcHarvestExcelDTO.getUnit());
                tcHarvestSeedTb.setHarvestApplyNum(tcHarvestSeedApplyTb.getHarvestApplyNum());
                tcHarvestSeedTb.setFSingleNumber(tcPollinationTb.getFSingleNumber());
                tcHarvestSeedTb.setMSingleNumber(tcPollinationTb.getMSingleNumber());
                tcHarvestSeedTb.setMTcSampleCode(tcPollinationTb.getMSampleCode());
                tcHarvestSeedTb.setFTcSampleCode(tcPollinationTb.getFSampleCode());
                tcHarvestSeedTb.setMaterialType(tcHarvestExcelDTO.getMaterialType());
                tcHarvestSeedTbList.add(tcHarvestSeedTb);
            }
            tcHarvestSeedTbMapper.insertBatch(tcHarvestSeedTbList);
        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        TcHarvestTaskDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcHarvestTaskDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("试验编号", dto.getExperimentNum()));
        sections.add(buildFieldSection("申请信息", applyFields));

        List<TcHarvestSeedTb> harvestSeedList = tcHarvestSeedTbMapper.selectList(new LambdaQueryWrapper<TcHarvestSeedTb>()
                .eq(TcHarvestSeedTb::getHarvestApplyNum, bioTaskDtlTb.getTaskNum())
                .orderByAsc(TcHarvestSeedTb::getId));
        if (CollectionUtil.isNotEmpty(harvestSeedList)) {
            Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
            Map<String, String> harvestTypeNameMap = buildDictNameMap(BioDictTypeEnum.HARVEST_TYPE);
            List<String> headers = buildHarvestHeaders("收获备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (TcHarvestSeedTb item : harvestSeedList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("母本小区编号", item.getMRegionNum());
                row.put("母本种子编号", item.getMSeedNum());
                row.put("母本单株编号", item.getMSingleNumber());
                row.put("母本取样编号", item.getMSampleCode());
                row.put("母本品种", resolveBreedName(breedNameMap, item.getMBreedCode()));
                row.put("父本小区编号", item.getFRegionNum());
                row.put("父本种子编号", item.getFSeedNum());
                row.put("父本单株编号", item.getFSingleNumber());
                row.put("父本取样编号", item.getFSampleCode());
                row.put("父本品种", resolveBreedName(breedNameMap, item.getFBreedCode()));
                row.put("收获数量", formatHarvestNumber(item.getSeedNumber(), item.getUnit()));
                row.put("收获方式", translateDict(harvestTypeNameMap, item.getHarvestTypeCode()));
                row.put("收获时间", item.getHarvestTime());
                row.put("收获备注", item.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("收获明细", headers, rows));
            return sections;
        }

        if (CollectionUtil.isNotEmpty(dto.getTcHarvestExcelDTOList())) {
            List<String> headers = buildHarvestHeaders("备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (TcHarvestExcelDTO item : dto.getTcHarvestExcelDTOList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("母本小区编号", item.getMotherRegionNum());
                row.put("母本种子编号", item.getMotherSeedNum());
                row.put("母本单株编号", item.getMotherSingleNumber());
                row.put("母本取样编号", item.getMotherSampleCode());
                row.put("母本品种", item.getMotherBreedName());
                row.put("父本小区编号", item.getFatherRegionNum());
                row.put("父本种子编号", item.getFatherSeedNum());
                row.put("父本单株编号", item.getFatherSingleNumber());
                row.put("父本取样编号", item.getFatherSampleCode());
                row.put("父本品种", item.getFatherBreedName());
                row.put("收获数量", formatHarvestNumber(item.getSeedNumber(), item.getUnit()));
                row.put("收获方式", item.getHarvestTypeName());
                row.put("收获时间", item.getHarvestTime());
                row.put("备注", item.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("收获明细", headers, rows));
        }

        return sections;
    }

    private List<String> buildHarvestHeaders(String remarkHeader) {
        return Arrays.asList("母本小区编号", "母本种子编号", "母本单株编号", "母本取样编号", "母本品种",
                "父本小区编号", "父本种子编号", "父本单株编号", "父本取样编号", "父本品种",
                "收获数量", "收获方式", "收获时间", remarkHeader);
    }

    private String resolveBreedName(Map<String, String> breedNameMap, String breedCode) {
        if (StringUtils.isEmpty(breedCode)) {
            return "";
        }
        return breedNameMap.getOrDefault(breedCode, breedCode);
    }

    private String formatHarvestNumber(BigDecimal seedNumber, String unit) {
        if (seedNumber == null) {
            return StringUtils.isEmpty(unit) ? "" : unit;
        }
        return seedNumber.stripTrailingZeros().toPlainString() + (StringUtils.isEmpty(unit) ? "" : unit);
    }

    private String formatHarvestNumber(String seedNumber, String unit) {
        return (StringUtils.isEmpty(seedNumber) ? "" : seedNumber) + (StringUtils.isEmpty(unit) ? "" : unit);
    }

    private void validateHarvestTimeFormat(String harvestTime) {
        if (StringUtils.isEmpty(harvestTime)) {
            throw new BusinessException("参数缺失：收获时间");
        }
        if (!harvestTime.matches("\\d{4}-\\d{2}-\\d{2}")) {
            throw new BusinessException("收获时间格式错误，请填写yyyy-MM-dd：" + harvestTime);
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(harvestTime);
        } catch (Exception e) {
            throw new BusinessException("收获时间格式错误，请填写yyyy-MM-dd：" + harvestTime);
        }
    }

    private Map<String, String> buildDictNameMap(BioDictTypeEnum dictTypeEnum) {
        return bioDictMapper.selectAllByDictType(dictTypeEnum.name()).stream()
                .collect(Collectors.toMap(BioDict::getDictValueCode, BioDict::getDictValueName, (left, right) -> left));
    }

    private String translateDict(Map<String, String> dictNameMap, String dictValueCode) {
        if (StringUtils.isEmpty(dictValueCode)) {
            return "";
        }
        return dictNameMap.getOrDefault(dictValueCode, dictValueCode);
    }
}
