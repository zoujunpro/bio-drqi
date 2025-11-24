package com.bio.drqi.manage.controller.plant;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;

import com.bio.drqi.manage.plant.req.PlantSingleStockListPageReqDTO;
import com.bio.drqi.manage.plant.req.PlantSingleStockQueryBySampleCodeListReqDTO;
import com.bio.drqi.manage.plant.req.PlantSingleStockQueryListReqDTO;
import com.bio.drqi.manage.plant.rsp.PlantSingleStockListPageRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantSingleStockQueryBySampleCodeListRspDTO;
import com.bio.drqi.manage.plant.rsp.PlantSingleStockQueryListRspDTO;
import com.bio.drqi.manage.service.plant.PlantSingleStockService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * cer苗库管理（有具体种植编号苗库）
 */
@RestController
@RequestMapping("plantSingleStock")
public class PlantSingleStockController {

    @Resource
    private PlantSingleStockService plantSingleStockService;


    /**
     * cer苗库管理（有具体种植编号苗库）-分页查询
     * @param plantSingleStockListPageReqDTO
     * @return
     */

    @PostMapping("/listPage")
    @WebLog(desc = "cer苗库管理（有具体种植编号苗库）-分页查询")
    public ResponseResult<PageInfo<PlantSingleStockListPageRspDTO>> listPage(@RequestBody  PlantSingleStockListPageReqDTO plantSingleStockListPageReqDTO) {
        return ResponseResult.getSuccess(plantSingleStockService.listPage(plantSingleStockListPageReqDTO));
    }

    /**
     * cer苗库管理（有具体种植编号苗库）-条件查询
     * @param plantSingleStockListPageReqDTO
     * @return
     */

    @PostMapping("/queryList")
    @WebLog(desc = "cer苗库管理（有具体种植编号苗库）-条件查询")
    public ResponseResult<List<PlantSingleStockQueryListRspDTO>> queryList(@RequestBody  PlantSingleStockQueryListReqDTO plantSingleStockListPageReqDTO) {
        return ResponseResult.getSuccess(plantSingleStockService.queryList(plantSingleStockListPageReqDTO));
    }

    /**
     * cer苗库管理（有具体种植编号苗库）-根据取样编号批次查询
     * @return
     */
    @PostMapping("/queryBySampleCodeList")
    @WebLog(desc = "cer苗库管理（有具体种植编号苗库）-根据取样编号批次查询")
    public ResponseResult<List<PlantSingleStockQueryBySampleCodeListRspDTO>>  queryBySampleCodeList(@RequestBody @Validated PlantSingleStockQueryBySampleCodeListReqDTO plantSingleStockQueryBySampleCodeListReqDTO){
        return ResponseResult.getSuccess(plantSingleStockService.queryBySampleCodeList(plantSingleStockQueryBySampleCodeListReqDTO));
    }
}
