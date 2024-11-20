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

    /**
     *  扫码
     * @param code
     * @return
     */

    @GetMapping("scanCode")
    public ResponseResult<Object> scanCode(String code){
        return ResponseResult.getSuccess(scanCodeService.scanCode(code));
    }

}
