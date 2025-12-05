package com.bio.drqi.applet.service.codescan.template;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodeT0PlantTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.unique.PlantUniqueCodeDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PlantCodeScanService extends AbstractBaseCodeScanService<PlantUniqueCodeDTO, ScanCodeT0PlantTestRspDTO> {

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;


    @Override
    public PlantUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        PlantUniqueCodeDTO plantUniqueCodeDTO = new PlantUniqueCodeDTO();
        plantUniqueCodeDTO.setPlantCode(uniqueCodeArr[0]);
        return plantUniqueCodeDTO;
    }

    @Override
    public ScanCodeT0PlantTestRspDTO dealCodeContent(PlantUniqueCodeDTO plantUniqueCodeDTO) {
        PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(plantUniqueCodeDTO.getPlantCode());
        if (plantSingleStockTb == null) {
            throw new BusinessException("无此T0代种植信息");
        }
        ScanCodeT0PlantTestRspDTO scanCodeT0PlantTestRspDTO = BeanUtils.copyProperties(plantSingleStockTb, ScanCodeT0PlantTestRspDTO.class);
        scanCodeT0PlantTestRspDTO.setGeneration(GenerationEnum.getGenerationDesc(scanCodeT0PlantTestRspDTO.getGeneration()));
        return scanCodeT0PlantTestRspDTO;
    }
}
