package com.bio.drqi.applet.service.codescan.template;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodeT0PlantTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.PlantUniqueCodeDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class T0PlantCodeScanService extends AbstractBaseCodeScanService<PlantUniqueCodeDTO, ScanCodeT0PlantTestRspDTO> {

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;


    @Override
    public PlantUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        PlantUniqueCodeDTO plantUniqueCodeDTO = new PlantUniqueCodeDTO();
        plantUniqueCodeDTO.setVectorTaskCode(uniqueCodeArr[0]);
        plantUniqueCodeDTO.setPlantCode(uniqueCodeArr[1]);
        return plantUniqueCodeDTO;
    }

    @Override
    public ScanCodeT0PlantTestRspDTO dealCodeContent(PlantUniqueCodeDTO plantUniqueCodeDTO) {
        CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCodeAndVectorTaskCode(plantUniqueCodeDTO.getPlantCode(), plantUniqueCodeDTO.getVectorTaskCode());
        if (cerPlantDtlTb == null) {
            throw new BusinessException("无此T0代种植信息");
        }

        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cerPlantDtlTb.getVectorTaskCode());
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(cerPlantDtlTb.getProjectCode());

        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerPlantDtlTb.getSubProjectCode());
        ScanCodeT0PlantTestRspDTO scanCodeT0PlantTestRspDTO = new ScanCodeT0PlantTestRspDTO();

        scanCodeT0PlantTestRspDTO.setProjectCode(cerProjectTb.getProjectCode());
        scanCodeT0PlantTestRspDTO.setProjectName(cerProjectTb.getProjectName());
        scanCodeT0PlantTestRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        scanCodeT0PlantTestRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        scanCodeT0PlantTestRspDTO.setVectorTaskName(cerVectorTaskTb.getVectorTaskName());
        scanCodeT0PlantTestRspDTO.setPlantDtlInfo(BeanUtils.copyProperties(cerPlantDtlTb, ScanCodeT0PlantTestRspDTO.PlantDtlInfo.class));
        scanCodeT0PlantTestRspDTO.getPlantDtlInfo().setGeneration(GenerationEnum.getGenerationDesc(scanCodeT0PlantTestRspDTO.getPlantDtlInfo().getGeneration()));


        //取样信息
        List<CerSampleTestTb> cerSampleTestTbList = cerSampleTestTbMapper.selectAllByVectorTaskCodeAndSampleCode(plantUniqueCodeDTO.getVectorTaskCode(), plantUniqueCodeDTO.getPlantCode());
        scanCodeT0PlantTestRspDTO.setSampleInfoList(BeanUtils.copyToList(cerSampleTestTbList,ScanCodeT0PlantTestRspDTO.SampleInfo.class));
        return scanCodeT0PlantTestRspDTO;
    }
}
