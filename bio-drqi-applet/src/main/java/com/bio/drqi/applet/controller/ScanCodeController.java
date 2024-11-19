package com.bio.drqi.applet.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.applet.dto.req.ScanCodePlasmidReqDTO;
import com.bio.drqi.applet.dto.req.ScanCodeSampleTestReqDTO;
import com.bio.drqi.applet.dto.req.ScanCodeTransformReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodePlasmidRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeTransformRspDTO;
import com.bio.drqi.applet.service.ScanCodeService;

import javax.annotation.Resource;

public class ScanCodeController {

    @Resource
    private ScanCodeService scanCodeService;


    public ResponseResult<ScanCodePlasmidRspDTO> plasmidDetail(ScanCodePlasmidReqDTO scanCodePlasmidReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.plasmidDetail(scanCodePlasmidReqDTO));
    }

    public ResponseResult<ScanCodeTransformRspDTO> transform(ScanCodeTransformReqDTO scanCodeTransformReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.transform(scanCodeTransformReqDTO));
    }

    public ResponseResult<ScanCodeSampleTestRspDTO> sampleTest(ScanCodeSampleTestReqDTO scanCodeSampleTestReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.sampleTest(scanCodeSampleTestReqDTO));
    }
}
