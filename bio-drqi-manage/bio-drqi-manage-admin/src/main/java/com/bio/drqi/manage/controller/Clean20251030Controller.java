package com.bio.drqi.manage.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.CheckResultEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.SampleApplyTypeEnum;
import com.bio.drqi.enums.VectorTaskStatusEnum;
import com.bio.drqi.manage.dto.project.*;
import com.bio.drqi.mapper.*;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test2")
public class Clean20251030Controller {

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;


    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;


    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    @Resource
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;

    @Resource
    private CerVectorStepLogMapper cerVectorStepLogMapper;

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private SeedStockDestructionLogMapper seedStockDestructionLogMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private BmsProductStockOutLogMapper bmsProductStockOutLogMapper;

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private BmsMoveOrderDetailTbMapper bmsMoveOrderDetailTbMapper;

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;


    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;


    @Resource
    private BmsProductTbMapper bmsProductTbMapper;


    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;


    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;


    @Resource
    private CerSampleCodePrefixTbMapper cerSampleCodePrefixTbMapper;

    @Resource
    private CerSampleApplyTbMapper cerSampleApplyTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleSampleTwoResultDetailTbMapper;




    @GetMapping("createExcel")
    public void createExcel(HttpServletResponse httpServletResponse) {
        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectSelective(null);
        List<Vector> vectorList = new ArrayList<>();
        for (CerVectorTb cerVectorTb : cerVectorTbList) {
            log.info("cerVectorTb=" + JSONUtil.toJsonStr(cerVectorTb));
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cerVectorTb.getVectorTaskCode());
            if (cerVectorTaskTb == null) {
                throw new BusinessException("找不到实施方案信息");
            }
            CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerVectorTaskTb.getProjectCode());

