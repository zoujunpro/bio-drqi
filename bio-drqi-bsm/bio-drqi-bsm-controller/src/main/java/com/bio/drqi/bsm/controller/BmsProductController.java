package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.dto.req.*;
import com.bio.drqi.bsm.dto.rsp.BmsProductListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;

/**
 * 商品管理
 */
@RestController
@RequestMapping("/product")
public class BmsProductController {


    /**
     * 商品管理-分页查询
     * @param bmsProductListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "商品管理-分页查询")
    public ResponseResult<PageInfo<BmsProductListPageRspDTO>> listPage(@RequestBody @Validated BmsProductListPageReqDTO bmsProductListPageReqDTO) {
        return null;
    }

    /**
     * 商品管理-查询
     * @return
     */
    @PostMapping("/list")
    @WebLog(desc = "商品管理-查询")
    public ResponseResult<String> list(@RequestBody @Validated BmsProductListReqDTO bmsProductListReqDTO) {
        return null;
    }

    /**
     * 商品管理-导出全部
     * @return
     */
    @PostMapping("/exportExcel")
    @WebLog(desc = "商品管理-导出全部")
    public void exportExcel(@RequestBody @Validated BmsProductExportExcelReqDTO bmsProductExportExcelReqDTO) {
    }


    /**
     * 商品管理-添加
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "商品管理-添加")
    public ResponseResult<String> add(@RequestBody @Validated BmsProductAddReqDTO bmsProductAddReqDTO) {
        return null;
    }


    /**
     * 商品管理-删除
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "商品管理-删除")
    public ResponseResult<String> delete(@RequestParam @Validated @NotNull Integer id) {
        return null;
    }


    /**
     * 商品管理-编辑
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "商品管理-编辑")
    public ResponseResult<String> edit(@RequestBody @Validated BmsProductEditReqDTO bmsProductEditReqDTO) {
        return null;
    }
}
