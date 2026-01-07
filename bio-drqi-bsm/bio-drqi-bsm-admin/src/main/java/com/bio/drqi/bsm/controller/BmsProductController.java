package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsProductListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductQueryListRspDTO;
import com.bio.drqi.bsm.service.BmsProductService;
import com.bio.drqi.common.aspect.RequestLog;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 材料管理
 */
@RestController
@RequestMapping("/product")
public class BmsProductController {

    @Resource
    private BmsProductService bmsProductService;

    /**
     * 材料管理-分页查询
     *
     * @param bmsProductListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "材料管理-分页查询")
    @RequirePermissions("bms:product:listPage")
    public ResponseResult<PageInfo<BmsProductListPageRspDTO>> listPage(@RequestBody @Validated BmsProductListPageReqDTO bmsProductListPageReqDTO) {
        return ResponseResult.getSuccess(bmsProductService.listPage(bmsProductListPageReqDTO));
    }


    /**
     * 材料管理-查询所有材料名称
     *
     * @return
     */
    @GetMapping("/listAllProductName")
    @WebLog(desc = "材料管理-查询所有材料名称")
    public ResponseResult<List<String>> listAllProductName() {
        return ResponseResult.getSuccess(bmsProductService.listAllProductName());
    }

    /**
     * 材料管理-查询
     *
     * @return
     */
    @PostMapping("/queryList")
    @WebLog(desc = "材料管理-查询")
    public ResponseResult<List<BmsProductQueryListRspDTO>> queryList(@RequestBody @Validated BmsProductQueryListReqDTO bmsProductQueryListReqDTO) {
        return ResponseResult.getSuccess(bmsProductService.queryList(bmsProductQueryListReqDTO));
    }

    /**
     * 材料管理-导出全部
     *
     * @return
     */
    @PostMapping("/exportExcel")
    @WebLog(desc = "材料管理-导出全部")
    @RequestLog("材料管理-导出全部")
    public void exportExcel(@RequestBody @Validated BmsProductExportExcelReqDTO bmsProductExportExcelReqDTO) {
        bmsProductService.exportExcel(bmsProductExportExcelReqDTO);
    }


    /**
     * 材料管理-添加
     *
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "材料管理-添加")
    @RequirePermissions("bms:product:add")
    @RequestLog("材料管理-添加")
    public ResponseResult<String> add(@RequestBody @Validated BmsProductAddReqDTO bmsProductAddReqDTO) {
        bmsProductService.add(bmsProductAddReqDTO);
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 材料管理-禁用
     *
     * @return
     */
    @GetMapping("/disable")
    @WebLog(desc = "材料管理-禁用")
    @RequirePermissions("bms:product:disable")
    @RequestLog("材料管理-禁用")
    public ResponseResult<String> disable(@RequestParam @Validated @NotNull Integer id) {
        bmsProductService.disable(id);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 材料管理-启用
     *
     * @return
     */
    @GetMapping("/enable")
    @WebLog(desc = "材料管理-启用")
    @RequirePermissions("bms:product:enable")
    @RequestLog("材料管理-启用")
    public ResponseResult<String> enable(@RequestParam @Validated @NotNull Integer id) {
        bmsProductService.enable(id);
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 材料管理-编辑
     *
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "材料管理-编辑")
    @RequirePermissions("bms:product:edit")
    @RequestLog("材料管理-编辑")
    public ResponseResult<String> edit(@RequestBody @Validated BmsProductEditReqDTO bmsProductEditReqDTO) {
        bmsProductService.edit(bmsProductEditReqDTO);
        return ResponseResult.getSuccess("ok");
    }
}
