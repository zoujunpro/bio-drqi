package com.bio.drqi.applet.service.codescan.template;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodePlantApplyTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.unique.PlantApplyUniqueCodeDTO;
import com.bio.drqi.domain.PlantApplyDetailTb;
import com.bio.drqi.mapper.PlantApplyDetailTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class PlantApplyCodeScanService extends AbstractBaseCodeScanService<PlantApplyUniqueCodeDTO, ScanCodePlantApplyTestRspDTO> {

    @Resource
    private PlantApplyDetailTbMapper plantApplyDetailTbMapper;

    @Override
    public PlantApplyUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        PlantApplyUniqueCodeDTO plantApplyUniqueCode = new PlantApplyUniqueCodeDTO();
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        plantApplyUniqueCode.setRegionNum(uniqueCodeArr[0]);
        plantApplyUniqueCode.setSeedNum(uniqueCodeArr[1]);
        return plantApplyUniqueCode;
    }

    @Override
    public ScanCodePlantApplyTestRspDTO dealCodeContent(PlantApplyUniqueCodeDTO plantApplyUniqueCodeDTO) {
        PlantApplyDetailTb plantApplyDetailTb = plantApplyDetailTbMapper.selectOneByRegionNumAndSeedNum(plantApplyUniqueCodeDTO.getRegionNum(), plantApplyUniqueCodeDTO.getSeedNum());
        if (plantApplyDetailTb == null) {
            throw new BusinessException("找不到此种植申请数据，小区编号：" + plantApplyUniqueCodeDTO.getRegionNum() + "种子编号：" + plantApplyUniqueCodeDTO.getSeedNum());
        }

        return BeanUtils.copyProperties(plantApplyDetailTb, ScanCodePlantApplyTestRspDTO.class);
    }
}
