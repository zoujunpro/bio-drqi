package com.bio.drqi.applet.service.impl;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.SpringUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.applet.contant.ScanCodeConstant;
import com.bio.drqi.applet.dto.req.QueryByPlantCodeReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeT0PlantTestRspDTO;
import com.bio.drqi.applet.service.ScanCodeService;
import com.bio.drqi.applet.service.codescan.BaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.unique.PlantUniqueCodeDTO;
import com.bio.drqi.applet.service.codescan.template.ProjectPlantCodeScanService;
import com.bio.print.api.PrintApi;
import com.bio.print.rsp.PrintDataRspDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ScanCodeServiceImpl implements ScanCodeService {

    @Resource
    private PrintApi printApi;

    @Resource
    private ProjectPlantCodeScanService projectPlantCodeScanService;

    @Override
    public ScanCodeRspDTO scanCode(String code) {
        ResponseResult<PrintDataRspDTO> responseResult = printApi.queryPrintDataByCode(code);
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        PrintDataRspDTO printDataRspDTO = responseResult.getData();
        if (StringUtils.isEmpty(printDataRspDTO.getUniqueCode())) {
            throw new BusinessException("二维码异常，请联系管理员：" + code);
        }
        BaseCodeScanService baseCodeScanService = (BaseCodeScanService) SpringUtils.getBean(ScanCodeConstant.scanCodeClassMap.get(printDataRspDTO.getPrintType()));
        ScanCodeRspDTO scanCodeRspDTO = new ScanCodeRspDTO();
        scanCodeRspDTO.setType(printDataRspDTO.getPrintType());
        scanCodeRspDTO.setPrintData(printDataRspDTO.getPrintData());
        scanCodeRspDTO.setData(baseCodeScanService.doScan(printDataRspDTO.getUniqueCode()));
        return scanCodeRspDTO;
    }

    @Override
    public ScanCodeT0PlantTestRspDTO queryByPlantCode(QueryByPlantCodeReqDTO queryByPlantCodeReqDTO) {
        PlantUniqueCodeDTO plantUniqueCodeDTO=new PlantUniqueCodeDTO();
        plantUniqueCodeDTO.setPlantCode(queryByPlantCodeReqDTO.getPlantCode());
        plantUniqueCodeDTO.setVectorTaskCode(queryByPlantCodeReqDTO.getVectorTaskCode());
        ScanCodeT0PlantTestRspDTO scanCodeT0PlantTestRspDTO = projectPlantCodeScanService.dealCodeContent(plantUniqueCodeDTO);
        return scanCodeT0PlantTestRspDTO;
    }

}
