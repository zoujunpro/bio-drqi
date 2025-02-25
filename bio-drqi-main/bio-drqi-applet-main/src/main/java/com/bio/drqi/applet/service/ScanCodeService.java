package com.bio.drqi.applet.service;


import com.bio.drqi.applet.dto.req.QueryByPlantCodeReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeT0PlantTestRspDTO;

public interface ScanCodeService {

    ScanCodeRspDTO scanCode(String code);

    ScanCodeT0PlantTestRspDTO queryByPlantCode(QueryByPlantCodeReqDTO queryByPlantCodeReqDTO);

}
