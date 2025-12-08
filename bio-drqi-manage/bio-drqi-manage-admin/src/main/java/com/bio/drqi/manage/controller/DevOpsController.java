package com.bio.drqi.manage.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.common.enums.SourceCodeEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.devOps.DevOpsModifyVectorTaskCodeBreedCodeReqDTO;
import com.bio.drqi.mapper.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/devOpsTest")
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
}