            List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectAllByVectorTaskCodeAndPlasmidName(cerVectorTaskTb.getVectorTaskCode(), cerVectorTb.getPlasmidName());
            Vector vector = new Vector();
            vector.setProjectCode(cerVectorTaskTb.getProjectCode());
            vector.setProjectType("1".equals(cerProjectTb.getProjectType())?"正常项目":"自研项目");
            vector.setSubProjectCode(cerVectorTaskTb.getSubProjectCode());
            vector.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
            vector.setPlasmidName(cerVectorTb.getPlasmidName());
            vector.setBacterialResistance(cerVectorTb.getBacterialResistance());
            vector.setPlasmidSpecificPrimers(cerVectorTb.getPlasmidSpecificPrimers());
            vector.setCopyNumber(cerVectorTb.getCopyNumber());
            vector.setSelectionMarker(cerVectorTb.getSelectionMarker());
            vector.setVectorTaskStatus(VectorTaskStatusEnum.getNameByStatus(cerVectorTaskTb.getTaskStatus()));
            if (CollectionUtil.isNotEmpty(cerPlasmidQualityTbList)) {
                if (cerPlasmidQualityTbList.size() == 1) {
                    vector.setAgrobacteriumInformation(cerPlasmidQualityTbList.get(0).getAgrobacteriumInformation());
                    vector.setAgrobacteriumResistance(cerPlasmidQualityTbList.get(0).getAgrobacteriumResistance());
                    vector.setPlasmidConcentration(cerPlasmidQualityTbList.get(0).getPlasmidConcentration());
                    vector.setExtractionKit(cerPlasmidQualityTbList.get(0).getExtractionKit());
                } else {
                    for (CerPlasmidQualityTb cerPlasmidQualityTb : cerPlasmidQualityTbList) {
                        if (StringUtils.isNotEmpty(cerPlasmidQualityTb.getAgrobacteriumInformation())) {
                            vector.setAgrobacteriumInformation(cerPlasmidQualityTb.getAgrobacteriumInformation());
                        }
                        if (StringUtils.isNotEmpty(cerPlasmidQualityTb.getAgrobacteriumResistance())) {
                            vector.setAgrobacteriumResistance(cerPlasmidQualityTb.getAgrobacteriumResistance());
                        }
                        if (StringUtils.isNotEmpty(cerPlasmidQualityTb.getPlasmidConcentration())) {
                            vector.setPlasmidConcentration(cerPlasmidQualityTb.getPlasmidConcentration());
                        }
                        if (StringUtils.isNotEmpty(cerPlasmidQualityTb.getExtractionKit())) {
                            vector.setExtractionKit(cerPlasmidQualityTb.getExtractionKit());
                        }
                    }
                }
            } else {
                cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
                if (CollectionUtil.isNotEmpty(cerPlasmidQualityTbList)) {
                    for (CerPlasmidQualityTb cerPlasmidQualityTb : cerPlasmidQualityTbList) {
                        if (StringUtils.isNotEmpty(cerPlasmidQualityTb.getAgrobacteriumInformation())) {
                            vector.setAgrobacteriumInformation(cerPlasmidQualityTb.getAgrobacteriumInformation());
                        }
                        if (StringUtils.isNotEmpty(cerPlasmidQualityTb.getAgrobacteriumResistance())) {
                            vector.setAgrobacteriumResistance(cerPlasmidQualityTb.getAgrobacteriumResistance());
                        }
                        if (StringUtils.isNotEmpty(cerPlasmidQualityTb.getPlasmidConcentration())) {
                            vector.setPlasmidConcentration(cerPlasmidQualityTb.getPlasmidConcentration());
                        }
                        if (StringUtils.isNotEmpty(cerPlasmidQualityTb.getExtractionKit())) {
                            vector.setExtractionKit(cerPlasmidQualityTb.getExtractionKit());
                        }
                    }
                }
            }
            vectorList.add(vector);
        }

        ExcelUtil.writeExcel("质粒信息.xlsx", "sheet1", vectorList, Vector.class, httpServletResponse);
    }

    @Data
    public static class Vector {

        @ExcelProperty("项目编号")
        private String projectCode;

        @ExcelProperty("项目类型")
        private String projectType;

        @ExcelProperty("子项目编号")
        private String subProjectCode;

        @ExcelProperty("实施方案编号")
        private String vectorTaskCode;


        @ExcelProperty("实施方案状态")
        private String vectorTaskStatus;

        @ExcelProperty("质粒名称")
        private String plasmidName;
        /**
         * 细菌抗性
         */
        @ExcelProperty("细菌抗性")
        private String bacterialResistance;

        /**
         * 质粒特异性引物
         */
        @ExcelProperty("质粒特异性引物")
        private String plasmidSpecificPrimers;
        /**
         * 拷贝数
         */
        @ExcelProperty("拷贝数")
        private String copyNumber;
        /**
         * 植物筛选标记
         */
        @ExcelProperty("植物筛选标记")
        private String selectionMarker;

        /**
         * 质检农杆菌信息
         */
        @ExcelProperty("质检农杆菌信息")
        private String agrobacteriumInformation;

        /**
         * 农杆菌抗性
         */
        @ExcelProperty("质检农杆菌抗性")
        private String agrobacteriumResistance;

        /**
         * 质粒浓度
         */
        @ExcelProperty("质检质粒浓度")
        private String plasmidConcentration;

        /**
         * 提取试剂盒
         */
        @ExcelProperty("质检提取试剂盒")
        private String extractionKit;


    }

    @GetMapping("/cleanSampleTestUserId20251030")
    public ResponseResult<String> cleanSampleTestUserId() {
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectSelective(null);
        for (CerSampleTestTb cerSampleTestTb : cerSampleTestTbList) {
            log.info("cerSampleTestTb=" + JSONUtil.toJsonStr(cerSampleTestTb));
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerSampleTestTb.getApplyNo());
            if (bioTaskDtlTb == null) {
                throw new BusinessException("找不到申请信息");
            }
            if (bioTaskDtlTb.getTaskStatus().equals(BioTaskStatusEnum.TASK_STATUS_2.status)) {
                if (cerSampleTestTb.getTestUserId() == null) {
                    cerSampleTestTb.setTestUserId(117);
                    cerSampleTestTb.setTestUserName("张立肖");
                    cerSampleTestTbMapper.updateById(cerSampleTestTb);
                }
            } else if (!CheckResultEnum.noCheck.name().equals(cerSampleTestTb.getCheckResult())) {
                if (cerSampleTestTb.getTestUserId() == null) {
                    cerSampleTestTb.setTestUserId(117);
                    cerSampleTestTb.setTestUserName("张立肖");
                    cerSampleTestTbMapper.updateById(cerSampleTestTb);
                }
            } else if (CollectionUtil.isNotEmpty(bioSampleSampleTwoResultDetailTbMapper.selectAllByApplyNoAndSampleCode(cerSampleTestTb.getApplyNo(), cerSampleTestTb.getSampleCode()))) {
                if (cerSampleTestTb.getTestUserId() == null) {
                    cerSampleTestTb.setTestUserId(117);
                    cerSampleTestTb.setTestUserName("张立肖");
                    cerSampleTestTbMapper.updateById(cerSampleTestTb);
                }
            }
        }
        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("/cleanSampleApply20251030")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSampleApply() {
        List<CerSampleApplyTb> cerSampleApplyTbList = cerSampleApplyTbMapper.selectSelective(null);
        for (CerSampleApplyTb cerSampleApplyTb : cerSampleApplyTbList) {
            log.info("cerSampleApplyTb={}" + JSONUtil.toJsonStr(cerSampleApplyTb));
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerSampleApplyTb.getApplyNo());
            if (bioTaskDtlTb == null) {
                throw new BusinessException("找不到取样检测信息");
            }
            NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
            cerSampleApplyTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
            cerSampleApplyTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
            cerSampleApplyTb.setApplyDesc(bioTaskDtlTb.getTaskDesc());
            cerSampleApplyTb.setApplyType(CollectionUtil.isNotEmpty(newSampleTestDTO.getRepeatSampleApplyList()) ? SampleApplyTypeEnum.R.name() : SampleApplyTypeEnum.F.name());
            cerSampleApplyTb.setIdentifyExcelUrl(newSampleTestDTO.getIdentifyPrimerTemplateExcelUrl());
            cerSampleApplyTb.setOneTestExcelUrl(newSampleTestDTO.getTestDataExcelUrl());
            cerSampleApplyTb.setNgsExcelUrl(newSampleTestDTO.getBioInfoResultExcelUrl());
            cerSampleApplyTb.setCloneFlag(newSampleTestDTO.isCloneFlag() ? BioDrQiContents.Y : BioDrQiContents.N);
            cerSampleApplyTb.setLayoutFlag(newSampleTestDTO.getTestType());

            //如果是首次申请，更新申请中包含的事实方案和取样编号范围
            List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
            if (CollectionUtil.isEmpty(cerSampleTestTbList)) {
                continue;
            }
            Map<String, List<CerSampleTestTb>> cerSampleTestTbListMap = cerSampleTestTbList.stream().collect(Collectors.groupingBy(CerSampleTestTb::getVectorTaskCode));
            cerSampleApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(cerSampleTestTbListMap.keySet()).replace("[", "").replace("]", "").replace("\"", ""));
            StringBuffer sampleCodeRangeBuff = new StringBuffer();
            if (SampleApplyTypeEnum.F.name().equals(cerSampleApplyTb.getApplyType())) {
                cerSampleTestTbListMap.forEach((vectorTaskCode, sampleTestList) -> {
                    CerSampleCodePrefixTb cerSampleCodePrefixTb = cerSampleCodePrefixTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
                    sampleTestList = sampleTestList.stream().filter(sampleTest -> sampleTest.getSampleCode().startsWith(cerSampleCodePrefixTb.getSampleCodePrefix())).sorted(Comparator.comparing(sampleTest -> Integer.valueOf(sampleTest.getSampleCode().substring(2)))).collect(Collectors.toList());
                    if (CollectionUtil.isNotEmpty(sampleTestList)) {
                        sampleCodeRangeBuff.append(sampleTestList.get(0).getSampleCode() + "-" + sampleTestList.get(sampleTestList.size() - 1).getSampleCode()).append(",");
                    }
                });
                if (StringUtils.isNotEmpty(sampleCodeRangeBuff.toString())) {
                    cerSampleApplyTb.setSampleCodeRange(sampleCodeRangeBuff.substring(0, sampleCodeRangeBuff.length() - 1));
                }
            }

            cerSampleApplyTbMapper.updateById(cerSampleApplyTb);
        }
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 修复种植信息导入缺失一条日期问题
     *
     * @return
     */
    @GetMapping("/cleanPlantDtl20251030")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlantDtl20251030() {
        List<PlantDTlExcel> plantDTlExcelList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\小麦EW00101-01a、02a.xlsx", PlantDTlExcel.class);
        for (PlantDTlExcel plantDTlExcel : plantDTlExcelList) {
            log.info("plantDTlExcel=" + JSONUtil.toJsonStr(plantDTlExcel));
            CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCode(plantDTlExcel.plantNum);
            if (cerPlantDtlTb == null) {
                throw new BusinessException("找不到种植编号");
            }
            cerPlantDtlTb.setHarvestDate(plantDTlExcel.harvestDate);
            cerPlantDtlTbMapper.updateById(cerPlantDtlTb);
        }
        return ResponseResult.getSuccess("ok");
    }

    @Data
    public static class PlantDTlExcel {

        @ExcelProperty("种植编号")
        private String plantNum;

        @ExcelProperty("收获日期")
        private String harvestDate;

    }
}
