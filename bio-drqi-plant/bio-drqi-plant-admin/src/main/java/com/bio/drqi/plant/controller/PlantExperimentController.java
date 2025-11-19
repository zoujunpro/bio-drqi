package com.bio.drqi.plant.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.plant.req.PlantExperimentListPageDetailReqDTO;
import com.bio.drqi.plant.req.PlantExperimentListPageReqDTO;
import com.bio.drqi.plant.rsp.PlantExperimentListPageDetailRspDTO;
import com.bio.drqi.plant.rsp.PlantExperimentListPageRspDTO;
import com.bio.drqi.plant.service.PlantExperimentService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

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
     * @param plantExperimentListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "CER试验管理-分页查询")
    public ResponseResult<PageInfo<PlantExperimentListPageRspDTO>> listPage(@RequestBody PlantExperimentListPageReqDTO plantExperimentListPageReqDTO) {
        return ResponseResult.getSuccess(plantExperimentService.listPage(plantExperimentListPageReqDTO));
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

    /**
     * CER试验管理-分页查询试验详情
     *
     * @param plantExperimentListPageDetailReqDTO
     * @return
     */
    @PostMapping("/listPageDetail")
    @WebLog(desc = "CER试验管理-分页查询试验详情")
    public ResponseResult<PageInfo<PlantExperimentListPageDetailRspDTO>> listPageDetail(@RequestBody PlantExperimentListPageDetailReqDTO plantExperimentListPageDetailReqDTO) {
        return ResponseResult.getSuccess(plantExperimentService.listPageDetail(plantExperimentListPageDetailReqDTO));
    }

}
