package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;

import com.bio.drqi.bsm.req.BmsSupplierAddReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierEditReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierExportExcelReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsBrandDetailRspDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierQueryByBrandCodeRspDTO;
import com.bio.drqi.bsm.service.BmsSupplierService;
import com.bio.drqi.common.aspect.RequestLog;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 供应商管理
 */
@RestController
@RequestMapping("/supplier")
public class BmsSupplierController {

    @Resource
    private BmsSupplierService bmsSupplierService;

    /**
     * 供应商管理-分页查询
     *
     * @param bmsSupplierListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "供应商管理-分页查询")
    @RequirePermissions("bms:supplier:listPage")
    public ResponseResult<PageInfo<BmsSupplierListPageRspDTO>> listPage(@RequestBody BmsSupplierListPageReqDTO bmsSupplierListPageReqDTO) {
        return ResponseResult.getSuccess(bmsSupplierService.listPage(bmsSupplierListPageReqDTO));
    }


    /**
     * 供应商管理-查询全部
     *
     * @return
     */
    @GetMapping("/listAll")
    @WebLog(desc = "供应商管理-查询全部")
    public ResponseResult<List<BmsSupplierListAllRspDTO>> listALl() {
        return ResponseResult.getSuccess(bmsSupplierService.listALl());
    }

    /**
     * 供应商管理-新增
     *
     * @param bmsSupplierAddReqDTO
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "供应商管理-新增")
    @RequirePermissions("bms:supplier:add")
    @RequestLog("供应商管理-新增")
    public ResponseResult<String> add(@RequestBody BmsSupplierAddReqDTO bmsSupplierAddReqDTO) {
        bmsSupplierService.add(bmsSupplierAddReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 供应商管理-编辑
     *
     * @param bmsSupplierEditReqDTO
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "供应商管理-编辑")
    @RequirePermissions("bms:supplier:edit")
    @RequestLog("供应商管理-编辑")
    public ResponseResult<String> edit(@RequestBody BmsSupplierEditReqDTO  bmsSupplierEditReqDTO) {
        bmsSupplierService.edit(bmsSupplierEditReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 供应商管理-详情
     *
     * @return
     */
    @GetMapping("/detail")
    @WebLog(desc = "供应商管理-详情")
    @RequirePermissions("bms:supplier:detail")
    public ResponseResult<BmsBrandDetailRspDTO> detail(@RequestParam Integer id ) {
        return ResponseResult.getSuccess(bmsSupplierService.detail(id));
    }

    /**
     * 供应商管理-禁用
     *
     * @param id
     * @return
     */
    @GetMapping("/disable")
    @WebLog(desc = "供应商管理-禁用")
    @RequirePermissions("bms:supplier:disable")
    @RequestLog("库存明细管理-禁用")
    public ResponseResult<String> delete(@RequestParam Integer id) {
        bmsSupplierService.disable(id);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 供应商管理-启用
     *
     * @param id
     * @return
     */
    @GetMapping("/enable")
    @WebLog(desc = "供应商管理-启用")
    @RequestLog("库存明细管理-启用")
    public ResponseResult<String> enable(Integer id){
        bmsSupplierService.enable(id);
        return ResponseResult.getSuccess("ok");
    }
    /**
     * 供应商管理-导出
     *
     * @return
     */
    @PostMapping("/exportExcel")
    @WebLog(desc = "供应商管理-导出")
    @RequirePermissions("bms:supplier:exportExcel")
    public void exportExcel(@RequestBody BmsSupplierExportExcelReqDTO bmsSupplierExportExcelReqDTO) {
        bmsSupplierService.exportExcel(bmsSupplierExportExcelReqDTO);
    }

    /**
     * 供应商管理-导入
     *
     * @return
     */
    @GetMapping("/exportExcel")
    @WebLog(desc = "供应商管理-导入")
    @RequirePermissions("bms:supplier:importExcel")
    public void importExcel() {
        bmsSupplierService.importExcel();
    }
}
