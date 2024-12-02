package com.bio.drqi.applet.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.applet.dto.rsp.ScanCodeRspDTO;
import com.bio.drqi.applet.service.ScanCodeService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.UUID;

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
    public ResponseResult<ScanCodeRspDTO> scanCode(@RequestParam String code){
        return ResponseResult.getSuccess(scanCodeService.scanCode(code));
    }

    public static void main(String[] args) {
        System.out.println(UUID.randomUUID().toString());
    }

}
