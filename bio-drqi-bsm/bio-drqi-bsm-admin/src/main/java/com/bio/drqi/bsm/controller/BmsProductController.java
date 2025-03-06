package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsProductListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductQueryListRspDTO;
import com.bio.drqi.bsm.service.BmsProductService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 商品管理
 */
@RestController
@RequestMapping("/product")
public class BmsProductController {

    @Resource
    private BmsProductService bmsProductService;

    /**
     * 商品管理-分页查询
     *
     * @param bmsProductListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "商品管理-分页查询")
    public ResponseResult<PageInfo<BmsProductListPageRspDTO>> listPage(@RequestBody @Validated BmsProductListPageReqDTO bmsProductListPageReqDTO) {
        return ResponseResult.getSuccess(bmsProductService.listPage(bmsProductListPageReqDTO));
    }

    /**
     * 商品管理-查询
     *
     * @return
     */
    @PostMapping("/queryList")
    @WebLog(desc = "商品管理-查询")
    public ResponseResult<List<BmsProductQueryListRspDTO>> queryList(@RequestBody @Validated BmsProductQueryListReqDTO bmsProductQueryListReqDTO) {
        return ResponseResult.getSuccess(bmsProductService.queryList(bmsProductQueryListReqDTO));
    }

    /**
     * 商品管理-导出全部
     *
     * @return
     */
    @PostMapping("/exportExcel")
    @WebLog(desc = "商品管理-导出全部")
    public void exportExcel(@RequestBody @Validated BmsProductExportExcelReqDTO bmsProductExportExcelReqDTO) {
        bmsProductService.exportExcel(bmsProductExportExcelReqDTO);
    }


    /**
     * 商品管理-添加
     *
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "商品管理-添加")
    public ResponseResult<String> add(@RequestBody @Validated BmsProductAddReqDTO bmsProductAddReqDTO) {
        bmsProductService.add(bmsProductAddReqDTO);
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 商品管理-删除
     *
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "商品管理-删除")
    public ResponseResult<String> delete(@RequestParam @Validated @NotNull Integer id) {
        bmsProductService.delete(id);
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 商品管理-编辑
     *
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "商品管理-编辑")
    public ResponseResult<String> edit(@RequestBody @Validated BmsProductEditReqDTO bmsProductEditReqDTO) {
        bmsProductService.edit(bmsProductEditReqDTO);
        return ResponseResult.getSuccess("ok");
    }
}
