package com.bio.drqi.bsm.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.dto.BmsCountPeriodTaskDTO;
import com.bio.drqi.bsm.req.BmsStockAddReqDTO;
import com.bio.drqi.bsm.req.BmsStockEditReqDTO;
import com.bio.drqi.bsm.rsp.BmsStockQueryByUnitRspDTO;
import com.bio.drqi.bsm.service.BmsStockService;
import com.bio.drqi.common.aspect.RequestLog;
import com.bio.drqi.domain.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     *
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
     *
     * @param bmsStockAddReqDTO
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "库房管理-新增库房")
    @RequirePermissions("bms:stock:add")
    @RequestLog("库房管理-新增库房")
    public ResponseResult<String> add(@RequestBody @Validated BmsStockAddReqDTO bmsStockAddReqDTO) {
        bmsStockService.add(bmsStockAddReqDTO);
        return ResponseResult.getSuccess("ok");

    }


    /**
     * 库房管理-编辑库房
     *
     * @param bmsStockEditReqDTO
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "库房管理-编辑库房")
    @RequirePermissions("bms:stock:edit")
    @RequestLog("库房管理-编辑库房")
    public ResponseResult<String> edit(@RequestBody BmsStockEditReqDTO bmsStockEditReqDTO) {
        bmsStockService.edit(bmsStockEditReqDTO);
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 库房管理-删除库房
     *
     * @param id
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "库房管理-删除库房")
    @RequirePermissions("bms:stock:delete")
    @RequestLog("库房管理-删除库房")
    public ResponseResult<String> delete(@RequestParam Integer id) {
        bmsStockService.delete(id);
        return ResponseResult.getSuccess("ok");
    }



    @GetMapping("/downJieCunStockExcel")
    @Transactional(rollbackFor = Exception.class)
    public void downJieCunStockExcel( @RequestParam String dateTime,HttpServletResponse httpServletResponse) {
        bmsStockService.downJieCunStockExcel(dateTime,httpServletResponse);
    }

}
