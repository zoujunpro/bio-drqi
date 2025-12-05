package com.bio.drqi.applet.service;


import com.bio.drqi.applet.dto.req.QueryByPlantCodeReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodePlantTestRspDTO;

public interface ScanCodeService {

    ScanCodeRspDTO scanCode(String code);

    ScanCodePlantTestRspDTO queryByPlantCode(QueryByPlantCodeReqDTO queryByPlantCodeReqDTO);

}
