package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsProductTypeAddReqDTO;
import com.bio.drqi.bsm.req.BmsProductTypeEditReqDTO;
import com.bio.drqi.bsm.req.BmsProductTypeListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsProductTyListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductTyListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductTypeService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 库存类型管理
 */
@RestController
@RequestMapping("/productType")
public class BmsProductTypeController {

    @Resource
    private BmsProductTypeService bmsProductTypeService;

    /**
     * 库存类型管理-分页查询
     * @param bmsProductTypeListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "库存类型管理-分页查询")
    public ResponseResult<PageInfo<BmsProductTyListPageRspDTO>> listPage(@RequestBody BmsProductTypeListPageReqDTO bmsProductTypeListPageReqDTO) {
        return ResponseResult.getSuccess(bmsProductTypeService.listPage(bmsProductTypeListPageReqDTO));
    }

    /**
     * 库存类型管理-查询所有
     * @return
     */
    @GetMapping("/listAll")
    @WebLog(desc = "库存类型管理-查询所有")
    public ResponseResult<List<BmsProductTyListAllRspDTO>> listAll() {
        return ResponseResult.getSuccess(bmsProductTypeService.listAll());
    }

    /**
     * 库存类型管理-新增
     * @param bmsProductTypeAddReqDTO
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "库存类型管理-新增")
    public ResponseResult<String> add(@RequestBody BmsProductTypeAddReqDTO bmsProductTypeAddReqDTO) {
        bmsProductTypeService.add(bmsProductTypeAddReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 库存类型管理-删除
     * @param id
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "库存类型管理-删除")
    public ResponseResult<String> delete(@RequestParam Integer id) {
        bmsProductTypeService.delete(id);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 库存类型管理-编辑
     * @param bmsProductTypeEditReqDTO
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "库存类型管理-编辑")
    public ResponseResult<String> edit(@RequestBody BmsProductTypeEditReqDTO bmsProductTypeEditReqDTO) {
        bmsProductTypeService.edit(bmsProductTypeEditReqDTO);
        return ResponseResult.getSuccess("ok");
    }
}
