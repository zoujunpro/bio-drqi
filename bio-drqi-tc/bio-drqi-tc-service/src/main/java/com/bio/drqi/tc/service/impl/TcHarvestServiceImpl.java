package com.bio.drqi.tc.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONConfig;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.common.enums.SeedSourceEnum;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.BioDict;
import com.bio.drqi.domain.CerBreedDict;
import com.bio.drqi.domain.CerSpeciesConf;
import com.bio.drqi.domain.TcHarvestSeedTb;
import com.bio.drqi.mapper.BioDictMapper;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.CerSpeciesConfMapper;
import com.bio.drqi.mapper.TcHarvestSeedTbMapper;
import com.bio.drqi.tc.req.TcHarvestListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcHarvestSeedStoreApplyReqDTO;
import com.bio.drqi.tc.req.TcHavestDownSeedStockInExcelReqDTO;
import com.bio.drqi.tc.rsp.TcHarvestListPageDetailRspDTO;
import com.bio.drqi.tc.service.TcHarvestService;
import com.bio.flow.dto.BioTaskStartReqDTO;
import com.bio.flow.service.BioTaskService;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class TcHarvestServiceImpl implements TcHarvestService {


    @Value("${cer.properties.excelTemplatePath}")
    private String excelTemplatePath;


    @Resource
    private TcHarvestSeedTbMapper tcHarvestSeedTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private BioDictMapper bioDictMapper;

    @Resource
    private BioTaskService bioTaskService;


    @Override
    public PageInfo<TcHarvestListPageDetailRspDTO> listPage(TcHarvestListPageDetailReqDTO tcHarvestListPageDetailReqDTO) {
        PageHelper.startPage(tcHarvestListPageDetailReqDTO.getPageNum(), tcHarvestListPageDetailReqDTO.getPageSize());
        List<TcHarvestSeedTb> tcHarvestSeedTbList = tcHarvestSeedTbMapper.selectSelective(BeanUtils.copyProperties(tcHarvestListPageDetailReqDTO, TcHarvestSeedTb.class));
        PageInfo<TcHarvestSeedTb> srcPageInfo = new PageInfo<>(tcHarvestSeedTbList);
        PageInfo<TcHarvestListPageDetailRspDTO> resultPageInfo = BeanUtils.copyPageInfoProperties(srcPageInfo, TcHarvestListPageDetailRspDTO.class);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, String> codeNameCerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
        Map<String, String> pollinationMethodNameMap = buildDictNameMap(BioDictTypeEnum.POLLINATE_TYPE);
        Map<String, String> harvestTypeNameMap = buildDictNameMap(BioDictTypeEnum.HARVEST_TYPE);
        Map<String, String> materialTypeNameMap = buildDictNameMap(BioDictTypeEnum.MATERIAL_TYPE);
        if (CollectionUtil.isNotEmpty(resultPageInfo.getList())) {
            resultPageInfo.getList().forEach(tcPollinationListPageDetailRspDTO -> {
                tcPollinationListPageDetailRspDTO.setFBreedName(codeNameCerBreedDictMap.get(tcPollinationListPageDetailRspDTO.getFBreedCode()));
                tcPollinationListPageDetailRspDTO.setMBreedName(codeNameCerBreedDictMap.get(tcPollinationListPageDetailRspDTO.getMBreedCode()));
                tcPollinationListPageDetailRspDTO.setPollinationMethodName(translateDict(pollinationMethodNameMap, tcPollinationListPageDetailRspDTO.getPollinationMethodCode()));
                tcPollinationListPageDetailRspDTO.setHarvestTypeName(translateDict(harvestTypeNameMap, tcPollinationListPageDetailRspDTO.getHarvestTypeCode()));
                tcPollinationListPageDetailRspDTO.setMaterialTypeName(translateDict(materialTypeNameMap, tcPollinationListPageDetailRspDTO.getMaterialType()));
            });
        }
        return resultPageInfo;
    }

    @Override
    public void downSeedStockInExcel(TcHavestDownSeedStockInExcelReqDTO tcHavestDownSeedStockInExcelReqDTO, HttpServletResponse httpServletResponse) {
        List<TcHarvestSeedTb> tcHarvestSeedTbList = tcHarvestSeedTbMapper.selectBatchIds(tcHavestDownSeedStockInExcelReqDTO.getIdList());
        List<com.bio.drqi.common.dto.SeedInStockExcelDTO> seedInStockExcelDTOList = new ArrayList<>();
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAll();
        Map<String, CerBreedDict> codeNameCerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, cerBreedDict -> cerBreedDict));
        Map<String, String> codeNameCerSpeciesDictMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
        Map<String, String> pollinationMethodNameMap = buildDictNameMap(BioDictTypeEnum.POLLINATE_TYPE);
        Map<String, String> harvestTypeNameMap = buildDictNameMap(BioDictTypeEnum.HARVEST_TYPE);
        Map<String, String> materialTypeNameMap = buildDictNameMap(BioDictTypeEnum.MATERIAL_TYPE);
        if (CollectionUtil.isNotEmpty(tcHarvestSeedTbList)) {
            for (TcHarvestSeedTb tcHarvestSeedTb : tcHarvestSeedTbList) {
                com.bio.drqi.common.dto.SeedInStockExcelDTO seedInStockExcelDTO = new com.bio.drqi.common.dto.SeedInStockExcelDTO();
                seedInStockExcelDTO.setSource(SeedSourceEnum.CODE_4.name);

                seedInStockExcelDTO.setGeneration(StringUtils.isNotEmpty(tcHarvestSeedTb.getMGenerationCode()) ? GenerationEnum.nextGenerationCode(tcHarvestSeedTb.getMGenerationCode()) : null);
                seedInStockExcelDTO.setPlantCode(null);
                seedInStockExcelDTO.setMaterialTypeName(translateDict(materialTypeNameMap, tcHarvestSeedTb.getMaterialType()));
                seedInStockExcelDTO.setVectorTaskCode(tcHarvestSeedTb.getFVectorTaskCode());
                seedInStockExcelDTO.setExperimentNum(tcHarvestSeedTb.getExperimentNum());
                seedInStockExcelDTO.setFatherRegionNum(tcHarvestSeedTb.getFRegionNum());
                seedInStockExcelDTO.setMatherRegionNum(tcHarvestSeedTb.getMRegionNum());
                seedInStockExcelDTO.setFatherSingleNum(tcHarvestSeedTb.getFSingleNumber());
                seedInStockExcelDTO.setMatherSingleNum(tcHarvestSeedTb.getMSingleNumber());
                seedInStockExcelDTO.setProductionLocationName("武清大田");
                seedInStockExcelDTO.setMatherInfo(null);
                seedInStockExcelDTO.setFatherInfo(null);
                seedInStockExcelDTO.setMatherSeedNum(tcHarvestSeedTb.getMSeedNum());
                seedInStockExcelDTO.setFatherSeedNum(tcHarvestSeedTb.getFSeedNum());
                if (StringUtils.isNotEmpty(tcHarvestSeedTb.getMBreedCode())) {
                    CerBreedDict cerBreedDict = codeNameCerBreedDictMap.get(tcHarvestSeedTb.getMBreedCode());
                    if (cerBreedDict != null) {
                        seedInStockExcelDTO.setBreedName(cerBreedDict.getBreedName());
                        seedInStockExcelDTO.setSpeciesName(codeNameCerSpeciesDictMap.get(cerBreedDict.getSpeciesCode()));
                    }
                }
                seedInStockExcelDTO.setHarvestTypeName(translateDict(harvestTypeNameMap, tcHarvestSeedTb.getHarvestTypeCode()));
                seedInStockExcelDTO.setHarvestTime(tcHarvestSeedTb.getHarvestTime());
                seedInStockExcelDTO.setPollinationMethodName(translateDict(pollinationMethodNameMap, tcHarvestSeedTb.getPollinationMethodCode()));
                seedInStockExcelDTO.setSeedNumber(tcHarvestSeedTb.getSeedNumber());
                seedInStockExcelDTO.setUnit(tcHarvestSeedTb.getUnit());
                seedInStockExcelDTO.setAliasName(null);
                seedInStockExcelDTO.setRemarks(tcHarvestSeedTb.getRemark());
                seedInStockExcelDTOList.add(seedInStockExcelDTO);
            }

        }
        ExcelUtil.writeExcel("种子入库数据", "sheet1", seedInStockExcelDTOList, com.bio.drqi.common.dto.SeedInStockExcelDTO.class, httpServletResponse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public BioTaskDtlTb seedStoreApply(TcHarvestSeedStoreApplyReqDTO reqDTO) {
        List<TcHarvestSeedTb> tcHarvestSeedTbList = tcHarvestSeedTbMapper.selectBatchIds(reqDTO.getIdList());
        if (CollectionUtil.isEmpty(tcHarvestSeedTbList)) {
            throw new BusinessException("未查询到收获种子");
        }
        if (tcHarvestSeedTbList.size() != reqDTO.getIdList().size()) {
            throw new BusinessException("部分收获种子不存在");
        }
        List<TcHarvestSeedTb> storedList = tcHarvestSeedTbList.stream()
                .filter(tcHarvestSeedTb -> hasStoredSeedNums(tcHarvestSeedTb.getSeedNums()))
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(storedList)) {
            throw new BusinessException("选择的收获种子已入库，不能重复入库，ID：" +
                    storedList.stream().map(tcHarvestSeedTb -> String.valueOf(tcHarvestSeedTb.getId())).collect(Collectors.joining(",")));
        }

        Map<String, CerBreedDict> breedDictMap = cerBreedDictMapper.selectAll().stream()
                .collect(Collectors.toMap(CerBreedDict::getBreedCode, cerBreedDict -> cerBreedDict, (left, right) -> left));
        SeedStoreTaskForm taskForm = buildSeedStoreTaskForm(tcHarvestSeedTbList, breedDictMap);

        BioTaskStartReqDTO bioTaskStartReqDTO = new BioTaskStartReqDTO();
        bioTaskStartReqDTO.setTaskType("seed_store_apply");
        bioTaskStartReqDTO.setTaskDesc(StringUtils.isNotEmpty(reqDTO.getTaskDesc()) ? reqDTO.getTaskDesc() : buildDefaultTaskDesc(tcHarvestSeedTbList));
        bioTaskStartReqDTO.setFormObject(JSONUtil.toJsonStr(taskForm, new JSONConfig().setIgnoreNullValue(false)));
        bioTaskStartReqDTO.setSelfFlowActorList(null);
        return bioTaskService.start(bioTaskStartReqDTO);
    }

    private SeedStoreTaskForm buildSeedStoreTaskForm(List<TcHarvestSeedTb> tcHarvestSeedTbList, Map<String, CerBreedDict> breedDictMap) {
        SeedStoreTaskForm taskForm = new SeedStoreTaskForm();
        taskForm.setApplyForm(new SeedStoreApplyForm());
        SeedStoreExecuteForm executeForm = new SeedStoreExecuteForm();
        List<SeedStoreExecuteFormContent> contentList = new ArrayList<>();
        for (TcHarvestSeedTb tcHarvestSeedTb : tcHarvestSeedTbList) {
            CerBreedDict mBreedDict = breedDictMap.get(tcHarvestSeedTb.getMBreedCode());
            if (mBreedDict == null) {
                throw new BusinessException("母本品种不存在，收获种子ID：" + tcHarvestSeedTb.getId());
            }
            SeedStoreExecuteFormContent content = new SeedStoreExecuteFormContent();
            content.setSource(SeedSourceEnum.CODE_4.code);
            content.setGeneration(StringUtils.isNotEmpty(tcHarvestSeedTb.getMGenerationCode()) ? GenerationEnum.nextGenerationCode(tcHarvestSeedTb.getMGenerationCode()) : null);
            content.setSpeciesCode(mBreedDict.getSpeciesCode());
            content.setBreedCode(tcHarvestSeedTb.getMBreedCode());
            content.setPollinationMethod(tcHarvestSeedTb.getPollinationMethodCode());
            content.setHarvestType(tcHarvestSeedTb.getHarvestTypeCode());
            content.setHarvestTime(tcHarvestSeedTb.getHarvestTime());
            content.setSeedNumber(tcHarvestSeedTb.getSeedNumber());
            content.setUnit(tcHarvestSeedTb.getUnit());
            content.setProductionLocationName("武清大田");
            content.setVectorTaskCode(tcHarvestSeedTb.getFVectorTaskCode());
            content.setExperimentNum(tcHarvestSeedTb.getExperimentNum());
            content.setFatherRegionNum(tcHarvestSeedTb.getFRegionNum());
            content.setMatherRegionNum(tcHarvestSeedTb.getMRegionNum());
            content.setFatherSingleNum(tcHarvestSeedTb.getFSingleNumber());
            content.setMatherSingleNum(tcHarvestSeedTb.getMSingleNumber());
            content.setMatherSeedNum(tcHarvestSeedTb.getMSeedNum());
            content.setFatherSeedNum(tcHarvestSeedTb.getFSeedNum());
            content.setMaterialType(tcHarvestSeedTb.getMaterialType());
            content.setRemarks(tcHarvestSeedTb.getRemark());
            content.setStoreFlag(BioDrQiContents.N);
            content.setUniqueCode("tc_harvest_seed_" + tcHarvestSeedTb.getId());
            contentList.add(content);
        }
        executeForm.setExecuteFormContentList(contentList);
        taskForm.setExecuteForm(executeForm);
        return taskForm;
    }

    private String buildDefaultTaskDesc(List<TcHarvestSeedTb> tcHarvestSeedTbList) {
        List<String> experimentNumList = tcHarvestSeedTbList.stream()
                .map(TcHarvestSeedTb::getExperimentNum)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
        String experimentDesc = CollectionUtil.isEmpty(experimentNumList) ? "" : "，试验编号：" + String.join(",", experimentNumList);
        return "收获种子入库申请，共" + tcHarvestSeedTbList.size() + "条" + experimentDesc;
    }

    private boolean hasStoredSeedNums(String seedNums) {
        if (StringUtils.isEmpty(seedNums)) {
            return false;
        }
        try {
            return CollectionUtil.isNotEmpty(JSONUtil.toList(seedNums, String.class));
        } catch (Exception e) {
            return true;
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

    @lombok.Data
    private static class SeedStoreTaskForm {
        private SeedStoreApplyForm applyForm;
        private SeedStoreExecuteForm executeForm;
    }

    @lombok.Data
    private static class SeedStoreApplyForm {
        private List<Object> applyFromContentList = new ArrayList<>();
    }

    @lombok.Data
    private static class SeedStoreExecuteForm {
        private String excelUrl;
        private List<SeedStoreExecuteFormContent> executeFormContentList = new ArrayList<>();
    }

    @lombok.Data
    private static class SeedStoreExecuteFormContent {
        private String seedNum;
        private String source;
        private String plantCode;
        private String vectorTaskCode;
        private String fatherInfo;
        private String matherInfo;
        private String matherSeedNum;
        private String fatherSeedNum;
        private String generation;
        private String speciesCode;
        private String breedCode;
        private String pollinationMethod;
        private String harvestType;
        private String harvestTime;
        private BigDecimal seedNumber;
        private String unit;
        private String productionLocationName;
        private String productionLocationCode;
        private String targetCharacter;
        private String remarks;
        private String stockLocationNum;
        private String geneType;
        private String aliasName;
        private String materialType;
        private String storeFlag;
        private String uniqueCode;
        private String matherRegionNum;
        private String fatherRegionNum;
        private String geneSeparateFlag;
        private String transFlag;
        private String experimentNum;
        private String fatherSingleNum;
        private String matherSingleNum;
    }
}
