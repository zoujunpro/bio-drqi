package com.bio.drqi.applet.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.applet.dto.req.QueryByPlantCodeReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeT0PlantTestRspDTO;
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

    /**
     * 根据种植编号查询信息
     * @param queryByPlantCodeReqDTO
     * @return
     */
    @PostMapping("/queryByPlantCode")
    public ResponseResult<ScanCodeT0PlantTestRspDTO> queryByPlantCode(@RequestBody QueryByPlantCodeReqDTO queryByPlantCodeReqDTO) {
        return ResponseResult.getSuccess(scanCodeService.queryByPlantCode(queryByPlantCodeReqDTO));
    }

}
