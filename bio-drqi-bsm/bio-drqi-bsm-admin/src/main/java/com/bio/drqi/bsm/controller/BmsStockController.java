package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsStockQueryByUnitRspDTO;
import com.bio.drqi.bsm.service.BmsStockService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 库房管理
 */
@RestController
@RequestMapping("/bmsStock")
public class BmsStockController {

    @Resource
    private BmsStockService bmsStockService;


    /**
     * 库房管理-根据单位查询
     * @param unitCode
     * @return
     */
    @GetMapping("/queryByUnit")
    @WebLog(desc = "库房管理-根据单位查询")
    public ResponseResult<List<BmsStockQueryByUnitRspDTO>> queryByUnit(@RequestParam String unitCode) {
        return ResponseResult.getSuccess(bmsStockService.queryStockByUnit(unitCode));
    }


    /**
     * 库房管理-新增库房
     * @param bmsStockAddReqDTO
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "库房管理-新增库房")
    public ResponseResult<String> add(@RequestBody BmsStockAddReqDTO bmsStockAddReqDTO) {
        bmsStockService.add(bmsStockAddReqDTO);
        return ResponseResult.getSuccess("ok");

    }

    /**
     * 库房管理-编辑库房
     * @param bmsStockEditReqDTO
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "库房管理-编辑库房")
    public ResponseResult<String> edit(@RequestBody BmsStockEditReqDTO bmsStockEditReqDTO) {
        bmsStockService.edit(bmsStockEditReqDTO);
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 库房管理-删除库房
     * @param id
     * @return
     */
    @PostMapping("/delete")
    @WebLog(desc = "库房管理-删除库房")
    public ResponseResult<String> delete(@RequestParam Integer id) {
        bmsStockService.delete(id);
        return ResponseResult.getSuccess("ok");
    }
}
