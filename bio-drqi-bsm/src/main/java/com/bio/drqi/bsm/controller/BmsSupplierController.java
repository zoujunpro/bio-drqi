package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.dto.req.BmsSupplierAddReqDTO;
import com.bio.drqi.bsm.dto.req.BmsSupplierListPageReqDTO;
import com.bio.drqi.bsm.dto.rsp.BmsSupplierListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

/**
 * 供应商管理
 */
@RestController
@RequestMapping("/supplier")
public class BmsSupplierController {


    /**
     * 供应商管理-分页查询
     * @param bmsSupplierListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "供应商管理-分页查询")
    public ResponseResult<PageInfo<BmsSupplierListPageRspDTO>> listPage(@RequestBody BmsSupplierListPageReqDTO bmsSupplierListPageReqDTO){
            return null;
    }

    /**
     * 供应商管理-新增
     * @param bmsSupplierAddReqDTO
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "供应商管理-新增")
    public ResponseResult<String> add(@RequestBody BmsSupplierAddReqDTO bmsSupplierAddReqDTO){
        return null;
    }
    /**
     * 供应商管理-删除
     * @param id
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "供应商管理-删除")
    public ResponseResult<String> delete(@RequestParam Integer id){
        return null;
    }
}
