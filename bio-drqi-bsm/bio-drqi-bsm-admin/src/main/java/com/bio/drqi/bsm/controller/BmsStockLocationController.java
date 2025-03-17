package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.rsp.BmsStockLocationQueryByUnitRspDTO;
import com.bio.drqi.bsm.service.BmsStockLocationService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 库存管理
 */
@RestController
@RequestMapping("/bmsStockLocation")
public class BmsStockLocationController {

    @Resource
    private BmsStockLocationService bmsStockLocationService;

    /**
     * 库存管理-根据单位查询库位信息
     * @param unitCode
     * @return
     */
    @GetMapping("queryByUnit")
    @WebLog(desc = "库存管理-根据单位查询库位信息")
    public ResponseResult<List<BmsStockLocationQueryByUnitRspDTO>> queryByUnit(@RequestParam String unitCode) {
        return ResponseResult.getSuccess(bmsStockLocationService.queryByUnit(unitCode));

    }

}
