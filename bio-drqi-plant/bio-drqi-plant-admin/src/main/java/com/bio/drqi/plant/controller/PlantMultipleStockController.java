package com.bio.drqi.plant.controller;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.plant.req.PlantMultipleStockListPageReqDTO;
import com.bio.drqi.plant.req.PlantMultipleStockQueryListReqDTO;
import com.bio.drqi.plant.rsp.PlantMultipleStockListPageRspDTO;
import com.bio.drqi.plant.rsp.PlantMultipleStockQueryListRspDTO;
import com.bio.drqi.plant.service.PlantMultipleStockService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * cer苗库管理（无具体种植编号苗库）
 */
@RestController
@RequestMapping("plantMultipleStock")
public class PlantMultipleStockController {

    @Resource
    private PlantMultipleStockService plantMultipleStockService;

    /**
     * cer苗库管理（无具体种植编号苗库）-分页查询
     *
     * @param plantMultipleStockListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "cer苗库管理（无具体种植编号苗库）-分页查询")
    public ResponseResult<PageInfo<PlantMultipleStockListPageRspDTO>> listPage(PlantMultipleStockListPageReqDTO plantMultipleStockListPageReqDTO) {
        return ResponseResult.getSuccess(plantMultipleStockService.listPage(plantMultipleStockListPageReqDTO));
    }

    /**
     * cer苗库管理（无具体种植编号苗库）-条件查询
     *
     * @param plantMultipleStockQueryListReqDTO
     * @return
     */
    @PostMapping("/queryList")
    @WebLog(desc = "cer苗库管理（无具体种植编号苗库）-条件查询")
    public ResponseResult<PageInfo<PlantMultipleStockQueryListRspDTO>> queryList(PlantMultipleStockQueryListReqDTO plantMultipleStockQueryListReqDTO) {
        return ResponseResult.getSuccess(plantMultipleStockService.queryList(plantMultipleStockQueryListReqDTO));
    }

}
