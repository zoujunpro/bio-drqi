package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.applet.dto.rsp.ScanCodePlantSampleTestRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeProjectSampleTestRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.PlantSampleTestUniqueReqDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

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


