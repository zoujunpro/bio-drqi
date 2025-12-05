package com.bio.drqi.applet.service.codescan.template;

import com.bio.drqi.applet.dto.rsp.ScanCodePlantSampleTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.unique.PlantSampleTestUniqueReqDTO;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 取样扫码
 */
@Service
public class PlantSampleTestCodeScanService extends AbstractBaseCodeScanService<PlantSampleTestUniqueReqDTO, ScanCodePlantSampleTestRspDTO> {


    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleSampleTwoResultDetailTbMapper;

    @Override
    public PlantSampleTestUniqueReqDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        PlantSampleTestUniqueReqDTO plantSampleTestUniqueReqDTO = new PlantSampleTestUniqueReqDTO();
        plantSampleTestUniqueReqDTO.setRegionNum(uniqueCodeArr[1]);
        plantSampleTestUniqueReqDTO.setSeedNum(uniqueCodeArr[2]);
        return plantSampleTestUniqueReqDTO;
    }


    @Override
    public ScanCodePlantSampleTestRspDTO dealCodeContent(PlantSampleTestUniqueReqDTO plantSampleTestUniqueReqDTO) {

        return null;
    }


}


