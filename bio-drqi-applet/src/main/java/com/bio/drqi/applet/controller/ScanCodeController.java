package com.bio.drqi.applet.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.applet.service.parse.dto.PlasmidUniqueCodeDTO;
import com.bio.drqi.applet.service.parse.dto.SampleTestUniqueReqDTO;
import com.bio.drqi.applet.service.parse.dto.TransformUniqueCodeDTO;
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
     * @param plasmidUniqueCodeDTO
     * @return
     */
    @PostMapping("plasmidDetail")
    @WebLog(desc = "质粒标签扫码")
    public ResponseResult<ScanCodePlasmidRspDTO> plasmidDetail(@RequestBody PlasmidUniqueCodeDTO plasmidUniqueCodeDTO) {
        return ResponseResult.getSuccess(scanCodeService.plasmidDetail(plasmidUniqueCodeDTO));
    }


    /**
     * 转化标签扫码
     * @param transformUniqueCodeDTO
     * @return
     */
    @PostMapping("transform")
    @WebLog(desc = "转化标签扫码")
    public ResponseResult<ScanCodeTransformRspDTO> transform(@RequestBody TransformUniqueCodeDTO transformUniqueCodeDTO) {
        return ResponseResult.getSuccess(scanCodeService.transform(transformUniqueCodeDTO));
    }

    /**
     * 取样标签扫码
     * @param sampleTestUniqueReqDTO
     * @return
     */
    @PostMapping("sampleTest")
    @WebLog(desc = "取样标签扫码")
    public ResponseResult<ScanCodeSampleTestRspDTO> sampleTest(@RequestBody SampleTestUniqueReqDTO sampleTestUniqueReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.sampleTest(sampleTestUniqueReqDTO));
    }
    /**
     * 种子扫码
     * @param sampleTestUniqueReqDTO
     * @return
     */
    @PostMapping("seed")
    @WebLog(desc = "取样标签扫码")
    public ResponseResult<ScanCodeSampleTestRspDTO> seed(@RequestBody SampleTestUniqueReqDTO sampleTestUniqueReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.sampleTest(sampleTestUniqueReqDTO));
    }
}
