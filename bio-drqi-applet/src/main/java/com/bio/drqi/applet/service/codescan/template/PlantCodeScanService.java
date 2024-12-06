package com.bio.drqi.applet.service.codescan.template;

import com.bio.drqi.applet.dto.rsp.ScanCodePlantTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.PlantUniqueCodeDTO;
import com.bio.drqi.mapper.CerPlantDtlTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PlantCodeScanService extends AbstractBaseCodeScanService<PlantUniqueCodeDTO, ScanCodePlantTestRspDTO> {

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;


    @Override
    public PlantUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        PlantUniqueCodeDTO plantUniqueCodeDTO=new PlantUniqueCodeDTO();
        plantUniqueCodeDTO.setVectorTaskCode(uniqueCodeArr[0]);
        plantUniqueCodeDTO.setPlantCode(uniqueCodeArr[1]);
        return plantUniqueCodeDTO;
    }

    @Override
    public ScanCodePlantTestRspDTO dealCodeContent(PlantUniqueCodeDTO plantUniqueCodeDTO) {

        return null;
    }
}
