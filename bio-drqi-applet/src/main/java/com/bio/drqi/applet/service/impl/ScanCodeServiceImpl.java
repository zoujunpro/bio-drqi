package com.bio.drqi.applet.service.impl;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.SpringUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.applet.contant.ScanCodeConstant;
import com.bio.drqi.applet.service.ScanCodeService;
import com.bio.drqi.applet.service.codescan.BaseCodeScanService;
import com.bio.print.api.PrintApi;
import com.bio.print.rsp.PrintDataRspDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ScanCodeServiceImpl implements ScanCodeService {

    @Resource
    private PrintApi printApi;

    @Override
    public Object scanCode(String code) {
        ResponseResult<PrintDataRspDTO> responseResult = printApi.queryPrintDataByCode(code);
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        PrintDataRspDTO printDataRspDTO = responseResult.getData();
        if (StringUtils.isEmpty(printDataRspDTO.getUniqueCode())) {
            throw new BusinessException("二维码异常，请联系管理员：" + code);
        }
        BaseCodeScanService baseCodeScanService = (BaseCodeScanService) SpringUtils.getBean(ScanCodeConstant.scanCodeClassMap.get(printDataRspDTO.getPrintType()));
        return baseCodeScanService.doScan(printDataRspDTO.getUniqueCode());
    }

}
