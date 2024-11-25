package com.bio.drqi.applet.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.applet.dto.req.SeedlingRemainReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingRemoveReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingReportReqDTO;
import com.bio.drqi.applet.service.SeedlingService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/***
 * 苗操作相关接口
 */
@RestController
@RequestMapping("/seedling")
public class SeedlingController {

    @Resource
    private SeedlingService seedlingService;

    /**
     * 保苗
     *
     * @return
     */
    @PostMapping("remain")
    public ResponseResult<String> remain(@RequestBody SeedlingRemainReqDTO seedlingRemainReqDTO) {
        seedlingService.remain(seedlingRemainReqDTO);
        return ResponseResult.getSuccess(null);
    }


    /**
     * 剔苗
     *
     * @return
     */
    @PostMapping("remove")
    public ResponseResult<String> remove(@RequestBody SeedlingRemoveReqDTO seedlingRemoveReqDTO) {
        seedlingService.remove(seedlingRemoveReqDTO);
        return ResponseResult.getSuccess(null);
    }

    /**
     * 苗报备
     *
     * @return
     */
    @PostMapping("report")
    public ResponseResult<String> report(@RequestBody SeedlingReportReqDTO seedlingReportReqDTO) {
        seedlingService.report(seedlingReportReqDTO);
        return ResponseResult.getSuccess(null);
    }

}
