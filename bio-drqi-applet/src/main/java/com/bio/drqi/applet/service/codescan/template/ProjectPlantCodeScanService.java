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
public class ProjectPlantCodeScanService extends AbstractBaseCodeScanService<PlantUniqueCodeDTO, ScanCodeT0PlantTestRspDTO> {

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;


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
        PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(plantUniqueCodeDTO.getPlantCode());
        if (plantSingleStockTb == null) {
            throw new BusinessException("无此T0代种植信息");
        }
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(plantSingleStockTb.getPlantCode());
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(plantSingleStockTb.getVectorTaskCode());

        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectOneBySubProjectCode(cerVectorTaskTb.getSubProjectCode());
        ScanCodeT0PlantTestRspDTO scanCodeT0PlantTestRspDTO = new ScanCodeT0PlantTestRspDTO();

        scanCodeT0PlantTestRspDTO.setProjectCode(cerVectorTaskTb.getProjectCode());
        scanCodeT0PlantTestRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        scanCodeT0PlantTestRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        scanCodeT0PlantTestRspDTO.setPlantDtlInfo(BeanUtils.copyProperties(plantSingleStockTb, ScanCodeT0PlantTestRspDTO.PlantDtlInfo.class));
        scanCodeT0PlantTestRspDTO.getPlantDtlInfo().setGeneration(GenerationEnum.getGenerationDesc(scanCodeT0PlantTestRspDTO.getPlantDtlInfo().getGeneration()));


        //取样信息
        scanCodeT0PlantTestRspDTO.setSampleInfoList(BeanUtils.copyToList(bioSampleTestTbList, ScanCodeT0PlantTestRspDTO.SampleInfo.class));
        return scanCodeT0PlantTestRspDTO;
    }
}
