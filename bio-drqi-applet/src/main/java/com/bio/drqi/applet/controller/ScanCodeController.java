package com.bio.drqi.applet.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.applet.service.parse.dto.ParseCodePlasmidDTO;
import com.bio.drqi.applet.dto.req.ScanCodeSampleTestReqDTO;
import com.bio.drqi.applet.dto.req.ScanCodeTransformReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodePlasmidRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeTransformRspDTO;
import com.bio.drqi.applet.service.ScanCodeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 扫码
 */
@RestController
@RequestMapping("scan")
public class ScanCodeController {

    @Resource
    private ScanCodeService scanCodeService;


    @GetMapping("scanCode")
    public ResponseResult<Object> scanCode(String code){
        return ResponseResult.getSuccess(scanCodeService.scanCode(code));
    }



    /**
     * 质粒标签扫码
     * @param parseCodePlasmidDTO
     * @return
     */
    @PostMapping("plasmidDetail")
    @WebLog(desc = "质粒标签扫码")
    public ResponseResult<ScanCodePlasmidRspDTO> plasmidDetail(@RequestBody ParseCodePlasmidDTO parseCodePlasmidDTO) {
        return ResponseResult.getSuccess(scanCodeService.plasmidDetail(parseCodePlasmidDTO));
    }


    /**
     * 转化标签扫码
     * @param scanCodeTransformReqDTO
     * @return
     */
    @PostMapping("transform")
    @WebLog(desc = "转化标签扫码")
    public ResponseResult<ScanCodeTransformRspDTO> transform(@RequestBody  ScanCodeTransformReqDTO scanCodeTransformReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.transform(scanCodeTransformReqDTO));
    }

    /**
     * 取样标签扫码
     * @param scanCodeSampleTestReqDTO
     * @return
     */
    @PostMapping("sampleTest")
    @WebLog(desc = "取样标签扫码")
    public ResponseResult<ScanCodeSampleTestRspDTO> sampleTest(@RequestBody  ScanCodeSampleTestReqDTO scanCodeSampleTestReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.sampleTest(scanCodeSampleTestReqDTO));
    }
    /**
     * 种子扫码
     * @param scanCodeSampleTestReqDTO
     * @return
     */
    @PostMapping("seed")
    @WebLog(desc = "取样标签扫码")
    public ResponseResult<ScanCodeSampleTestRspDTO> seed(@RequestBody  ScanCodeSampleTestReqDTO scanCodeSampleTestReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.sampleTest(scanCodeSampleTestReqDTO));
    }
}
