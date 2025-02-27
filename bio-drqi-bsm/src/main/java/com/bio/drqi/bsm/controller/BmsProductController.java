package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.dto.req.BmsProductAddReqDTO;
import com.bio.drqi.bsm.dto.req.BmsProductEditReqDTO;
import com.bio.drqi.bsm.dto.req.BmsProductListPageReqDTO;
import com.bio.drqi.bsm.dto.req.BmsProductListReqDTO;
import com.bio.drqi.bsm.dto.rsp.BmsProductListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseResult<PageInfo<BmsProductListPageRspDTO>> listPage(BmsProductListPageReqDTO bmsProductListPageReqDTO) {
        return null;
    }

    /**
     * 商品管理-查询
     * @return
     */
    @GetMapping("/list")
    @WebLog(desc = "商品管理-查询")
    public ResponseResult<String> list(BmsProductListReqDTO bmsProductListReqDTO) {
        return null;
    }

    /**
     * 商品管理-导出全部
     * @return
     */
    @GetMapping("/exportExcel")
    @WebLog(desc = "商品管理-导出全部")
    public void exportExcel() {
    }


    /**
     * 商品管理-添加
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "商品管理-添加")
    public ResponseResult<String> add(BmsProductAddReqDTO bmsProductAddReqDTO) {
        return null;
    }


    /**
     * 商品管理-删除
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "商品管理-删除")
    public ResponseResult<String> delete() {
        return null;
    }


    /**
     * 商品管理-编辑
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "商品管理-编辑")
    public ResponseResult<String> edit(BmsProductEditReqDTO bmsProductEditReqDTO) {
        return null;
    }
}
