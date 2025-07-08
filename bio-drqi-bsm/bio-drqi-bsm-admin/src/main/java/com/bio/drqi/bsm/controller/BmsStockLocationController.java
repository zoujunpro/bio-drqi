package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsStockLocationAddReqDTO;
import com.bio.drqi.bsm.req.BmsStockLocationEditReqDTO;
import com.bio.drqi.bsm.req.BmsStockLocationListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockLocationListAllStockRspDTO;
import com.bio.drqi.bsm.rsp.BmsStockLocationListPageRspDTO;
import com.bio.drqi.bsm.service.BmsStockLocationService;
import com.bio.drqi.common.aspect.RequestLog;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 库位管理
 */
@RestController
@RequestMapping("/bmsStockLocation")
public class BmsStockLocationController {

    @Resource
    private BmsStockLocationService bmsStockLocationService;


    /**
     * 库房管理-根据单位查询库位信息
     *
     * @param unitCode
     * @return
     */
    @GetMapping("queryByUnit")
    @WebLog(desc = "库存管理-根据单位查询库位信息")
    public ResponseResult<List<BmsStockLocationQueryByUnitRspDTO>> queryByUnit(@RequestParam String unitCode) {
        return ResponseResult.getSuccess(bmsStockLocationService.queryByUnit(unitCode));

    }



    /**
     * 库位管理-分页查询
     *
     * @param bmsStockLocationListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "库房管理-分页查询")
    @RequirePermissions("bms:bmsStockLocation:listPage")
    public ResponseResult<PageInfo<BmsStockLocationListPageRspDTO>> listPage(@RequestBody BmsStockLocationListPageReqDTO bmsStockLocationListPageReqDTO) {
        return ResponseResult.getSuccess(bmsStockLocationService.listPage(bmsStockLocationListPageReqDTO));
    }

    /**
     * 库位管理-新增
     *
     * @param bmsStockLocationAddReqDTO
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "库房管理-新增")
    @RequirePermissions("bms:bmsStockLocation:add")
    @RequestLog("库房管理-新增")
    public ResponseResult<String> add(@RequestBody @Validated BmsStockLocationAddReqDTO bmsStockLocationAddReqDTO) {
        bmsStockLocationService.add(bmsStockLocationAddReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 库位管理-删除
     *
     * @param id
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "库房管理-删除")
    @RequestLog("库房管理-删除")
    @RequirePermissions("bms:bmsStockLocation:delete")
    public ResponseResult<String> delete(Integer id) {
        bmsStockLocationService.delete(id);
        return ResponseResult.getSuccess("ok");

    }

    /**
     * 库位管理-编辑(暂时不做)
     *
     * @param bmsStockLocationEditReqDTO
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "库房管理-编辑")
    @RequirePermissions("bms:bmsStockLocation:edit")
    @RequestLog("库房管理-编辑")
    public ResponseResult edit(@RequestBody BmsStockLocationEditReqDTO bmsStockLocationEditReqDTO) {
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 库位管理-查询所有库房
     *
     * @return
     */
    @GetMapping("/listAllStock")
    @WebLog(desc = "库房管理-查询所有库房")
    public ResponseResult<List<BmsStockLocationListAllStockRspDTO>> listAllStock() {
        return ResponseResult.getSuccess(bmsStockLocationService.listAllStock());
    }

}
