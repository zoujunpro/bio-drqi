package com.bio.drqi.manage.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.flowtask.plant.PlantSampleTestTaskService;
import com.bio.drqi.manage.sample.req.ApproveSampleResultReqDTO;
import com.bio.drqi.manage.service.bio.BioSampleTestService;
import com.bio.drqi.mapper.*;
import com.bio.print.PlantApplyPrintDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.Jar;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/test1")
@Slf4j
public class CleanTestController {

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private BioSampleTestService bioSampleTestService;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private PlantSampleTestTaskService plantSampleTestTaskService;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private BioPrintLabelInfoTbMapper bioPrintLabelInfoTbMapper;

    @Resource
    private PlantApplyDetailTbMapper plantApplyDetailTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @GetMapping("/cleanPrintPlant_apply_label_print")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPrintPlant_apply_label_print() {
        List<BioPrintLabelInfoTb> plant_label_cer_printList = bioPrintLabelInfoTbMapper.searchAllByLabelType("plant_apply_label_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : plant_label_cer_printList) {
            String[] uniqueCodeArr = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            PlantApplyDetailTb plantApplyDetailTb = plantApplyDetailTbMapper.selectOneByRegionNumAndSeedNum(uniqueCodeArr[0], uniqueCodeArr[1]);
            PlantApplyPrintDTO plantApplyPrintDTO = new PlantApplyPrintDTO();
            plantApplyPrintDTO.setRegionNum(plantApplyDetailTb.getRegionNum());
            CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(plantApplyDetailTb.getBreedCode());
            plantApplyPrintDTO.setSeedNum(plantApplyDetailTb.getSeedNum());
            plantApplyPrintDTO.setBreedName(cerBreedDict.getBreedName());
            plantApplyPrintDTO.setTaskNum(plantApplyDetailTb.getPlantApplyNum());
            plantApplyPrintDTO.setPrintNumber(1);
            bioPrintLabelInfoTb.setLabelText(JSONUtil.toJsonStr(plantApplyPrintDTO));
            bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);



        }


        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanPrint")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPrint() {
        List<BioPrintLabelInfoTb> plant_label_cer_printList = bioPrintLabelInfoTbMapper.searchAllByLabelType("plant_label_cer_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : plant_label_cer_printList) {
            String[] str = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            if (str.length == 3) {
                bioPrintLabelInfoTb.setUniqueCode(str[2]);
                bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
            }
        }
        List<BioPrintLabelInfoTb> plant_label_project_printList = bioPrintLabelInfoTbMapper.searchAllByLabelType("plant_label_project_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : plant_label_project_printList) {
            String[] str = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            if (str.length == 2) {
                bioPrintLabelInfoTb.setUniqueCode(str[1]);
                bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
            }
        }
        List<BioPrintLabelInfoTb> sample_label_large_project_printList = bioPrintLabelInfoTbMapper.searchAllByLabelType("sample_label_large_project_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : sample_label_large_project_printList) {
            String[] str = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            if (str.length == 3) {
                bioPrintLabelInfoTb.setUniqueCode(str[0] + "|" + str[2]);
                bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
            }
        }

        List<BioPrintLabelInfoTb> sample_label_small_project_printList = bioPrintLabelInfoTbMapper.searchAllByLabelType("sample_label_small_project_print");
        for (BioPrintLabelInfoTb bioPrintLabelInfoTb : sample_label_small_project_printList) {
            String[] str = bioPrintLabelInfoTb.getUniqueCode().split("\\|");
            if (str.length == 3) {
                bioPrintLabelInfoTb.setUniqueCode(str[0] + "|" + str[2]);
                bioPrintLabelInfoTbMapper.updateById(bioPrintLabelInfoTb);
            }
        }


