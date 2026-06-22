package com.bio.drqi.manage.flowtask.seed;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.domain.*;
import com.bio.drqi.common.enums.SeedSourceEnum;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.bio.drqi.manage.service.common.SeedPlantService;
import com.bio.drqi.mapper.*;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 入库
 */
@Service("seed_store_apply")
@Slf4j
public class SeedStoreApplyProcService extends AbstractSeedTaskService {

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private BioDictMapper bioDictMapper;

    @Resource
    private SeedPlantService seedPlantService;


    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        SeedInStoreDTO seedInStoreDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedInStoreDTO.class);
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectList(null);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, CerBreedDict> cerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getBreedCode(), cerBreedDict -> cerBreedDict));
        Map<String, CerSpeciesConf> cerSpeciesConfMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, cerSpeciesConf -> cerSpeciesConf));

        for (SeedInStoreDTO.ExecuteFormContent executeFormContent : seedInStoreDTO.getExecuteForm().getExecuteFormContentList()) {
            log.info("种子入库 executeFormContent={}", JSONUtil.toJsonStr(executeFormContent));
            ValidatorUtil.validator(executeFormContent);
            BeanUtils.trimFiledSpace(executeFormContent);
            //通用校验
            if (StringUtils.isNotEmpty(executeFormContent.getHarvestTime())) {
                if (!validateDateFormat(executeFormContent.getHarvestTime())) {
                    throw new BusinessException("收获日期格式错误,要求yyyy-mm-dd，实际格式：" + executeFormContent.getHarvestTime());
                }
            }
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfMap.get(executeFormContent.getSpeciesCode());
            if (cerSpeciesConf == null) {
                throw new BusinessException("不支持此物种入库:" + executeFormContent.getSpeciesCode());
            }

            CerBreedDict cerBreedDict = cerBreedDictMap.get(executeFormContent.getBreedCode());
            if (cerBreedDict == null) {
                throw new BusinessException("不支持此品种入库：" + executeFormContent.getBreedCode());
            }

            if (executeFormContent.getSeedNumber() == null || executeFormContent.getSeedNumber().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("入库数量非法");
            }
            if (StringUtils.isEmpty(GenerationEnum.getGenerationDesc(executeFormContent.getGeneration()))) {
                throw new BusinessException("代次填写错误：" + executeFormContent.getGeneration());
            }
            if (StringUtils.isNotEmpty(executeFormContent.getProductionLocationName())) {
                SeedProduceAddressDict seedProduceAddressDict = seedProduceAddressDictMapper.selectOneByAddressName(executeFormContent.getProductionLocationName());
                if (seedProduceAddressDict == null) {
                    throw new BusinessException("种子生产地点填写错误");
                }
                executeFormContent.setProductionLocationCode(seedProduceAddressDict.getAddressCode());
            }
            if (StringUtils.isNotEmpty(executeFormContent.getVectorTaskCode())) {
                CerVectorTaskTb vectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(executeFormContent.getVectorTaskCode());
                if (vectorTaskTb == null) {
                    throw new BusinessException("实施方案不存在：" + executeFormContent.getVectorTaskCode());
                }
                CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(vectorTaskTb.getProjectCode());
                executeFormContent.setTargetCharacter(cerProjectTb.getProjectName());
            }
            if (SeedSourceEnum.getByCode(executeFormContent.getSource()) == null) {
                throw new BusinessException("来源渠道不正确：" + executeFormContent.getSource());
            }

            //CER 校验
            if (SeedSourceEnum.CODE_1.code.equals(executeFormContent.getSource())&&GenerationEnum.T0.code.equals(executeFormContent.getGeneration())) {
                if(StringUtils.isEmpty(executeFormContent.getPlantCode())){
                    throw new BusinessException("来源自CER的种子必然有种植编号");
                }
                PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(executeFormContent.getPlantCode());
                if (plantSingleStockTb == null) {
                    throw new BusinessException(executeFormContent.getPlantCode() + "种植编号不存在:" + executeFormContent.getPlantCode());
                }
                if(!StringUtils.equals(executeFormContent.getVectorTaskCode(),plantSingleStockTb.getVectorTaskCode())){
                    throw new BusinessException("种植编号为："+executeFormContent.getPlantCode()+"的种子实施方案编号填写不正确");
                }
                if(!StringUtils.equals(executeFormContent.getSpeciesCode(),plantSingleStockTb.getSpeciesCode())){
                    throw new BusinessException("种植编号为："+executeFormContent.getPlantCode()+"的种子物种填写不正确");
                }
                if(!StringUtils.equals(executeFormContent.getBreedCode(),plantSingleStockTb.getBreedCode())){
                    throw new BusinessException("种植编号为："+executeFormContent.getPlantCode()+"的种子品种填写不正确");
                }
            }
            //大田校验
            if (SeedSourceEnum.CODE_4.code.equals(executeFormContent.getSource())) {
                if (StringUtils.isEmpty(executeFormContent.getFatherSeedNum())) {
                    throw new BusinessException("父本种子编号必填");
                }
                if (StringUtils.isEmpty(executeFormContent.getMatherSeedNum())) {
                    throw new BusinessException("母本种子编号必填");
                }
                if (StringUtils.isEmpty(executeFormContent.getFatherRegionNum())) {
                    throw new BusinessException("父本小区编号必填");
                }
                if (StringUtils.isEmpty(executeFormContent.getMatherRegionNum())) {
                    throw new BusinessException("母本小区编号必填");
                }
                if (StringUtils.isEmpty(executeFormContent.getExperimentNum())) {
                    throw new BusinessException("试验编号必填");
                }
                TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(executeFormContent.getExperimentNum());
                if (tcExperimentTb == null) {
                    throw new BusinessException("试验编号不存在：" + executeFormContent.getExperimentNum());
                }
                TcExperimentDesignTb matherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByRegionNumAndSeedNum(executeFormContent.getMatherRegionNum(), executeFormContent.getMatherSeedNum());
                if (matherTcExperimentDesignTb == null) {
                    throw new BusinessException("试验方案中不存在此小区或者母本种子，当前试验方案编号：" + tcExperimentTb.getExperimentNum() + "母本小区编号：" + executeFormContent.getMatherRegionNum() + "母本种子编号:" + executeFormContent.getMatherSeedNum());
                }
                TcExperimentDesignTb fatherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByRegionNumAndSeedNum(executeFormContent.getFatherRegionNum(), executeFormContent.getFatherSeedNum());
                if (fatherTcExperimentDesignTb == null) {
                    throw new BusinessException("试验方案中不存在此小区或者母本种子，当前试验方案编号：" + tcExperimentTb.getExperimentNum() + "父本小区编号：" + executeFormContent.getFatherRegionNum() + "父本种子编号:" + executeFormContent.getFatherSeedNum());
                }
                TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSingleNumberAndMSingleNumber(executeFormContent.getExperimentNum(), executeFormContent.getFatherRegionNum(), executeFormContent.getMatherRegionNum(), executeFormContent.getFatherSeedNum(), executeFormContent.getMatherSeedNum(), executeFormContent.getFatherSingleNum(), executeFormContent.getMatherSingleNum());
                if (tcPollinationTb == null) {
                    throw new BusinessException("无此授粉信息或者授粉信息不匹配：当前对应数据行的试验方案：" + executeFormContent.getExperimentNum() + " 父本种子编号：" + executeFormContent.getFatherSeedNum() + " 母本种子编号：" + executeFormContent.getMatherSeedNum());
                }
            }
            executeFormContent.setBreedName(cerBreedDict.getBreedName());
            executeFormContent.setSpeciesName(cerSpeciesConf.getSpeciesName());
        }
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        // 需要进行数据回退， 需要回退入库日志，库存记录 和提交记录 ，如果某一条种子已经使用，则整个工单无法回退
        SeedInStoreDTO seedInStoreDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedInStoreDTO.class);
        List<SeedStockInLog> seedStockInLogList = seedStockInLogMapper.selectAllByTaskNum(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(seedStockInLogList)) {
            for (SeedStockInLog seedStockInLog : seedStockInLogList) {
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedStockInLog.getSeedNum());
                if (seedStockTb.getSeedNumber().compareTo(seedStockTb.getTotalNumber()) != 0) {
                    throw new BusinessException("该批次中有种子被使用，无法撤销 使用种子编号：" + seedStockTb.getSeedNum());
                }
                seedPlantService.seedInStockDeleteRefPlant(seedStockTb, seedStockInLog.getUniqueCode());
            }


            //删除种子入库记录
            seedStockInLogMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
            //删除种子库存
            seedStockTbMapper.deleteBySeedNumIn(seedStockInLogList.stream().map(SeedStockInLog::getSeedNum).collect(Collectors.toList()));
        }
        seedInStoreDTO.getExecuteForm().getExecuteFormContentList().stream().forEach(executeFormContent -> {
            executeFormContent.setStoreFlag(CerProjectContents.N);
        });
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(seedInStoreDTO));
    }


    private boolean validateDateFormat(String date) {
        String pattern = "\\d{4}-\\d{2}-\\d{2}"; // 定义日期格式为yyyy-MM-dd
        return date.matches(pattern);
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        SeedInStoreDTO seedInStoreDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedInStoreDTO.class);
        if (seedInStoreDTO == null || seedInStoreDTO.getExecuteForm() == null || CollectionUtil.isEmpty(seedInStoreDTO.getExecuteForm().getExecuteFormContentList())) {
            return Collections.emptyList();
        }

        List<SeedInStoreDTO.ExecuteFormContent> contentList = seedInStoreDTO.getExecuteForm().getExecuteFormContentList();
        Map<String, String> speciesNameMap = cerSpeciesConfMapper.selectList(null).stream()
                .collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName, (left, right) -> left));
        Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
        Map<String, String> addressNameMap = seedProduceAddressDictMapper.selectAll().stream()
                .collect(Collectors.toMap(SeedProduceAddressDict::getAddressCode, SeedProduceAddressDict::getAddressName, (left, right) -> left));

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();

        List<String> headers = Arrays.asList(
                "种子编号", "来源", "代次", "物种", "品种", "种子数量", "单位",
                "收获方式", "授粉方式", "收获时间", "生产地点", "材料类型",
                "实施方案编号", "试验方案编号", "种植编号", "父本种子编号",
                "母本种子编号", "父本小区编号", "母本小区编号", "备注"
        );
        List<Map<String, Object>> rows = new ArrayList<>();
        for (SeedInStoreDTO.ExecuteFormContent content : contentList) {
            Map<String, Object> row = new LinkedHashMap<>();
            row.put("种子编号", content.getSeedNum());
            row.put("来源", translateSeedSource(content.getSource()));
            row.put("代次", GenerationEnum.getGenerationDesc(content.getGeneration()));
            row.put("物种", StringUtils.isNotEmpty(content.getSpeciesName()) ? content.getSpeciesName() : speciesNameMap.get(content.getSpeciesCode()));
            row.put("品种", StringUtils.isNotEmpty(content.getBreedName()) ? content.getBreedName() : breedNameMap.get(content.getBreedCode()));
            row.put("种子数量", formatSeedNumber(content.getSeedNumber()));
            row.put("单位", content.getUnit());
            row.put("收获方式", translateDict(BioDictTypeEnum.HARVEST_TYPE, content.getHarvestType()));
            row.put("授粉方式", translateDict(BioDictTypeEnum.POLLINATE_TYPE, content.getPollinationMethod()));
            row.put("收获时间", content.getHarvestTime());
            row.put("生产地点", StringUtils.isNotEmpty(content.getProductionLocationName()) ? content.getProductionLocationName() : addressNameMap.get(content.getProductionLocationCode()));
            row.put("材料类型", translateDict(BioDictTypeEnum.MATERIAL_TYPE, content.getMaterialType()));
            row.put("实施方案编号", content.getVectorTaskCode());
            row.put("试验方案编号", content.getExperimentNum());
            row.put("种植编号", content.getPlantCode());
            row.put("父本种子编号", content.getFatherSeedNum());
            row.put("母本种子编号", content.getMatherSeedNum());
            row.put("父本小区编号", content.getFatherRegionNum());
            row.put("母本小区编号", content.getMatherRegionNum());
            row.put("备注", content.getRemarks());
            rows.add(row);
        }
        sections.add(buildTableSection("入库明细（份数：" + contentList.size() + "）", headers, rows));
        return sections;
    }

    private String translateSeedSource(String sourceCode) {
        SeedSourceEnum seedSourceEnum = SeedSourceEnum.getByCode(sourceCode);
        return seedSourceEnum == null ? sourceCode : seedSourceEnum.name;
    }

    private String translateDict(BioDictTypeEnum dictTypeEnum, String dictValueCode) {
        if (StringUtils.isEmpty(dictValueCode)) {
            return "";
        }
        BioDict bioDict = bioDictMapper.selectOneByDictTypeAndDictValueCode(dictTypeEnum.name(), dictValueCode);
        return bioDict == null ? dictValueCode : bioDict.getDictValueName();
    }

    private String formatSeedNumber(BigDecimal seedNumber) {
        if (seedNumber == null) {
            return "";
        }
        return seedNumber.stripTrailingZeros().toPlainString();
    }
}
