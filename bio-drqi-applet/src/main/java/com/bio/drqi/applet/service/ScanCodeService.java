package com.bio.drqi.applet.service;


import com.bio.drqi.applet.dto.req.ScanCodePlasmidReqDTO;
import com.bio.drqi.applet.dto.req.ScanCodeSampleTestReqDTO;
import com.bio.drqi.applet.dto.req.ScanCodeTransformReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodePlasmidRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeTransformRspDTO;

public interface ScanCodeService {


    ScanCodePlasmidRspDTO plasmidDetail(ScanCodePlasmidReqDTO scanCodePlasmidReqDTO);

    ScanCodeTransformRspDTO transform(ScanCodeTransformReqDTO scanCodeTransformReqDTO);

    ScanCodeSampleTestRspDTO sampleTest(ScanCodeSampleTestReqDTO scanCodeSampleTestReqDTO);

}
