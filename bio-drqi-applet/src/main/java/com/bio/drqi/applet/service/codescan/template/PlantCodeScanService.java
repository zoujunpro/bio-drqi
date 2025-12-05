package com.bio.drqi.applet.service.codescan.template;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodePlantTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.unique.PlantUniqueCodeDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PlantCodeScanService extends AbstractBaseCodeScanService<PlantUniqueCodeDTO, ScanCodePlantTestRspDTO> {

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;


    @Override
    public PlantUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        PlantUniqueCodeDTO plantUniqueCodeDTO = new PlantUniqueCodeDTO();
        plantUniqueCodeDTO.setPlantCode(uniqueCode);
        return plantUniqueCodeDTO;
    }

    @Override
    public ScanCodePlantTestRspDTO dealCodeContent(PlantUniqueCodeDTO plantUniqueCodeDTO) {
        PlantSingleStockTb plantSingleStockTb = plantSingleStockTbMapper.selectOneByPlantCode(plantUniqueCodeDTO.getPlantCode());
        if (plantSingleStockTb == null) {
            throw new BusinessException("无此T0代种植信息");
        }
        ScanCodePlantTestRspDTO scanCodePlantTestRspDTO = BeanUtils.copyProperties(plantSingleStockTb, ScanCodePlantTestRspDTO.class);
        scanCodePlantTestRspDTO.setGeneration(GenerationEnum.getGenerationDesc(scanCodePlantTestRspDTO.getGeneration()));
        return scanCodePlantTestRspDTO;
    }
}
