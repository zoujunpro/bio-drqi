package com.bio.drqi.manage.controller;


import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.devOps.DevOpsModifySubProjectCodeReqDTO;
import com.bio.drqi.manage.devOps.DevOpsModifyVectorTaskCodeBreedCodeReqDTO;
import com.bio.drqi.manage.dto.plant.task.PlantExperimentTaskDTO;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/devOpsTest")
@Slf4j
public class DevOpsController {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;


    @Resource
    private PlantMultipleStockTbMapper plantMultipleStockTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;


    @Resource
    private BioSampleCodePrefixTbMapper bioSampleCodePrefixTbMapper;


    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    @Resource
    private BioSampleApplyTbMapper bioSampleApplyTbMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private CerPlasmidQualityTbMapper cerPlasmidQualityTbMapper;

    @Resource
    private PlantApplyDetailTbMapper plantApplyDetailTbMapper;

    @Resource
    private PlantApplyTbMapper plantApplyTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @PostMapping("modifyVectorTaskCodeBreedCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> modifyVectorTaskCodeBreedCode(@RequestBody DevOpsModifyVectorTaskCodeBreedCodeReqDTO devOpsModifyVectorTaskCodeBreedCodeReqDTO) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(devOpsModifyVectorTaskCodeBreedCodeReqDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("无此实施方案号");
        }
        CerBreedDict cerBreedDict = cerBreedDictMapper.selectOneByBreedNameAndSpeciesCode(devOpsModifyVectorTaskCodeBreedCodeReqDTO.getBreedName(), cerVectorTaskTb.getSpeciesCode());
        if (cerBreedDict == null) {
            throw new BusinessException("物种" + cerVectorTaskTb.getBreedCode() + "下无此品种信息");
        }
        cerVectorTaskTb.setBreedCode(cerBreedDict.getBreedCode());
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);

        List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
        if (CollectionUtil.isNotEmpty(cerTransformTbList)) {
            for (CerTransformTb cerTransformTb : cerTransformTbList) {
                cerTransformTb.setBreedCode(cerBreedDict.getBreedCode());
                cerTransformTbMapper.updateById(cerTransformTb);

                PlantMultipleStockTb plantMultipleStockTb = plantMultipleStockTbMapper.selectOneByVectorTaskCodeAndTransformCode(cerTransformTb.getVectorTaskCode(), cerTransformTb.getTransformCode());
                if (plantMultipleStockTb != null) {
                    plantMultipleStockTb.setBreedCode(cerBreedDict.getBreedCode());
                    plantMultipleStockTbMapper.updateById(plantMultipleStockTb);
                }
            }
        }

        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByVectorTaskCodeAndSourceCode(cerVectorTaskTb.getVectorTaskCode(), SourceCodeEnum.project.name());
        if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
            for (BioSampleTestTb bioSampleTestTb : bioSampleTestTbList) {
                bioSampleTestTb.setBreedCode(cerBreedDict.getBreedCode());
                bioSampleTestTbMapper.updateById(bioSampleTestTb);
            }
            List<PlantSingleStockTb> plantSingleStockTbList = plantSingleStockTbMapper.selectAllByPlantCodeIn(bioSampleTestTbList.stream().map(BioSampleTestTb::getSampleCode).collect(Collectors.toList()));
            if (CollectionUtil.isNotEmpty(plantSingleStockTbList)) {
                for (PlantSingleStockTb plantSingleStockTb : plantSingleStockTbList) {
                    plantSingleStockTb.setBreedCode(cerBreedDict.getBreedCode());
                    plantSingleStockTbMapper.updateById(plantSingleStockTb);
                }
            }
        }
        return ResponseResult.getSuccess("ok");
    }


    @PostMapping("/cleanSubProjectCode")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSubProjectCode(@RequestBody DevOpsModifySubProjectCodeReqDTO devOpsModifySubProjectCodeReqDTO) {
        log.info("子项目编号修改开始");
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(devOpsModifySubProjectCodeReqDTO.getOldSubProjectCode());
        if (cerSubProjectTb == null) {
            throw new BusinessException("找不到子项目编号");
        }
        cerSubProjectTb.setSubProjectCode(devOpsModifySubProjectCodeReqDTO.getNewSubProjectCode());
        cerSubProjectTbMapper.updateById(cerSubProjectTb);
        //修改实施方案编号和所属子项目编号
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllBySubProjectId(cerSubProjectTb.getId());
        if (CollectionUtil.isNotEmpty(cerVectorTaskTbList)) {
            for (CerVectorTaskTb cerVectorTaskTb : cerVectorTaskTbList) {
                log.info("修改实施方案编号");
                String oldVectorTaskCode = cerVectorTaskTb.getVectorTaskCode();
                String newVectorTaskCode = devOpsModifySubProjectCodeReqDTO.getNewSubProjectCode() +"-"+ cerVectorTaskTb.getVectorTaskCode().split("-")[1];
                cerVectorTaskTb.setVectorTaskCode(newVectorTaskCode);
                cerVectorTaskTb.setSubProjectCode(devOpsModifySubProjectCodeReqDTO.getNewSubProjectCode());
                cerVectorTaskTb.setSubProjectCode(devOpsModifySubProjectCodeReqDTO.getNewSubProjectCode());
                cerVectorTaskTbMapper.updateById(cerVectorTaskTb);

                List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
                if (CollectionUtil.isNotEmpty(cerVectorTbList)) {
                    cerVectorTbList.forEach(cerVectorTb -> {
                        log.info("修改载体构建信息");
                        cerVectorTb.setVectorTaskCode(newVectorTaskCode);
                        cerVectorTbMapper.updateById(cerVectorTb);
                    });
                }
                //修改质粒质检中实施方案编号
                List<CerPlasmidQualityTb> cerPlasmidQualityTbList = cerPlasmidQualityTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
                if (CollectionUtil.isNotEmpty(cerPlasmidQualityTbList)) {
                    cerPlasmidQualityTbList.forEach(cerPlasmidQualityTb -> {
                        log.info("修改质粒质检中实施方案编号");
                        cerPlasmidQualityTb.setVectorTaskCode(newVectorTaskCode);
                        cerPlasmidQualityTb.setSubProjectCode(devOpsModifySubProjectCodeReqDTO.getNewSubProjectCode());
                        cerPlasmidQualityTbMapper.updateById(cerPlasmidQualityTb);
                    });
                }

                List<CerTransformTb> cerTransformTbList = cerTransformTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId());
                if (CollectionUtil.isNotEmpty(cerPlasmidQualityTbList)) {

                    cerTransformTbList.forEach(cerTransformTb -> {
                        log.info("修改转化中实施方案编号");
                        cerTransformTb.setVectorTaskCode(newVectorTaskCode);
                        cerTransformTb.setSubProjectCode(devOpsModifySubProjectCodeReqDTO.getNewSubProjectCode());
                        cerTransformTbMapper.updateById(cerTransformTb);
                    });
                }
                //移苗
                List<CerConversionAndTransRef> cerConversionAndTransRefList = cerConversionAndTransRefMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
                if (CollectionUtil.isNotEmpty(cerConversionAndTransRefList)) {
                    cerConversionAndTransRefList.forEach(cerConversionAndTransRef -> {
                        cerConversionAndTransRef.setVectorTaskCode(newVectorTaskCode);
                        cerConversionAndTransRef.setSubProjectCode(devOpsModifySubProjectCodeReqDTO.getNewSubProjectCode());
                        cerConversionAndTransRefMapper.updateById(cerConversionAndTransRef);
                    });
                }
                //取样库
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
                if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
                    bioSampleTestTbList.forEach(bioSampleTestTb -> {
                        log.info("修改取样中实施方案编号");
                        bioSampleTestTb.setVectorTaskCode(newVectorTaskCode);
                        bioSampleTestTbMapper.updateById(bioSampleTestTb);
                    });
                }
                //取样申请
                List<BioSampleApplyTb> bioSampleApplyTbList = bioSampleApplyTbMapper.selectSelective(null);
                if (CollectionUtil.isNotEmpty(bioSampleApplyTbList)) {
                    bioSampleApplyTbList.forEach(bioSampleApplyTb -> {
                        if(StringUtils.isNotEmpty(bioSampleApplyTb.getVectorTaskCodes())&&bioSampleApplyTb.getVectorTaskCodes().contains(oldVectorTaskCode)){
                            log.info("修改取样申请中实施方案编号");
                            bioSampleApplyTb.setVectorTaskCodes(bioSampleApplyTb.getVectorTaskCodes().replace(oldVectorTaskCode,newVectorTaskCode));
                            bioSampleApplyTbMapper.updateById(bioSampleApplyTb);
                        }

                    });
                }

                List<PlantApplyDetailTb> plantApplyDetailTbList = plantApplyDetailTbMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
                if (CollectionUtil.isNotEmpty(plantApplyDetailTbList)) {
                    log.info("修改CER种植申请中实施方案编号");
                    plantApplyDetailTbList.forEach(plantApplyDetailTb -> {
                        plantApplyDetailTb.setVectorTaskCode(oldVectorTaskCode);
                        plantApplyDetailTbMapper.updateById(plantApplyDetailTb);
                    });
                    plantApplyDetailTbList.stream().map(PlantApplyDetailTb::getPlantApplyNum).distinct().forEach(plantApplyNum -> {
                        PlantApplyTb plantApplyTb = plantApplyTbMapper.selectOneByPlantApplyNum(plantApplyNum);
                        List<String> vectorTaskCodeList = JSONUtil.toList(plantApplyTb.getVectorTaskCodes(), String.class);
                        vectorTaskCodeList.remove(oldVectorTaskCode);
                        vectorTaskCodeList.add(newVectorTaskCode);
                        plantApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(vectorTaskCodeList));
                        plantApplyTbMapper.updateById(plantApplyTb);

                        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(plantApplyNum);
                        PlantExperimentTaskDTO plantExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), PlantExperimentTaskDTO.class);
                        plantExperimentTaskDTO.setVectorTaskCodeList(vectorTaskCodeList);
                        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(plantExperimentTaskDTO));
                        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
                    });

                }
                //修改种子库
                List<SeedStockTb> seedStockTbList = seedStockTbMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
                if (CollectionUtil.isNotEmpty(seedStockTbList)) {
                    log.info("修改种子库中实施方案编号");
                    seedStockTbList.forEach(seedStockTb -> {
                        seedStockTb.setVectorTaskCode(newVectorTaskCode);
                        seedStockTbMapper.updateById(seedStockTb);
                    });
                }

                BioSampleCodePrefixTb bioSampleCodePrefixTb = bioSampleCodePrefixTbMapper.selectOneByVectorTaskCode(oldVectorTaskCode);
                if (bioSampleCodePrefixTb != null) {
                    log.info("修改取样移苗前缀实施方案编号");
                    bioSampleCodePrefixTb.setVectorTaskCode(newVectorTaskCode);
                    bioSampleCodePrefixTbMapper.updateById(bioSampleCodePrefixTb);
                }
                //tc
                List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllByVectorTaskCode(oldVectorTaskCode);
                if (CollectionUtil.isNotEmpty(tcExperimentDesignTbList)) {
                    tcExperimentDesignTbList.forEach(tcExperimentDesignTb -> {
                        tcExperimentDesignTb.setVectorTaskCode(newVectorTaskCode);
                        tcExperimentDesignTbMapper.updateById(tcExperimentDesignTb);
                    });
                    tcExperimentDesignTbList.stream().map(TcExperimentDesignTb::getExperimentNum).distinct().forEach(tcExperimentNum -> {
                        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcExperimentNum);
                        List<String> vectorTaskCodeList = JSONUtil.toList(tcExperimentTb.getVectorTaskCodes(), String.class);
                        vectorTaskCodeList.remove(oldVectorTaskCode);
                        vectorTaskCodeList.add(newVectorTaskCode);
                        tcExperimentTb.setVectorTaskCodes(JSONUtil.toJsonStr(vectorTaskCodeList));
                        tcExperimentTbMapper.updateById(tcExperimentTb);

                        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcExperimentNum);
                        TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
                        tcExperimentTaskDTO.setVectorTaskCodeList(vectorTaskCodeList);
                        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcExperimentTaskDTO));
                        bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
                    });
                }

            }
        }

        return ResponseResult.getSuccess("ok");
    }

    public static void main(String[] args) {
        System.out.println("TC001-04");
    }
}
