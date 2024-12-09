package com.bio.drqi.applet.service.codescan.template;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodeT0PlantTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.PlantUniqueCodeDTO;
import com.bio.drqi.domain.CerPlantDtlTb;
import com.bio.drqi.domain.CerProjectTb;
import com.bio.drqi.domain.CerSubProjectTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.mapper.CerPlantDtlTbMapper;
import com.bio.drqi.mapper.CerProjectTbMapper;
import com.bio.drqi.mapper.CerSubProjectTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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
        ScanCodeT0PlantTestRspDTO scanCodeT0PlantTestRspDTO=new ScanCodeT0PlantTestRspDTO();

        scanCodeT0PlantTestRspDTO.setProjectCode(cerProjectTb.getProjectCode());
        scanCodeT0PlantTestRspDTO.setProjectName(cerProjectTb.getProjectName());
        scanCodeT0PlantTestRspDTO.setSubProjectCode(cerSubProjectTb.getSubProjectCode());
        scanCodeT0PlantTestRspDTO.setSubProjectName(cerSubProjectTb.getSubProjectName());
        scanCodeT0PlantTestRspDTO.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        scanCodeT0PlantTestRspDTO.setVectorTaskName(cerVectorTaskTb.getVectorTaskName());
        scanCodeT0PlantTestRspDTO.setPlantDtlInfo(BeanUtils.copyProperties(cerPlantDtlTb, ScanCodeT0PlantTestRspDTO.PlantDtlInfo.class));
        return scanCodeT0PlantTestRspDTO;
    }
}
