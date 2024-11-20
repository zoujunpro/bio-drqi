package com.bio.drqi.applet.service;


import com.bio.drqi.applet.service.parse.dto.ParseCodePlasmidDTO;
import com.bio.drqi.applet.dto.req.ScanCodeSampleTestReqDTO;
import com.bio.drqi.applet.dto.req.ScanCodeTransformReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodePlasmidRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeTransformRspDTO;

public interface ScanCodeService {

    Object scanCode(String code);
    ScanCodePlasmidRspDTO plasmidDetail(ParseCodePlasmidDTO parseCodePlasmidDTO);

    ScanCodeTransformRspDTO transform(ScanCodeTransformReqDTO scanCodeTransformReqDTO);

    ScanCodeSampleTestRspDTO sampleTest(ScanCodeSampleTestReqDTO scanCodeSampleTestReqDTO);

}
