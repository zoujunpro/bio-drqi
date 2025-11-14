package com.bio.drqi.plant.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.plant.req.PlantExperimentReqDTO;
import com.bio.drqi.plant.rsp.PlantExperimentRspDTO;
import com.bio.drqi.plant.service.PlantExperimentService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * CER试验管理
 */
@RestController
@RequestMapping("plantExperiment")
public class PlantExperimentController {

    @Resource
    private PlantExperimentService plantExperimentService;

    /**
     * CER试验管理-分页查询
     *
     * @param plantExperimentReqDTO
     * @return
     */
    @GetMapping("/listPage")
    @WebLog(desc = "CER试验管理-分页查询")
    public ResponseResult<PageInfo<PlantExperimentRspDTO>> listPage(@RequestBody PlantExperimentReqDTO plantExperimentReqDTO) {

        return ResponseResult.getSuccess(plantExperimentService.listPage(plantExperimentReqDTO));
    }

    /**
     * CER试验管理-下载模板
     *
     * @param httpServletResponse
     */
    @GetMapping("/downloadTemplate")
    @WebLog(desc = "CER试验管理-下载模板")
    public void downloadTemplate(HttpServletResponse httpServletResponse) {
        plantExperimentService.downloadTemplate(httpServletResponse);
    }


}
