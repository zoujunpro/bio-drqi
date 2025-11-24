package com.bio.drqi.manage.controller.plant;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;

import com.bio.drqi.manage.plant.req.PlantMultipleStockListPageReqDTO;
import com.bio.drqi.manage.plant.req.PlantMultipleStockQueryListForTaskReqDTO;
import com.bio.drqi.manage.plant.req.PlantMultipleStockQueryListReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantMultipleStockListPageRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantMultipleStockQueryListForTaskRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantMultipleStockQueryListRspDTO;
import com.bio.drqi.manage.service.plant.PlantMultipleStockService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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
    public ResponseResult<List<PlantMultipleStockQueryListRspDTO>> queryList(PlantMultipleStockQueryListReqDTO plantMultipleStockQueryListReqDTO) {
        return ResponseResult.getSuccess(plantMultipleStockService.queryList(plantMultipleStockQueryListReqDTO));
    }

    /**
     * cer苗库管理（无具体种植编号苗库）-根据来源查询取样检测首次取样可选条件
     *
     * @param plantMultipleStockQueryListForTaskReqDTO
     * @return
     */
    @PostMapping("/queryListForTask")
    @WebLog(desc = "cer苗库管理（无具体种植编号苗库）-根据来源查询取样检测首次取样可选条件")
    public ResponseResult<List<PlantMultipleStockQueryListForTaskRspDTO>> queryListForTask(PlantMultipleStockQueryListForTaskReqDTO plantMultipleStockQueryListForTaskReqDTO) {
        return ResponseResult.getSuccess(plantMultipleStockService.queryListForTask(plantMultipleStockQueryListForTaskReqDTO));
    }


}