        return null;
    }

    @GetMapping("/checkSample")
    public ResponseResult<String> checkSample() {
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo("C0005286");
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum("C0005286");

        ApproveSampleResultReqDTO approveSampleResultReqDTO = new ApproveSampleResultReqDTO();
        approveSampleResultReqDTO.setTaskNum("C0005286");
        approveSampleResultReqDTO.setContentList(bioSampleTestTbList.stream().map(bioSampleTestTb -> new ApproveSampleResultReqDTO.Content(bioSampleTestTb.getSampleCode(), "stay")).collect(Collectors.toList()));
        bioSampleTestService.approveSampleResult(approveSampleResultReqDTO);
        plantSampleTestTaskService.executeTask(bioTaskDtlTb);
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanPlasmid")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlasmid() {

        List<Plasmid> plasmidList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\质粒信息.xlsx", Plasmid.class);
        for (Plasmid plasmid : plasmidList) {
            log.info("plasmid=" + JSONUtil.toJsonStr(plasmid));
            if (StringUtils.isEmpty(plasmid.plasmidName)) {
                continue;
            }
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(plasmid.vectorTaskCode);

            if (cerVectorTaskTb == null) {
                throw new BusinessException("数据异常，找不到实施方案");
            }
            CerVectorTb cerVectorTb = cerVectorTbMapper.selectOneByPlasmidNameAndVectorTaskId(plasmid.plasmidName, cerVectorTaskTb.getId());
            if (cerVectorTb == null) {
                throw new BusinessException("找不到质粒信息");
            }
            cerVectorTb.setBacterialResistance(plasmid.bacterialResistance);
            cerVectorTb.setPlasmidSpecificPrimers(plasmid.plasmidSpecificPrimers);
            cerVectorTb.setCopyNumber(plasmid.copyNumber);
            cerVectorTb.setSelectionMarker(plasmid.selectionMarker);
            cerVectorTbMapper.updateById(cerVectorTb);
        }

        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanPlasmidCheck")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlasmidCheck() {

        List<Plasmid> plasmidList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\质粒信息.xlsx", Plasmid.class);
        Map<String, List<Plasmid>> map = plasmidList.stream().filter(plasmid -> StringUtils.isNotEmpty(plasmid.plasmidName)).collect(Collectors.groupingBy(Plasmid::getVectorTaskCode));
        map.forEach((vectorTaskCode, plasmids) -> {
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
            List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
            cerPlasmidQualityTbMapper.deleteByVectorTaskId(cerVectorTaskTb.getId());
            if (CollectionUtil.isEmpty(cerPlasmidQualityTbList)) {
                throw new BusinessException("找不到质检信息");
            }
            for (Plasmid plasmid : plasmids) {
                CerPlasmidQualityTb cerPlasmidQualityTb = new CerPlasmidQualityTb();
                cerPlasmidQualityTb.setSubProjectId(cerVectorTaskTb.getSubProjectId());
                cerPlasmidQualityTb.setProjectId(cerVectorTaskTb.getProjectId());
                cerPlasmidQualityTb.setVectorTaskId(cerVectorTaskTb.getId());
                cerPlasmidQualityTb.setPlasmidName(plasmid.getPlasmidName());
                cerPlasmidQualityTb.setQualityInspectionNumber(plasmid.getPlasmidName());
                cerPlasmidQualityTb.setQualityInspectionResult("pass");
                cerPlasmidQualityTb.setAgrobacteriumInformation(plasmid.getAgrobacteriumInformation());
                cerPlasmidQualityTb.setCreateUserName("张立肖");
                cerPlasmidQualityTb.setCreateUserId(24);
                cerPlasmidQualityTb.setUpdateTime(new Date());
                cerPlasmidQualityTb.setCreateTime(new Date());
                if (StringUtils.isNotEmpty(plasmid.agrobacteriumInformation)) {
                    cerPlasmidQualityTb.setQualityInspectionType(JSONUtil.toJsonStr(Arrays.asList("2")));
                } else {
                    cerPlasmidQualityTb.setQualityInspectionType(JSONUtil.toJsonStr(Arrays.asList("1")));
                }

                cerPlasmidQualityTb.setAgrobacteriumResistance(plasmid.getAgrobacteriumResistance());
                cerPlasmidQualityTb.setPlasmidConcentration(plasmid.getPlasmidConcentration());
                cerPlasmidQualityTb.setExtractionKit(plasmid.getExtractionKit());
                cerPlasmidQualityTb.setTaskStatus(cerPlasmidQualityTbList.get(0).getTaskStatus());
                cerPlasmidQualityTb.setTaskNum(cerPlasmidQualityTbList.get(0).getTaskNum());
                cerPlasmidQualityTb.setFileUrls(JSONUtil.toJsonStr(cerPlasmidQualityTbList.get(0).getFileUrls()));
                cerPlasmidQualityTb.setProjectCode(cerVectorTaskTb.getProjectCode());
                cerPlasmidQualityTb.setSubProjectCode(cerVectorTaskTb.getSubProjectCode());
                cerPlasmidQualityTb.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                cerPlasmidQualityTb.setRemark(cerPlasmidQualityTbList.get(0).getRemark());
                cerPlasmidQualityTbMapper.insert(cerPlasmidQualityTb);
            }

        });


        return ResponseResult.getSuccess("ok");

    }

    @Data
    public static class Plasmid {

        /**
         * 质粒名称
         */
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
         * 农杆菌信息
         */
        @ExcelProperty("质检农杆菌信息")
        private String agrobacteriumInformation;


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


        @ExcelProperty("实施方案编号")
        private String vectorTaskCode;
    }
}
