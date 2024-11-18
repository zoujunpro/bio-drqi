package com.bio.drqi.manage.controller.project;


import com.bio.drqi.base.PrintRspDTO;
import com.bio.drqi.projectPrint.SamplePrintReqDTO;
import com.bio.drqi.projectPrint.TransFormPrintReqDTO;
import com.bio.drqi.projectPrint.VectorBuildPrintReqDTO;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.aspect.RequestLog;
import com.bio.drqi.manage.service.project.ProjectPrintService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 打印
 */
@RestController
@RequestMapping("/projectPrint")
public class ProjectPrintController {

    @Resource
    private ProjectPrintService projectPrintService;

    /**
     * 载体构建打印
     *
     * @return
     */
    @PostMapping("vectorBuildPrint")
    @WebLog(desc = "载体构建打印")
    @RequestLog("载体构建打印")
    public ResponseResult<PrintRspDTO> vectorBuildPrint(@RequestBody @Validated VectorBuildPrintReqDTO vectorBuildPrintReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.vectorBuildPrint(vectorBuildPrintReqDTO));
    }

    /**
     * 转化打印
     *
     * @return
     */
    @PostMapping("transFormPrint")
    @WebLog(desc = "转化打印")
    @RequestLog("转化打印")
    public ResponseResult<PrintRspDTO> transFormPrint(@RequestBody @Validated TransFormPrintReqDTO transFormPrintReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.transFormPrint(transFormPrintReqDTO));
    }


    /**
     * 取样编号打印
     *
     * @return
     */
    @PostMapping("samplePrint")
    @WebLog(desc = "取样编号打印")
    @RequestLog("取样编号打印")
    public ResponseResult<PrintRspDTO> samplePrint(@RequestBody @Validated SamplePrintReqDTO samplePrintReqDTO) {
        return ResponseResult.getSuccess(projectPrintService.samplePrint(samplePrintReqDTO));
    }
    /**
     * 96孔板签
     *
     * @return
     */
    @GetMapping("layoutPrint")
    @WebLog(desc = "96孔板签")
    @RequestLog("96孔板签")
    public ResponseResult<PrintRspDTO> layoutPrint(@RequestParam @Validated String layoutNumber) {
        return ResponseResult.getSuccess(projectPrintService.layoutPrint(layoutNumber));
    }

}
