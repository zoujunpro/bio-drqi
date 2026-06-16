package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.util.LetterUtil;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.drqi.enums.VectorTaskStatusEnum;
import com.bio.drqi.manage.dto.project.ImplementPlanAddDTO;
import com.bio.drqi.manage.service.project.VectorTaskService;
import com.bio.drqi.manage.vector.req.GetVectorTaskNumReqDTO;
import com.bio.drqi.manage.vector.req.QueryPageVectorReqDTO;
import com.bio.drqi.manage.vector.req.VectorTaskModifyVectorTaskCodeReqDTO;
import com.bio.drqi.manage.vector.rsp.CerImplementationPlanBaseInfoRspDTO;
import com.bio.drqi.manage.vector.rsp.CerImplementationPlanFullInfoRspDTO;
import com.bio.drqi.manage.vector.rsp.StepListRspDTO;
import com.bio.drqi.manage.vector.rsp.VectorListPageRspDTO;
import com.bio.drqi.manage.vector.rsp.VectorTaskSpeciesRspDTO;
import com.bio.drqi.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VectorTaskServiceImpl implements VectorTaskService {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;


    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerVectorStepLogMapper cerVectorStepLogMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerInstantVerifyTaskTbMapper cerInstantVerifyTaskTbMapper;

    @Resource
    private BioSampleCodePrefixTbMapper bioSampleCodePrefixTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;

    @Resource
    private SeedStockOutLogMapper seedStockOutLogMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;


    @Override
    public PageInfo<VectorListPageRspDTO> listPage(QueryPageVectorReqDTO queryPageVectorReqDTO) {
        PageHelper.startPage(queryPageVectorReqDTO.getPageNum(), queryPageVectorReqDTO.getPageSize());
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectSelective(BeanUtils.copyProperties(queryPageVectorReqDTO, CerVectorTaskTb.class));
        PageInfo<CerVectorTaskTb> srcPage = new PageInfo<>(cerVectorTaskTbList);
        PageInfo<VectorListPageRspDTO> vectorBaseInfoRspDTOPageInfo = BeanUtils.copyPageInfoProperties(srcPage, VectorListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(vectorBaseInfoRspDTOPageInfo.getList())) {
            Map<String, String> breedMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
            Map<String, String> speciesMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
            vectorBaseInfoRspDTOPageInfo.getList().forEach(vectorListPageRspDTO -> {
                vectorListPageRspDTO.setBreedName(breedMap.get(vectorListPageRspDTO.getBreedCode()));
                vectorListPageRspDTO.setSpeciesName(speciesMap.get(vectorListPageRspDTO.getSpeciesCode()));
                vectorListPageRspDTO.setCurrentStepName(ImplementationPlanTypeEnum.getDesc(vectorListPageRspDTO.getCurrentStepCode()));
            });
        }
        return vectorBaseInfoRspDTOPageInfo;
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listAll() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectSelective(null);
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listBySpeciesCode(String speciesCode) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectSelective(CerVectorTaskTb.builder().speciesCode(speciesCode).build());
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listAllBySubProject(Integer subProjectId) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllBySubProjectIdAndTaskStatusOrderByIdDesc(subProjectId, VectorTaskStatusEnum.TASK_STATUS_2.status);
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listForVectorBuild(Integer subProjectId) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listForVectorBuild(subProjectId);
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }


    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listForTransForm(Integer subProjectId) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listForTransForm(subProjectId);
        List<CerImplementationPlanBaseInfoRspDTO> result = BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
        Map<Integer, List<String>> agrobacteriumInformationMap = getAgrobacteriumInformationMap(subProjectId);

        result.forEach(cerImplementationPlanBaseInfoRspDTO -> {
            if (StringUtils.isEmpty(cerImplementationPlanBaseInfoRspDTO.getAcceptorMaterial())) {
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(cerImplementationPlanBaseInfoRspDTO.getBreedCode());
                if (cerBreedDict != null) {
                    cerImplementationPlanBaseInfoRspDTO.setAcceptorMaterial(cerBreedDict.getBreedName());
                }
            }
            List<String> agrobacteriumInformationList = agrobacteriumInformationMap.get(cerImplementationPlanBaseInfoRspDTO.getId());
            cerImplementationPlanBaseInfoRspDTO.setAgrobacteriumInformationList(agrobacteriumInformationList);

        });
        return result;
    }

    private Map<Integer, List<String>> getAgrobacteriumInformationMap(Integer subProjectId) {
        CerPlasmidQualityTb query = new CerPlasmidQualityTb();
        query.setSubProjectId(subProjectId);
        List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectSelective(query);
        if (CollectionUtil.isEmpty(cerPlasmidQualityTbList)) {
            return Collections.emptyMap();
        }
        return cerPlasmidQualityTbList.stream()
                .filter(item -> isAgrobacteriumCheck(item.getQualityInspectionType()))
                .filter(item -> item.getVectorTaskId() != null)
                .filter(item -> StringUtils.isNotEmpty(item.getAgrobacteriumInformation()))
                .collect(Collectors.groupingBy(
                        CerPlasmidQualityTb::getVectorTaskId,
                        Collectors.collectingAndThen(Collectors.toList(), this::getDistinctAgrobacteriumInformationList)
                ));
    }

    private boolean isAgrobacteriumCheck(String qualityInspectionType) {
        return "2".equals(qualityInspectionType)
                || "农杆菌检测".equals(qualityInspectionType)
                || "农杆菌转化".equals(qualityInspectionType);
    }

    private List<String> getDistinctAgrobacteriumInformationList(List<CerPlasmidQualityTb> plasmidQualityTbList) {
        return plasmidQualityTbList.stream()
                .map(CerPlasmidQualityTb::getAgrobacteriumInformation)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listForMoveSeed() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listForMoveSeed();
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listForFirstSample(String speciesCode) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listForFirstSample(speciesCode);
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listForPlasmid(Integer subProjectId) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listForPlasmid(subProjectId);
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }


    @Override
    public String getTaskNum(GetVectorTaskNumReqDTO getVectorTaskNumReqDTO) {
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectById(getVectorTaskNumReqDTO.getSubProjectId());
        if (cerSubProjectTb == null) {
            throw new BusinessException("子项目不存在");
        }
        //当前子项目下所有实施方案
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllBySubProjectId(cerSubProjectTb.getId());
        //复制工单
        if (StringUtils.isNotEmpty(getVectorTaskNumReqDTO.getVectorTaskCode())) {
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(getVectorTaskNumReqDTO.getVectorTaskCode());
            if (cerVectorTaskTb != null && cerVectorTaskTb.getVectorTaskCode().matches("^[0-9a-zA-Z]{1,9}\\-[0-9]{2}[a-z]$")) {
                cerVectorTaskTbList = cerVectorTaskTbList.stream().filter(vectorTask -> vectorTask.getVectorTaskCode().matches("^[0-9a-zA-Z]{1,9}\\-[0-9]{2}[a-z]$")).filter(vectorTaskTb -> vectorTaskTb.getVectorTaskCode().contains(cerVectorTaskTb.getVectorTaskCode().substring(0, cerVectorTaskTb.getVectorTaskCode().length() - 1))).collect(Collectors.toList());
                List<String> lastLetter = cerVectorTaskTbList.stream().map(vectorTask -> vectorTask.getVectorTaskCode().substring(vectorTask.getVectorTaskCode().length() - 1)).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                return cerVectorTaskTb.getVectorTaskCode().substring(0, cerVectorTaskTb.getVectorTaskCode().length() - 1) + LetterUtil.nextLetter(lastLetter.get(0));
            } else {
                Integer maxNumber = findMaxIndex(cerVectorTaskTbList);
                return getVectorTaskCode(cerSubProjectTb.getSubProjectCode(), maxNumber);
            }
        } else {
            //新建立工单 判断是否有字母
            Integer maxNumber = findMaxIndex(cerVectorTaskTbList);
            if (CerProjectContents.Y.equals(getVectorTaskNumReqDTO.getIfLetter())) {
                return getVectorTaskCode(cerSubProjectTb.getSubProjectCode(), maxNumber) + "a";
            } else {
                return getVectorTaskCode(cerSubProjectTb.getSubProjectCode(), maxNumber);
            }
        }
    }


    private Integer findMaxIndex(List<CerVectorTaskTb> cerVectorTaskTbList) {
        if (CollectionUtil.isEmpty(cerVectorTaskTbList)) {
            return 0;
        }
        List<String> numberList = new ArrayList<>();
        List<CerVectorTaskTb> haveLetterCerVectorTaskTbList = cerVectorTaskTbList.stream().filter(vectorTask -> vectorTask.getVectorTaskCode().matches("^[0-9a-zA-Z]{1,8}\\-[0-9]{2}[a-z]$")).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(haveLetterCerVectorTaskTbList)) {
            List<String> numberList1 = haveLetterCerVectorTaskTbList.stream().map(vectorTask -> vectorTask.getVectorTaskCode().substring(vectorTask.getVectorTaskCode().length() - 3, vectorTask.getVectorTaskCode().length() - 1)).collect(Collectors.toList());
            numberList.addAll(numberList1);
        }
        List<CerVectorTaskTb> noLetterCerVectorTaskTbList = cerVectorTaskTbList.stream().filter(vectorTask -> vectorTask.getVectorTaskCode().matches("^[0-9a-zA-Z]{1,8}\\-[0-9]{2}$")).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noLetterCerVectorTaskTbList)) {
            List<String> numberList2 = noLetterCerVectorTaskTbList.stream().map(vectorTask -> vectorTask.getVectorTaskCode().substring(vectorTask.getVectorTaskCode().length() - 2)).collect(Collectors.toList());
            numberList.addAll(numberList2);
        }
        if (CollectionUtil.isEmpty(numberList)) {
            return 0;
        } else {
            numberList = numberList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            return Integer.valueOf(numberList.get(0));
        }
    }

    @Override
    public List<StepListRspDTO> stepList(Integer id) {
        List<StepListRspDTO> result = new ArrayList<>();
        List<CerVectorStepLog> cerVectorStepLogList = cerVectorStepLogMapper.selectAllByVectorTaskIdOrderById(id);
        List<String> stepCodeList = cerVectorStepLogList.stream().map(CerVectorStepLog::getStepCode).collect(Collectors.toList());
        for (ImplementationPlanTypeEnum implementationPlanTypeEnum : ImplementationPlanTypeEnum.values()) {
            StepListRspDTO stepListRspDTO = new StepListRspDTO();
            stepListRspDTO.setStepCode(implementationPlanTypeEnum.name());
            stepListRspDTO.setStepName(implementationPlanTypeEnum.desc);
            stepListRspDTO.setShowFlag(CollectionUtil.isNotEmpty(stepCodeList) && stepCodeList.contains(implementationPlanTypeEnum.name()) ? CerProjectContents.Y : CerProjectContents.N);
            result.add(stepListRspDTO);
        }
        return result;
    }

    @Override
    public List<StepListRspDTO> stepListByCode(String vectorTaskCode) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb == null) {
            return new ArrayList<>();
        }
        return stepList(cerVectorTaskTb.getId());
    }

    @Override
    public CerImplementationPlanBaseInfoRspDTO detail(Integer id) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(id);
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerSubProjectTb.getTaskNum());
        BioSampleCodePrefixTb bioSampleCodePrefixTb = bioSampleCodePrefixTbMapper.selectOneByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        CerImplementationPlanBaseInfoRspDTO cerImplementationPlanBaseInfoRspDTO = BeanUtils.copyProperties(cerVectorTaskTb, CerImplementationPlanBaseInfoRspDTO.class);
        cerImplementationPlanBaseInfoRspDTO.setSampleCodePrefix(bioSampleCodePrefixTb.getSampleCodePrefix());
        if (StringUtils.isEmpty(cerImplementationPlanBaseInfoRspDTO.getAcceptorMaterial())) {
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(cerImplementationPlanBaseInfoRspDTO.getBreedCode());
            if (cerBreedDict != null) {
                cerImplementationPlanBaseInfoRspDTO.setAcceptorMaterial(cerBreedDict.getBreedName());
            }
        }
        cerImplementationPlanBaseInfoRspDTO.setSubProjectDesc(bioTaskDtlTb.getTaskDesc());
        cerImplementationPlanBaseInfoRspDTO.setProjectName(cerProjectTb.getProjectName());
        return cerImplementationPlanBaseInfoRspDTO;
    }

    @Override
    public CerImplementationPlanBaseInfoRspDTO detailByCode(String vectorTaskCode) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案编号不存在");
        }
        CerImplementationPlanBaseInfoRspDTO cerImplementationPlanBaseInfoRspDTO = BeanUtils.copyProperties(cerVectorTaskTb, CerImplementationPlanBaseInfoRspDTO.class);
        if (cerImplementationPlanBaseInfoRspDTO != null && StringUtils.isNotEmpty(cerImplementationPlanBaseInfoRspDTO.getProjectCode())) {
            CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerImplementationPlanBaseInfoRspDTO.getProjectCode());
            cerImplementationPlanBaseInfoRspDTO.setProjectName(cerProjectTb.getProjectName());
        }
        return cerImplementationPlanBaseInfoRspDTO;
    }

    @Override
    public CerImplementationPlanFullInfoRspDTO fullInfo(String vectorTaskCode) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb == null) {
            throw new BusinessException("实施方案编号不存在");
        }

        CerImplementationPlanFullInfoRspDTO result = new CerImplementationPlanFullInfoRspDTO();
        result.setPlanInfo(detailByCode(vectorTaskCode));
        result.setPlasmidPrimerList(buildPlasmidPrimerList(cerVectorTaskTb.getId()));
        result.setTransformSampleSeedList(buildTransformSampleSeedList(cerVectorTaskTb));
        return result;
    }

    @Override
    public void exportFullInfoExcel(String vectorTaskCode, HttpServletResponse response) {
        CerImplementationPlanFullInfoRspDTO fullInfo = fullInfo(vectorTaskCode);
        String fileName = "实施方案全量信息_" + vectorTaskCode + ".xlsx";
        SXSSFWorkbook workbook = new SXSSFWorkbook(-1);
        try {
            SXSSFSheet sheet = workbook.createSheet("实施方案全量信息");
            ExcelStyles styles = new ExcelStyles(workbook);
            int rowIndex = 0;
            rowIndex = writeReportTitle(sheet, styles, rowIndex, fullInfo.getPlanInfo());
            rowIndex = writePlanInfo(sheet, styles, rowIndex, fullInfo.getPlanInfo());
            rowIndex = writePlasmidPrimerInfo(sheet, styles, rowIndex, fullInfo.getPlasmidPrimerList());
            rowIndex = writeStatisticsInfo(sheet, styles, rowIndex, fullInfo.getTransformSampleSeedList());
            writeTransformSampleSeedInfo(sheet, styles, rowIndex, fullInfo.getTransformSampleSeedList());
            initFullInfoSheet(sheet);
            setFullInfoExcelColumnWidth(sheet);
            writeWorkbook(response, fileName, workbook);
        } finally {
            workbook.dispose();
        }
    }

    private List<CerImplementationPlanFullInfoRspDTO.PlasmidPrimerInfo> buildPlasmidPrimerList(Integer vectorTaskId) {
        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectAllByVectorTaskId(vectorTaskId);
        if (CollectionUtil.isEmpty(cerVectorTbList)) {
            return new ArrayList<>();
        }
        return cerVectorTbList.stream().map(cerVectorTb -> {
            CerImplementationPlanFullInfoRspDTO.PlasmidPrimerInfo item = new CerImplementationPlanFullInfoRspDTO.PlasmidPrimerInfo();
            item.setPlasmidName(cerVectorTb.getPlasmidName());
            item.setPrimer(cerVectorTb.getPlasmidSpecificPrimers());
            return item;
        }).collect(Collectors.toList());
    }

    private List<CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo> buildTransformSampleSeedList(CerVectorTaskTb cerVectorTaskTb) {
        List<CerTransformTb> transformList = cerTransformTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        List<BioSampleTestTb> sampleList = bioSampleTestTbMapper.selectAllByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        List<SeedStockTb> seedStockList = seedStockTbMapper.selectAllByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        Map<String, List<BioSampleTestTb>> sampleMap = groupSampleByTransformCode(sampleList);
        Map<String, List<SeedStockTb>> seedStockMap = groupSeedStockByPlantCode(seedStockList);
        Map<String, String> mutationTypeMap = getMutationTypeMap(sampleList);

        List<CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo> result = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(transformList)) {
            for (CerTransformTb transform : transformList) {
                List<BioSampleTestTb> currentSampleList = sampleMap.remove(transform.getTransformCode());
                if (CollectionUtil.isEmpty(currentSampleList)) {
                    continue;
                }
                for (BioSampleTestTb sample : currentSampleList) {
                    result.add(buildTransformSampleSeedInfo(transform.getTransformCode(), sample, seedStockMap.get(sample.getSampleCode()), mutationTypeMap));
                }
            }
        }
        if (!sampleMap.isEmpty()) {
            for (List<BioSampleTestTb> currentSampleList : sampleMap.values()) {
                for (BioSampleTestTb sample : currentSampleList) {
                    result.add(buildTransformSampleSeedInfo(sample.getTransformCode(), sample, seedStockMap.get(sample.getSampleCode()), mutationTypeMap));
                }
            }
        }
        return result.stream()
                .filter(this::showTransformSampleSeedInfo)
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(
                                item -> defaultString(item.getTransformCode()) + "|" + defaultString(item.getSampleCode()),
                                item -> item,
                                (first, second) -> first,
                                LinkedHashMap::new
                        ),
                        map -> new ArrayList<>(map.values())
                ))
                .stream()
                .collect(Collectors.toList());
    }

    private Map<String, String> getMutationTypeMap(List<BioSampleTestTb> sampleList) {
        if (CollectionUtil.isEmpty(sampleList)) {
            return Collections.emptyMap();
        }
        List<String> sampleCodeList = sampleList.stream()
                .map(BioSampleTestTb::getSampleCode)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.toList());
        if (CollectionUtil.isEmpty(sampleCodeList)) {
            return Collections.emptyMap();
        }
        List<BioSampleTestTwoResultDetailTb> detailList = bioSampleTestTwoResultDetailTbMapper.selectAllBySampleCodeIn(sampleCodeList);
        if (CollectionUtil.isEmpty(detailList)) {
            return Collections.emptyMap();
        }
        return detailList.stream()
                .filter(item -> StringUtils.isNotEmpty(item.getSampleCode()))
                .filter(item -> StringUtils.isNotEmpty(item.getVarType()))
                .collect(Collectors.groupingBy(
                        BioSampleTestTwoResultDetailTb::getSampleCode,
                        LinkedHashMap::new,
                        Collectors.collectingAndThen(
                                Collectors.mapping(BioSampleTestTwoResultDetailTb::getVarType, Collectors.toList()),
                                list -> list.stream().distinct().collect(Collectors.joining("、"))
                        )
                ));
    }

    private Map<String, List<BioSampleTestTb>> groupSampleByTransformCode(List<BioSampleTestTb> sampleList) {
        if (CollectionUtil.isEmpty(sampleList)) {
            return new LinkedHashMap<>();
        }
        return sampleList.stream().collect(Collectors.groupingBy(
                item -> StringUtils.isEmpty(item.getTransformCode()) ? "" : item.getTransformCode(),
                LinkedHashMap::new,
                Collectors.toList()
        ));
    }

    private Map<String, List<SeedStockTb>> groupSeedStockByPlantCode(List<SeedStockTb> seedStockList) {
        if (CollectionUtil.isEmpty(seedStockList)) {
            return new LinkedHashMap<>();
        }
        return seedStockList.stream()
                .filter(item -> StringUtils.isNotEmpty(item.getPlantCode()))
                .collect(Collectors.groupingBy(SeedStockTb::getPlantCode, LinkedHashMap::new, Collectors.toList()));
    }

    private CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo buildTransformSampleSeedInfo(
            String transformCode,
            BioSampleTestTb sample,
            List<SeedStockTb> seedStockList,
            Map<String, String> mutationTypeMap) {
        CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo item = new CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo();
        item.setTransformCode(transformCode);
        if (sample != null) {
            item.setSampleCode(sample.getSampleCode());
            item.setTestFlag(StringUtils.isNotEmpty(sample.getTestResult()) && !"noTest".equals(sample.getTestResult()) ? "是" : "否");
            item.setTestResult(testResultName(sample.getTestResult()));
            item.setCheckFlag(StringUtils.isNotEmpty(sample.getCheckResult()) && !"noCheck".equals(sample.getCheckResult()) ? "是" : "否");
            item.setCheckResult(checkResultName(sample.getCheckResult()));
            item.setMutationTypeSummary(mutationTypeMap.get(sample.getSampleCode()));
        }
        if (CollectionUtil.isNotEmpty(seedStockList)) {
            List<String> seedNumList = seedStockList.stream()
                    .map(SeedStockTb::getSeedNum)
                    .filter(StringUtils::isNotEmpty)
                    .distinct()
                    .collect(Collectors.toList());
            item.setHarvestFlag("是");
            item.setSeedNum(String.join("、", seedNumList));
            item.setGeneration(seedStockList.stream()
                    .map(SeedStockTb::getGeneration)
                    .filter(StringUtils::isNotEmpty)
                    .distinct()
                    .collect(Collectors.joining("、")));
            item.setSeedNumber(sumSeedTotalNumber(seedStockList));
            item.setStockOutNumber(sumSeedStockOutNumber(seedNumList));
            item.setUnit(findFirstUnit(seedStockList));
            fillFieldHarvestInfo(item, seedNumList);
        } else {
            item.setHarvestFlag("否");
        }
        return item;
    }

    private boolean showTransformSampleSeedInfo(CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo item) {
        if (item == null || StringUtils.isEmpty(item.getSampleCode())) {
            return false;
        }
        return !("舍弃".equals(item.getCheckResult()) && StringUtils.isEmpty(item.getSeedNum()));
    }

    private BigDecimal sumSeedTotalNumber(List<SeedStockTb> seedStockList) {
        if (CollectionUtil.isEmpty(seedStockList)) {
            return null;
        }
        return seedStockList.stream()
                .map(item -> item.getTotalNumber() == null ? item.getSeedNumber() : item.getTotalNumber())
                .filter(item -> item != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private BigDecimal sumSeedStockOutNumber(List<String> seedNumList) {
        if (CollectionUtil.isEmpty(seedNumList)) {
            return null;
        }
        BigDecimal result = BigDecimal.ZERO;
        for (String seedNum : seedNumList) {
            List<SeedStockOutLog> seedStockOutLogList = seedStockOutLogMapper.selectAllBySeedNum(seedNum);
            if (CollectionUtil.isEmpty(seedStockOutLogList)) {
                continue;
            }
            BigDecimal current = seedStockOutLogList.stream()
                    .map(SeedStockOutLog::getSeedNumber)
                    .filter(item -> item != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            result = result.add(current);
        }
        return result;
    }

    private String findFirstUnit(List<SeedStockTb> seedStockList) {
        if (CollectionUtil.isEmpty(seedStockList)) {
            return "";
        }
        return seedStockList.stream()
                .map(SeedStockTb::getUnit)
                .filter(StringUtils::isNotEmpty)
                .findFirst()
                .orElse("");
    }

    private void fillFieldHarvestInfo(CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo item, List<String> seedNumList) {
        if (CollectionUtil.isEmpty(seedNumList)) {
            return;
        }
        List<SeedStockTb> fieldHarvestSeedList = new ArrayList<>();
        boolean fieldPlantFlag = false;
        for (String seedNum : seedNumList) {
            if (CollectionUtil.isEmpty(tcExperimentDesignTbMapper.selectAllBySeedNum(seedNum))) {
                continue;
            }
            fieldPlantFlag = true;
            fieldHarvestSeedList.addAll(seedStockTbMapper.selectAllByMatherSeedNum(seedNum).stream()
                    .filter(this::isFieldHarvestSeed)
                    .collect(Collectors.toList()));
            fieldHarvestSeedList.addAll(seedStockTbMapper.selectAllByFatherSeedNum(seedNum).stream()
                    .filter(this::isFieldHarvestSeed)
                    .collect(Collectors.toList()));
        }
        item.setFieldPlantFlag(fieldPlantFlag ? "是" : "否");
        if (CollectionUtil.isEmpty(fieldHarvestSeedList)) {
            return;
        }
        List<SeedStockTb> distinctList = fieldHarvestSeedList.stream()
                .filter(item1 -> StringUtils.isNotEmpty(item1.getSeedNum()))
                .collect(Collectors.collectingAndThen(
                        Collectors.toMap(SeedStockTb::getSeedNum, item1 -> item1, (first, second) -> first, LinkedHashMap::new),
                        map -> new ArrayList<>(map.values())
                ));
        item.setFieldHarvestSeedNum(distinctList.stream().map(SeedStockTb::getSeedNum).collect(Collectors.joining("、")));
        item.setFieldHarvestSeedNumber(distinctList.stream()
                .map(seedStockTb -> formatNumber(seedStockTb.getTotalNumber() == null ? seedStockTb.getSeedNumber() : seedStockTb.getTotalNumber(), seedStockTb.getUnit()))
                .collect(Collectors.joining("、")));
        item.setFieldHarvestGeneration(distinctList.stream()
                .map(SeedStockTb::getGeneration)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .collect(Collectors.joining("、")));
    }

    private boolean isFieldHarvestSeed(SeedStockTb seedStockTb) {
        return seedStockTb != null && "4".equals(seedStockTb.getSourceType());
    }

    private int writeReportTitle(SXSSFSheet sheet, ExcelStyles styles, int rowIndex, CerImplementationPlanBaseInfoRspDTO planInfo) {
        Row titleRow = sheet.createRow(rowIndex);
        titleRow.setHeightInPoints(30);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("实施方案全量信息");
        titleCell.setCellStyle(styles.reportTitleStyle);
        for (int i = 1; i <= 13; i++) {
            titleRow.createCell(i).setCellStyle(styles.reportTitleStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 13));
        return rowIndex + 1;
    }

    private int writePlanInfo(SXSSFSheet sheet,
                              ExcelStyles styles,
                              int rowIndex,
                              CerImplementationPlanBaseInfoRspDTO planInfo) {
        rowIndex = writeSectionTitle(sheet, styles, rowIndex, "实施方案基础信息", 0, 5);
        String[][] rows = new String[][]{
                {"实施方案编号", planInfo.getVectorTaskCode(), "项目编号", planInfo.getProjectCode(), "项目名称", planInfo.getProjectName()},
                {"子项目编号", planInfo.getSubProjectCode(), "受体材料", planInfo.getAcceptorMaterial(), "递送方式", deliveryMethodName(planInfo.getDeliveryMethod())},
                {"物种", speciesName(planInfo.getSpeciesCode()), "品种", breedName(planInfo.getBreedCode())}
        };
        for (String[] rowData : rows) {
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(22);
            for (int i = 0; i < rowData.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(defaultString(rowData[i]));
                cell.setCellStyle(i % 2 == 0 ? styles.labelStyle : styles.bodyStyle);
            }
            if (rowData.length == 4) {
                for (int i = 4; i <= 5; i++) {
                    row.createCell(i).setCellStyle(styles.bodyStyle);
                }
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 3, 5));
            }
        }
        return rowIndex;
    }

    private int writeStatisticsInfo(SXSSFSheet sheet,
                                    ExcelStyles styles,
                                    int rowIndex,
                                    List<CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo> list) {
        rowIndex = writeSectionTitle(sheet, styles, rowIndex, "数据统计", 0, 5);
        int sampleCount = countDistinctValue(list, CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo::getSampleCode);
        int transformCount = countDistinctValue(list, CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo::getTransformCode);
        int seedCount = countDistinctSeedNum(list);
        int harvestCount = countDistinctSampleByPredicate(list, item -> "是".equals(item.getHarvestFlag()));
        int checkPassCount = countDistinctSampleByPredicate(list, item -> "保留".equals(item.getCheckResult()));
        int noCheckCount = countDistinctSampleByPredicate(list, item -> "未审核".equals(item.getCheckResult()));
        int haveTestResultCount = countDistinctSampleByPredicate(list, item -> "已检测且有结果".equals(item.getTestResult()));
        int noTestCount = countDistinctSampleByPredicate(list, item -> "未检测".equals(item.getTestResult()));
        int noTestResultCount = countDistinctSampleByPredicate(list, item -> "已检测但无结果".equals(item.getTestResult()));

        List<String[]> rowList = new ArrayList<>();
        rowList.add(new String[]{"转化统计", String.valueOf(transformCount), "取样统计", String.valueOf(sampleCount), "已检测有结果", String.valueOf(haveTestResultCount)});
        rowList.add(new String[]{"未检测", String.valueOf(noTestCount), "已检测未有结果", String.valueOf(noTestResultCount), "审核通过统计", String.valueOf(checkPassCount)});
        rowList.add(new String[]{"未审核统计", String.valueOf(noCheckCount), "收获统计", String.valueOf(harvestCount), "种子统计", String.valueOf(seedCount)});

        for (String[] rowData : rowList) {
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(22);
            for (int i = 0; i < rowData.length; i++) {
                Cell cell = row.createCell(i);
                cell.setCellValue(defaultString(rowData[i]));
                cell.setCellStyle(i % 2 == 0 ? styles.labelStyle : styles.bodyStyle);
            }
            if (rowData.length == 2) {
                for (int i = 2; i <= 5; i++) {
                    row.createCell(i).setCellStyle(styles.bodyStyle);
                }
                sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 1, 5));
            }
        }
        return rowIndex;
    }

    private int writePlasmidPrimerInfo(SXSSFSheet sheet,
                                       ExcelStyles styles,
                                       int rowIndex,
                                       List<CerImplementationPlanFullInfoRspDTO.PlasmidPrimerInfo> list) {
        rowIndex = writeSectionTitle(sheet, styles, rowIndex, "质粒/引物信息", 0, 5);
        rowIndex = writePlasmidPrimerHeader(sheet, styles, rowIndex);
        if (CollectionUtil.isEmpty(list)) {
            return writeEmptyRow(sheet, styles, rowIndex, 6);
        }
        for (CerImplementationPlanFullInfoRspDTO.PlasmidPrimerInfo item : list) {
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(24);
            writeCell(row, 0, item.getPlasmidName(), styles.plainBodyStyle);
            for (int i = 1; i <= 1; i++) {
                row.createCell(i).setCellStyle(styles.plainBodyStyle);
            }
            writeCell(row, 2, item.getPrimer(), styles.plainBodyStyle);
            for (int i = 3; i <= 5; i++) {
                row.createCell(i).setCellStyle(styles.plainBodyStyle);
            }
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 0, 1));
            sheet.addMergedRegion(new CellRangeAddress(row.getRowNum(), row.getRowNum(), 2, 5));
        }
        return rowIndex;
    }

    private int writeTransformSampleSeedInfo(SXSSFSheet sheet,
                                             ExcelStyles styles,
                                             int rowIndex,
                                             List<CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo> list) {
        rowIndex = writeSectionTitle(sheet, styles, rowIndex, "转化-取样-检测-审核-收获-种子库信息", 0, 13);
        rowIndex = writeHeader(sheet, styles, rowIndex,
                "转化编号", "取样编号/种植编号", "检测结果", "突变类型合计", "审核结果", "是否收获",
                "种子编号", "当前种子代次", "总数量", "出库数量", "大田是否种植", "大田收获种子编号", "大田收获数量", "大田收获代次");
        if (CollectionUtil.isEmpty(list)) {
            return writeEmptyRow(sheet, styles, rowIndex, 14);
        }
        int dataStartRow = rowIndex;
        for (CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo item : list) {
            Row row = sheet.createRow(rowIndex++);
            row.setHeightInPoints(24);
            writeCell(row, 0, item.getTransformCode(), styles.centerBodyStyle);
            writeCell(row, 1, item.getSampleCode(), styles.centerBodyStyle);
            writeCell(row, 2, item.getTestResult(), styles.bodyStyle);
            writeCell(row, 3, item.getMutationTypeSummary(), styles.bodyStyle);
            writeCell(row, 4, item.getCheckResult(), styles.bodyStyle);
            writeStatusCell(row, 5, item.getHarvestFlag(), styles);
            writeCell(row, 6, item.getSeedNum(), styles.bodyStyle);
            writeCell(row, 7, item.getGeneration(), styles.bodyStyle);
            writeCell(row, 8, formatNumber(item.getSeedNumber(), item.getUnit()), styles.bodyStyle);
            writeCell(row, 9, formatNumber(item.getStockOutNumber(), item.getUnit()), styles.bodyStyle);
            writeStatusCell(row, 10, item.getFieldPlantFlag(), styles);
            writeCell(row, 11, item.getFieldHarvestSeedNum(), styles.bodyStyle);
            writeCell(row, 12, item.getFieldHarvestSeedNumber(), styles.bodyStyle);
            writeCell(row, 13, item.getFieldHarvestGeneration(), styles.bodyStyle);
        }
        mergeSameValueCells(sheet, dataStartRow, rowIndex - 1, 0);
        return rowIndex;
    }

    private int countDistinctValue(List<CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo> list,
                                   java.util.function.Function<CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo, String> mapper) {
        if (CollectionUtil.isEmpty(list)) {
            return 0;
        }
        return (int) list.stream()
                .map(mapper)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .count();
    }

    private int countDistinctSampleByPredicate(List<CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo> list,
                                               java.util.function.Predicate<CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo> predicate) {
        if (CollectionUtil.isEmpty(list)) {
            return 0;
        }
        return (int) list.stream()
                .filter(predicate)
                .map(CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo::getSampleCode)
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .count();
    }

    private int countDistinctSeedNum(List<CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo> list) {
        if (CollectionUtil.isEmpty(list)) {
            return 0;
        }
        return (int) list.stream()
                .map(CerImplementationPlanFullInfoRspDTO.TransformSampleSeedInfo::getSeedNum)
                .filter(StringUtils::isNotEmpty)
                .flatMap(seedNum -> java.util.Arrays.stream(seedNum.split("、")))
                .filter(StringUtils::isNotEmpty)
                .distinct()
                .count();
    }

    private int writeSectionTitle(SXSSFSheet sheet, ExcelStyles styles, int rowIndex, String title, int firstCol, int lastCol) {
        Row row = sheet.createRow(rowIndex);
        row.setHeightInPoints(24);
        Cell cell = row.createCell(firstCol);
        cell.setCellValue(title);
        cell.setCellStyle(styles.titleStyle);
        for (int i = firstCol + 1; i <= lastCol; i++) {
            row.createCell(i).setCellStyle(styles.titleStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, firstCol, lastCol));
        return rowIndex + 1;
    }

    private int writeHeader(SXSSFSheet sheet, ExcelStyles styles, int rowIndex, String... headers) {
        Row row = sheet.createRow(rowIndex++);
        row.setHeightInPoints(24);
        for (int i = 0; i < headers.length; i++) {
            writeCell(row, i, headers[i], styles.headerStyle);
        }
        return rowIndex;
    }

    private int writePlasmidPrimerHeader(SXSSFSheet sheet, ExcelStyles styles, int rowIndex) {
        Row row = sheet.createRow(rowIndex);
        row.setHeightInPoints(24);
        writeCell(row, 0, "质粒/载体", styles.headerStyle);
        for (int i = 1; i <= 1; i++) {
            row.createCell(i).setCellStyle(styles.headerStyle);
        }
        writeCell(row, 2, "引物", styles.headerStyle);
        for (int i = 3; i <= 5; i++) {
            row.createCell(i).setCellStyle(styles.headerStyle);
        }
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 0, 1));
        sheet.addMergedRegion(new CellRangeAddress(rowIndex, rowIndex, 2, 5));
        return rowIndex + 1;
    }

    private int writeEmptyRow(SXSSFSheet sheet, ExcelStyles styles, int rowIndex, int columnCount) {
        Row row = sheet.createRow(rowIndex++);
        row.setHeightInPoints(24);
        for (int i = 0; i < columnCount; i++) {
            writeCell(row, i, i == 0 ? "暂无数据" : "", styles.emptyStyle);
        }
        return rowIndex;
    }

    private void writeCell(Row row, int columnIndex, String value, CellStyle style) {
        Cell cell = row.createCell(columnIndex);
        cell.setCellValue(defaultString(value));
        cell.setCellStyle(style);
    }

    private void writeStatusCell(Row row, int columnIndex, String value, ExcelStyles styles) {
        String text = defaultString(value);
        if ("是".equals(text)) {
            writeCell(row, columnIndex, text, styles.yesStyle);
        } else if ("否".equals(text)) {
            writeCell(row, columnIndex, text, styles.noStyle);
        } else {
            writeCell(row, columnIndex, text, styles.bodyStyle);
        }
    }

    private void mergeSameValueCells(SXSSFSheet sheet, int startRow, int endRow, int columnIndex) {
        if (endRow <= startRow) {
            return;
        }
        int mergeStart = startRow;
        String previousValue = getCellStringValue(sheet, startRow, columnIndex);
        for (int i = startRow + 1; i <= endRow; i++) {
            String currentValue = getCellStringValue(sheet, i, columnIndex);
            if (!StringUtils.equals(previousValue, currentValue)) {
                addMergedRegionIfNeeded(sheet, mergeStart, i - 1, columnIndex, previousValue);
                mergeStart = i;
                previousValue = currentValue;
            }
        }
        addMergedRegionIfNeeded(sheet, mergeStart, endRow, columnIndex, previousValue);
    }

    private void addMergedRegionIfNeeded(SXSSFSheet sheet, int startRow, int endRow, int columnIndex, String value) {
        if (endRow > startRow && StringUtils.isNotEmpty(value)) {
            sheet.addMergedRegion(new CellRangeAddress(startRow, endRow, columnIndex, columnIndex));
        }
    }

    private String getCellStringValue(SXSSFSheet sheet, int rowIndex, int columnIndex) {
        Row row = sheet.getRow(rowIndex);
        if (row == null || row.getCell(columnIndex) == null) {
            return "";
        }
        return row.getCell(columnIndex).getStringCellValue();
    }

    private void initFullInfoSheet(SXSSFSheet sheet) {
        sheet.setDisplayGridlines(false);
        sheet.createFreezePane(0, 2);
        sheet.setFitToPage(true);
        sheet.getPrintSetup().setLandscape(true);
        sheet.getPrintSetup().setFitWidth((short) 1);
        sheet.getPrintSetup().setFitHeight((short) 0);
        sheet.setMargin(SXSSFSheet.TopMargin, 0.5);
        sheet.setMargin(SXSSFSheet.BottomMargin, 0.5);
        sheet.setMargin(SXSSFSheet.LeftMargin, 0.3);
        sheet.setMargin(SXSSFSheet.RightMargin, 0.3);
    }

    private void setFullInfoExcelColumnWidth(SXSSFSheet sheet) {
        int[] widths = new int[]{18, 32, 18, 34, 18, 34, 26, 16, 18, 16, 16, 28, 22, 18};
        for (int i = 0; i < widths.length; i++) {
            sheet.setColumnWidth(i, widths[i] * 256);
        }
    }

    private void writeWorkbook(HttpServletResponse response, String fileName, SXSSFWorkbook workbook) {
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            ServletOutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            outputStream.flush();
        } catch (IOException e) {
            throw new BusinessException("实施方案全量信息导出失败");
        }
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }

    private String formatNumber(BigDecimal number, String unit) {
        if (number == null) {
            return "";
        }
        return number.stripTrailingZeros().toPlainString() + defaultString(unit);
    }

    private String breedName(String breedCode) {
        if (StringUtils.isEmpty(breedCode)) {
            return "";
        }
        CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(breedCode);
        return cerBreedDict == null ? breedCode : cerBreedDict.getBreedName();
    }

    private String speciesName(String speciesCode) {
        if (StringUtils.isEmpty(speciesCode)) {
            return "";
        }
        CerSpeciesConf cerSpeciesConf = cerSpeciesConfMapper.selectOneBySpeciesCode(speciesCode);
        return cerSpeciesConf == null ? speciesCode : cerSpeciesConf.getSpeciesName();
    }

    private String deliveryMethodName(String code) {
        if ("A".equals(code)) {
            return "农杆菌转化";
        }
        if ("B".equals(code)) {
            return "基因枪";
        }
        if ("P".equals(code)) {
            return "原生质体转化";
        }
        if ("V".equals(code)) {
            return "病毒载体";
        }
        return defaultString(code);
    }

    private String testResultName(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        if ("noTest".equals(code)) {
            return "未检测";
        }
        if ("noResult".equals(code)) {
            return "已检测但无结果";
        }
        if ("haveResult".equals(code)) {
            return "已检测且有结果";
        }
        return code;
    }

    private String checkResultName(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        if ("stay".equals(code)) {
            return "保留";
        }
        if ("remove".equals(code)) {
            return "舍弃";
        }
        if ("noCheck".equals(code)) {
            return "未审核";
        }
        return code;
    }

    private static class ExcelStyles {
        private final CellStyle reportTitleStyle;
        private final CellStyle subtitleStyle;
        private final CellStyle titleStyle;
        private final CellStyle headerStyle;
        private final CellStyle labelStyle;
        private final CellStyle bodyStyle;
        private final CellStyle plainBodyStyle;
        private final CellStyle centerBodyStyle;
        private final CellStyle emptyStyle;
        private final CellStyle yesStyle;
        private final CellStyle noStyle;

        private ExcelStyles(SXSSFWorkbook workbook) {
            this.reportTitleStyle = buildStyle(workbook, true, (short) 16, IndexedColors.DARK_TEAL.getIndex(), IndexedColors.WHITE.getIndex(), HorizontalAlignment.CENTER);
            this.subtitleStyle = buildStyle(workbook, false, (short) 10, IndexedColors.WHITE.getIndex(), IndexedColors.GREY_80_PERCENT.getIndex(), HorizontalAlignment.RIGHT);
            this.titleStyle = buildStyle(workbook, true, (short) 11, IndexedColors.GREY_25_PERCENT.getIndex(), IndexedColors.BLACK.getIndex(), HorizontalAlignment.LEFT);
            this.headerStyle = buildStyle(workbook, true, (short) 10, IndexedColors.GREY_25_PERCENT.getIndex(), IndexedColors.BLACK.getIndex(), HorizontalAlignment.CENTER);
            this.labelStyle = buildStyle(workbook, true, (short) 10, IndexedColors.GREY_25_PERCENT.getIndex(), IndexedColors.BLACK.getIndex(), HorizontalAlignment.LEFT);
            this.bodyStyle = buildStyle(workbook, false, (short) 10, IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(), HorizontalAlignment.LEFT);
            this.plainBodyStyle = buildStyle(workbook, false, (short) 10, IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(), HorizontalAlignment.LEFT);
            this.centerBodyStyle = buildStyle(workbook, false, (short) 10, IndexedColors.WHITE.getIndex(), IndexedColors.BLACK.getIndex(), HorizontalAlignment.CENTER);
            this.emptyStyle = buildStyle(workbook, false, (short) 10, IndexedColors.WHITE.getIndex(), IndexedColors.GREY_50_PERCENT.getIndex(), HorizontalAlignment.CENTER);
            this.yesStyle = buildStyle(workbook, true, (short) 10, IndexedColors.LIGHT_GREEN.getIndex(), IndexedColors.GREEN.getIndex(), HorizontalAlignment.CENTER);
            this.noStyle = buildStyle(workbook, false, (short) 10, IndexedColors.GREY_25_PERCENT.getIndex(), IndexedColors.GREY_80_PERCENT.getIndex(), HorizontalAlignment.CENTER);
        }

        private static CellStyle buildStyle(SXSSFWorkbook workbook, boolean bold, short fontSize, short fillColor, short fontColor, HorizontalAlignment alignment) {
            CellStyle style = workbook.createCellStyle();
            style.setAlignment(alignment);
            style.setVerticalAlignment(VerticalAlignment.CENTER);
            style.setWrapText(true);
            style.setBorderTop(BorderStyle.THIN);
            style.setBorderRight(BorderStyle.THIN);
            style.setBorderBottom(BorderStyle.THIN);
            style.setBorderLeft(BorderStyle.THIN);
            style.setFillForegroundColor(fillColor);
            style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            Font font = workbook.createFont();
            font.setBold(bold);
            font.setFontHeightInPoints(fontSize);
            font.setColor(fontColor);
            style.setFont(font);
            return style;
        }
    }

    @Override
    public void stop(Integer id) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(id);
        if (!VectorTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
            throw new BusinessException("只有执行中实施方案可以暂停");
        }
        cerVectorTaskTb.setTaskStatus(VectorTaskStatusEnum.TASK_STATUS_4.status);
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
    }

    @Override
    public void start(Integer id) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(id);
        if (!VectorTaskStatusEnum.TASK_STATUS_4.status.equals(cerVectorTaskTb.getTaskStatus())) {
            throw new BusinessException("只有暂停实施方案可以再次启动");
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(cerVectorTaskTb.getProjectId());
        if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
            throw new BusinessException("该项目不是执行中");
        }
        cerVectorTaskTb.setTaskStatus(VectorTaskStatusEnum.TASK_STATUS_2.status);
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
    }

    @Override
    public void complete(Integer id) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(id);
        cerVectorTaskTb.setTaskStatus(VectorTaskStatusEnum.TASK_STATUS_5.status);
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
    }

    @Override
    public String getInstantVerifyTaskCode(String vectorTaskCode) {
        List<CerInstantVerifyTaskTb> cerInstantVerifyTaskTbList = cerInstantVerifyTaskTbMapper.selectAllByVectorTaskCode(vectorTaskCode);
        if (CollectionUtil.isEmpty(cerInstantVerifyTaskTbList)) {
            return vectorTaskCode + "-A";
        } else if (cerInstantVerifyTaskTbList.size() == 1) {
            String[] strArr = cerInstantVerifyTaskTbList.get(0).getInstantVerifyCode().split("-");
            String lastCode = strArr[strArr.length - 1];
            return vectorTaskCode + "-" + LetterUtil.nextLetterForInstantVerify(lastCode);
        } else {
            List<String> lastCodeList = cerInstantVerifyTaskTbList.stream().sorted(Comparator.comparing(CerInstantVerifyTaskTb::getId).reversed()).map(cerInstantVerifyTaskTb -> cerInstantVerifyTaskTb.getInstantVerifyCode().split("-")[cerInstantVerifyTaskTb.getInstantVerifyCode().split("-").length - 1]).collect(Collectors.toList());
            return vectorTaskCode + "-" + LetterUtil.nextLetterForInstantVerify(lastCodeList.get(0));
        }
    }

    @Override
    public List<VectorTaskSpeciesRspDTO> findAllSpecies() {
        List<VectorTaskSpeciesRspDTO> result = new ArrayList<>();
        List<String> allSpeciesCodeList = cerVectorTaskTbMapper.selectAllSpeciesCode();
        if (CollectionUtil.isNotEmpty(allSpeciesCodeList)) {
            List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAllBySpeciesCodeIn(allSpeciesCodeList);
            cerSpeciesConfList.forEach(cerSpeciesConf -> {
                VectorTaskSpeciesRspDTO vectorTaskSpeciesRspDTO = new VectorTaskSpeciesRspDTO();
                vectorTaskSpeciesRspDTO.setSpeciesCode(cerSpeciesConf.getSpeciesCode());
                vectorTaskSpeciesRspDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
                result.add(vectorTaskSpeciesRspDTO);
            });
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Integer id) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(id);
        if (cerVectorTaskTb == null) {
            throw new BusinessException("参数异常，实施方案找不到");
        }
        if (SecurityContextHolder.getUserId().intValue() != cerVectorTaskTb.getCreateUserId()) {
            throw new BusinessException("只有项目负责人可以删除");
        }
        if (StringUtils.isNotEmpty(cerVectorTaskTb.getCurrentStepCode())) {
            throw new BusinessException("该实施方案已有后续步骤进行，无法删除");
        }
        cerVectorTaskTbMapper.deleteById(id);
        bioSampleCodePrefixTbMapper.deleteByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        bioTaskDtlTbMapper.deleteByTaskNum(cerVectorTaskTb.getTaskNum());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyVectorTaskCode(VectorTaskModifyVectorTaskCodeReqDTO vectorTaskModifyVectorTaskCodeReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(vectorTaskModifyVectorTaskCodeReqDTO.getId());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("参数异常，实施方案找不到");
        }
        if (!vectorTaskModifyVectorTaskCodeReqDTO.getVectorTaskCode().startsWith(cerVectorTaskTb.getSubProjectCode())) {
            throw new BusinessException("实施方案编号必须包含子项目");
        }
        if (SecurityContextHolder.getUserId().intValue() != cerVectorTaskTb.getCreateUserId()) {
            throw new BusinessException("只有项目负责人可以删除");
        }
        if (StringUtils.isNotEmpty(cerVectorTaskTb.getCurrentStepCode())) {
            throw new BusinessException("该实施方案已有后续步骤进行，无法删除");
        }
        if (cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskModifyVectorTaskCodeReqDTO.getVectorTaskCode()) != null) {
            throw new BusinessException("实施方案编号系统中已经存在，不能改成重复编号");
        }
        if (!vectorTaskModifyVectorTaskCodeReqDTO.getVectorTaskCode().matches("^[0-9a-zA-Z]{1,8}\\-[0-9]{2}[a-z]$") && !vectorTaskModifyVectorTaskCodeReqDTO.getVectorTaskCode().matches("^[0-9a-zA-Z]{1,8}\\-[0-9]{2}$")) {
            throw new BusinessException("实施方案编号格式不正确");
        }

        //必须先更新
        BioSampleCodePrefixTb bioSampleCodePrefixTb = bioSampleCodePrefixTbMapper.selectOneByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        if (bioSampleCodePrefixTb == null) {
            throw new BusinessException("数据异常，找不到该实施方案的取样编号前缀记录信息");
        }
        bioSampleCodePrefixTb.setVectorTaskCode(vectorTaskModifyVectorTaskCodeReqDTO.getVectorTaskCode());
        bioSampleCodePrefixTbMapper.updateById(bioSampleCodePrefixTb);

        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerVectorTaskTb.getTaskNum());
        if (bioTaskDtlTb == null) {
            throw new BusinessException("数据异常，找不到该实施方案的发起工单");
        }

        cerVectorTaskTb.setVectorTaskCode(vectorTaskModifyVectorTaskCodeReqDTO.getVectorTaskCode());
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);


        ImplementPlanAddDTO implementPlanAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ImplementPlanAddDTO.class);
        implementPlanAddDTO.setVectorTaskCode(vectorTaskModifyVectorTaskCodeReqDTO.getVectorTaskCode());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(implementPlanAddDTO));
        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
    }


    private String getVectorTaskCode(String subProjectNum, Integer currentNum) {
        return subProjectNum + "-" + StringUtils.padl(String.valueOf(currentNum + 1), 2, '0');
    }


}
