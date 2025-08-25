package com.bio.drqi.manage.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.NewSampleTestDTO;
import com.bio.drqi.manage.dto.project.TransformDTO;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import com.bio.drqi.tc.service.dto.TcPollinationTaskDTO;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import com.lark.oapi.service.task.v1.enums.SourceEnum;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test")
public class Clean20250721Controller {

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
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;


    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;


    @GetMapping("cleanTaskNew")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTaskNew() {
        List<VectorTaskExcel> vectorTaskExcelList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\无标题-LWR.xlsx", VectorTaskExcel.class);
        for (VectorTaskExcel vectorTaskExcel : vectorTaskExcelList) {
            log.info("vectorTaskExce={}", JSONUtil.toJsonStr(vectorTaskExcel));
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(vectorTaskExcel.id);
            if (cerVectorTaskTb == null) {
                throw new BusinessException("数据异常");
            }
            if (!cerVectorTaskTb.getVectorTaskCode().equals(vectorTaskExcel.vectorTaskCode)) {
                throw new BusinessException("数据匹配失败");
            }
            StringBuffer breedCode = new StringBuffer("");
            String[] breedNameArr = vectorTaskExcel.breedName.split("\\|");
            for (String breedName : breedNameArr) {
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(breedName, cerVectorTaskTb.getSpeciesCode());
                if (cerBreedDict == null) {
                    throw new BusinessException("找不到品种");
                }
                breedCode.append(cerBreedDict.getBreedCode()).append("|");
            }
            cerVectorTaskTb.setBreedCode(breedCode.substring(0, breedCode.length() - 1));
            cerVectorTaskTb.setAcceptorMaterial(vectorTaskExcel.acceptorMaterial);
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("cleanTaskNewDev")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTaskNewDev() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectList(null);
        for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
            log.info("cerVectorTaskTb={}", JSONUtil.toJsonStr(cerVectorTaskTb));
            cerVectorTaskTb.setBreedCode(cerVectorTaskTb.getAcceptorMaterial());
            StringBuffer breedNameBuf = new StringBuffer("");
            String[] breedCodeArr = cerVectorTaskTb.getAcceptorMaterial().split("\\|");
            for (String breedCode : breedCodeArr) {
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(breedCode);
                if (cerBreedDict == null) {
                    throw new BusinessException("找不到品种");
                }
                breedNameBuf.append(cerBreedDict.getBreedName()).append("|");
            }
            cerVectorTaskTb.setAcceptorMaterial(breedNameBuf.substring(0, breedNameBuf.length() - 1));
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
        }
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("cleanTransFormNew")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTransFormNew() {
        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectList(null);
        for (CerTransformTb cerTransformTb : cerTransformTbList) {
            log.info("cerTransformTb={}", JSONUtil.toJsonStr(cerTransformTb));
            if (cerTransformTb.getAcceptorMaterial().length() == 32) {
                CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(cerTransformTb.getAcceptorMaterial());
                cerTransformTb.setAcceptorMaterial(cerBreedDict.getBreedName());
                cerTransformTbMapper.updateById(cerTransformTb);
            }
        }

        List<BioTaskDtlTb> bioTaskDtlTbList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("transform");
        for (BioTaskDtlTb bioTaskDtlTb : bioTaskDtlTbList) {
            log.info("bioTaskDtlTb={}", JSONUtil.toJsonStr(bioTaskDtlTb));
            TransformDTO transformDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TransformDTO.class);
            if (CollectionUtil.isEmpty(transformDTO.getContentList())) {
                continue;
            }
            if (BioTaskStatusEnum.TASK_STATUS_4.status.equals(bioTaskDtlTb.getTaskStatus())
                    || BioTaskStatusEnum.TASK_STATUS_3.status.equals(bioTaskDtlTb.getTaskStatus())) {
                continue;
            }

            for (TransformDTO.Content content : transformDTO.getContentList()) {
                if (content.getAcceptorMaterial().length() == 32) {
                    CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedCode(content.getAcceptorMaterial());
                    if (cerBreedDict == null) {
                        throw new BusinessException("找不到品种");
                    }
                    content.setAcceptorMaterial(cerBreedDict.getBreedName());
                    bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(transformDTO));
                    bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
                }
            }
        }

        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("cleanSampleAcceptorMaterial")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSample() {
        List<BioTaskDtlTb> bioTaskDtlTbList = bioTaskDtlTbMapper.selectAllByTaskTypeCode("sample_and_test");
        for (BioTaskDtlTb bioTaskDtlTb : bioTaskDtlTbList) {
            log.info("bioTaskDtlTb={}", JSONUtil.toJsonStr(bioTaskDtlTb));
            NewSampleTestDTO newSampleTestDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), NewSampleTestDTO.class);
            if (CollectionUtil.isNotEmpty(newSampleTestDTO.getFirstSampleApplyList())) {
                for (NewSampleTestDTO.FirstSampleApply firstSampleApply : newSampleTestDTO.getFirstSampleApplyList()) {
                    CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(firstSampleApply.getTransformCode(), firstSampleApply.getVectorTaskCode());
                    if (cerTransformTb == null) {
                        throw new BusinessException("找不到转化编号");
                    }
                    firstSampleApply.setAcceptorMaterial(cerTransformTb.getAcceptorMaterial());
                }
            }
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(newSampleTestDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("cleanSampleOne")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSampleOne() {
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByApplyNo("T0001566");
        for (CerSampleTestTb cerSampleTestTb : cerSampleTestTbList) {
            String sampleCodeNum = cerSampleTestTb.getSampleCode().substring(2);
            if (cerSampleTestTb.getSampleCode().startsWith("AB")) {
                cerSampleTestTb.setSampleCode("AB" + (Integer.valueOf(sampleCodeNum) - 7));
                if (StringUtils.isNotEmpty(cerSampleTestTb.getUniqueCode())) {
                }
            } else if (cerSampleTestTb.getSampleCode().startsWith("TO")) {
                cerSampleTestTb.setSampleCode("TO" + (Integer.valueOf(sampleCodeNum) - 29));
            }
            cerSampleTestTb.setUniqueCode(cerSampleTestTb.getProjectCode() + cerSampleTestTb.getSampleCode());
            cerSampleTestTbMapper.updateById(cerSampleTestTb);
        }
        return ResponseResult.getSuccess("ok");

    }


    @Data
    public static class VectorTaskExcel {
        @ExcelProperty("id")
        private String id;

        @ExcelProperty("实施方案编号")
        private String vectorTaskCode;


        @ExcelProperty("受体材料")
        private String acceptorMaterial;

        @ExcelProperty("品种")
        private String breedName;

    }

}
