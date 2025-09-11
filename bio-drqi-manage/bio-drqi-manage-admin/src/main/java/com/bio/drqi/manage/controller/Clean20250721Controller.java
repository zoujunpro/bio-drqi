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
import com.bio.drqi.manage.dto.project.PlasmidDTO;
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
import java.util.Arrays;
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


    @GetMapping("cleanPlasmid")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlasmid() {
        List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectList(null);
        for (CerPlasmidQualityTb cerPlasmidQualityTb : cerPlasmidQualityTbList) {
            log.info("cerPlasmidQualityTb={}", JSONUtil.toJsonStr(cerPlasmidQualityTb));
            if (cerPlasmidQualityTb.getQualityInspectionType().length() == 1) {
                cerPlasmidQualityTb.setQualityInspectionType(JSONUtil.toJsonStr(Arrays.asList(cerPlasmidQualityTb.getQualityInspectionType())));
                cerPlasmidQualityTbMapper.updateById(cerPlasmidQualityTb);
            }
        }
        List<BioTaskDtlTb> list = bioTaskDtlTbMapper.selectAllByTaskTypeCode("plasmid_check");
        for (BioTaskDtlTb bioTaskDtlTb : list) {
            log.info("bioTaskDtlTb={}", JSONUtil.toJsonStr(bioTaskDtlTb));
            PlasmidDTO plasmidDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlasmidDTO.class);
            if (CollectionUtil.isEmpty(plasmidDTO.getContentList())) {
                continue;
            }
            for (PlasmidDTO.Content content : plasmidDTO.getContentList()) {
                if (content.getQualityInspectionType().length() == 1) {
                    content.setQualityInspectionType(JSONUtil.toJsonStr(Arrays.asList(content.getQualityInspectionType())));
                }
            }
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plasmidDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }
        return ResponseResult.getSuccess("ok");

    }


    @GetMapping("/cleanVector")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanVector() {
        List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectSelective(null);
        for (CerVectorTb cerVectorTb : cerVectorTbList) {
            log.info("cerVectorTb={}", JSONUtil.toJsonStr(cerVectorTb));
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(cerVectorTb.getVectorTaskId());
            if (cerVectorTaskTb != null) {
                cerVectorTb.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
                cerVectorTbMapper.updateById(cerVectorTb);
            }
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanSubProject")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSubProject() {
        List<CerSubProjectTb> cerSubProjectTbList = cerSubProjectTbMapper.selectSelective(null);
        for (CerSubProjectTb cerSubProjectTb : cerSubProjectTbList) {
            CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(cerSubProjectTb.getProjectId());
            if (cerProjectTb != null) {
                cerSubProjectTb.setProjectCode(cerProjectTb.getProjectCode());
                cerSubProjectTbMapper.updateById(cerSubProjectTb);
            }
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanConversionAndTrans")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanConversionAndTrans() {
        List<CerConversionAndTransRef> list = cerConversionAndTransRefMapper.selectList(null);
        for (CerConversionAndTransRef cerConversionAndTransRef : list) {
            log.info("cerConversionAndTransRef={}", JSONUtil.toJsonStr(cerConversionAndTransRef));
            CerConversionAndTransTb cerConversionAndTransTb = cerConversionAndTransTbMapper.selectById(cerConversionAndTransRef.getConversionAndTransId());
            cerConversionAndTransRef.setTaskNum(cerConversionAndTransTb.getTaskNum());
            cerConversionAndTransRefMapper.updateById(cerConversionAndTransRef);
        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanSampleAcceptorMaterial")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSampleAcceptorMaterial() {
        List<CerSampleTestTb> list = cerSampleTestTbMapper.selectList(null);
        for (CerSampleTestTb cerSampleTestTb : list) {
            log.info("cerSampleTestTb={}", JSONUtil.toJsonStr(cerSampleTestTb));
            if (cerSampleTestTb.getAcceptorMaterial() == null || cerSampleTestTb.getAcceptorMaterial().length() == 32) {
                CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(cerSampleTestTb.getTransformCode(), cerSampleTestTb.getVectorTaskCode());
                cerSampleTestTb.setAcceptorMaterial(cerTransformTb.getAcceptorMaterial());
                    cerSampleTestTbMapper.updateById(cerSampleTestTb);
            }

        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanCloneSampleCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanCloneSampleCode() {
        List<CerSampleTestTb> list = cerSampleTestTbMapper.selectList(null);
        for (CerSampleTestTb cerSampleTestTb : list) {
            log.info("cerSampleTestTb={}", JSONUtil.toJsonStr(cerSampleTestTb));
            if (cerSampleTestTb.getSampleCode().contains("-")) {
                cerSampleTestTb.setCloneSampleCode(cerSampleTestTb.getSampleCode().split("-")[0]);
                cerSampleTestTbMapper.updateById(cerSampleTestTb);
            }

        }
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanPlantAcceptorMaterial")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanPlantAcceptorMaterial() {
        List<CerPlantDtlTb> cerPlantDtlTbList = cerPlantDtlTbMapper.selectSelective(null);
        for (CerPlantDtlTb cerPlantDtlTb : cerPlantDtlTbList) {
            log.info("cerPlantDtlTb={}",JSONUtil.toJsonStr(cerPlantDtlTb));
            if (cerPlantDtlTb.getAcceptorMaterial() != null && cerPlantDtlTb.getAcceptorMaterial().length() == 32) {
                List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllBySampleCode(cerPlantDtlTb.getSampleCode());
                    cerPlantDtlTb.setAcceptorMaterial(cerSampleTestTbList.get(0).getAcceptorMaterial());
                    cerPlantDtlTbMapper.updateById(cerPlantDtlTb);
            }
        }
        return ResponseResult.getSuccess("ok");

    }

    @GetMapping("/cleanStepCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanStepCode() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectSelective(null);
        for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
            log.info("cerVectorTaskTb={}", JSONUtil.toJsonStr(cerVectorTaskTb));
            List<CerVectorStepLog> cerVectorStepLogList = cerVectorStepLogMapper.selectAllByVectorTaskIdOrderById(cerVectorTaskTb.getId());
            if (CollectionUtil.isNotEmpty(cerVectorStepLogList)) {
                cerVectorTaskTb.setCurrentStepCode(cerVectorStepLogList.get(cerVectorStepLogList.size() - 1).getStepCode());
                cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
            }
        }
        return ResponseResult.getSuccess("ok");
    }

}
