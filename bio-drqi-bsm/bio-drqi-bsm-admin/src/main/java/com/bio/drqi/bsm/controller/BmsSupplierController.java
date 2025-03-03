package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;

import com.bio.drqi.bsm.req.BmsSupplierAddReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierExportExcelReqDTO;
import com.bio.drqi.bsm.req.BmsSupplierListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsSupplierListPageRspDTO;
import com.bio.drqi.bsm.service.BmsSupplierService;
import com.github.pagehelper.PageInfo;
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
    public ResponseResult<PageInfo<BmsSupplierListPageRspDTO>> listPage(@RequestBody BmsSupplierListPageReqDTO bmsSupplierListPageReqDTO) {
        return ResponseResult.getSuccess(bmsSupplierService.listPage(bmsSupplierListPageReqDTO));
    }

    /**
     * 供应商管理-查询全部
     *
     * @return
     */
    @GetMapping("/listALl")
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
    public ResponseResult<String> add(@RequestBody BmsSupplierAddReqDTO bmsSupplierAddReqDTO) {
        bmsSupplierService.add(bmsSupplierAddReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 供应商管理-删除
     *
     * @param id
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "供应商管理-删除")
    public ResponseResult<String> delete(@RequestParam Integer id) {
        bmsSupplierService.delete(id);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 供应商管理-导出
     *
     * @return
     */
    @PostMapping("/exportExcel")
    @WebLog(desc = "供应商管理-导出")
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
    public void importExcel() {

    }
}
