package com.bio.drqi.applet.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.applet.dto.req.ScanCodePlasmidReqDTO;
import com.bio.drqi.applet.dto.req.ScanCodeSampleTestReqDTO;
import com.bio.drqi.applet.dto.req.ScanCodeTransformReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodePlasmidRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeTransformRspDTO;
import com.bio.drqi.applet.service.ScanCodeService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 扫码
 */
@RestController
@RequestMapping("scan")
public class ScanCodeController {

    @Resource
    private ScanCodeService scanCodeService;


    /**
     * 质粒标签扫码
     * @param scanCodePlasmidReqDTO
     * @return
     */
    @PostMapping("plasmidDetail")
    public ResponseResult<ScanCodePlasmidRspDTO> plasmidDetail(@RequestBody ScanCodePlasmidReqDTO scanCodePlasmidReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.plasmidDetail(scanCodePlasmidReqDTO));
    }


    /**
     * 转化标签扫码
     * @param scanCodeTransformReqDTO
     * @return
     */
    @PostMapping("transform")
    public ResponseResult<ScanCodeTransformRspDTO> transform(@RequestBody  ScanCodeTransformReqDTO scanCodeTransformReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.transform(scanCodeTransformReqDTO));
    }

    /**
     * 取样标签扫码
     * @param scanCodeSampleTestReqDTO
     * @return
     */
    @PostMapping("sampleTest")
    public ResponseResult<ScanCodeSampleTestRspDTO> sampleTest(@RequestBody  ScanCodeSampleTestReqDTO scanCodeSampleTestReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.sampleTest(scanCodeSampleTestReqDTO));
    }
}
