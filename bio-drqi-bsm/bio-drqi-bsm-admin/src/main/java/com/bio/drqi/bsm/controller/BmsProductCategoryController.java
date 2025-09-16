package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsProductCategoryListAllRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductCategoryListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsProductTyListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProductCategoryService;
import com.bio.drqi.bsm.service.BmsProductTypeService;
import com.bio.drqi.common.aspect.RequestLog;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 材料类别管理
 */
@RestController
@RequestMapping("/productCategory")
public class BmsProductCategoryController {

    @Resource
    private BmsProductCategoryService bmsProductCategoryService;


    /**
     * 材料类别管理-分页查询
     * @param bmsProductCategoryListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "材料类别管理-分页查询")
    @RequirePermissions("bms:productCategory:listPage")
    public ResponseResult<PageInfo<BmsProductCategoryListPageRspDTO>> listPage(@RequestBody BmsProductCategoryListPageReqDTO bmsProductCategoryListPageReqDTO) {
        return ResponseResult.getSuccess(bmsProductCategoryService.listPage(bmsProductCategoryListPageReqDTO));
    }


    /**
     * 材料类别管理-查询所有
     * @return
     */
    @GetMapping("/listAll")
    @WebLog(desc = "材料类别管理-查询所有")
    public ResponseResult<List<BmsProductCategoryListAllRspDTO>> listAll() {
        return ResponseResult.getSuccess(bmsProductCategoryService.listAll());
    }


    /**
     * 材料类别管理-新增
     * @param bmsProductCategoryAddReqDTO
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "材料类别管理-新增")
    @RequirePermissions("bms:productCategory:add")
    @RequestLog("材料管理-导出全部")
    public ResponseResult<String> add(@RequestBody BmsProductCategoryAddReqDTO bmsProductCategoryAddReqDTO) {
        bmsProductCategoryService.add(bmsProductCategoryAddReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 材料类别管理-删除
     * @param id
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "材料类别管理-删除")
    @RequirePermissions("bms:productCategory:delete")
    @RequestLog("材料类别管理-删除")
    public ResponseResult<String> delete(@RequestParam Integer id) {
        bmsProductCategoryService.delete(id);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 材料类别管理-编辑
     * @param bmsProductCategoryEditReqDTO
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "材料类别管理-编辑")
    @RequirePermissions("bms:productCategory:edit")
    @RequestLog("材料类别管理-编辑")
    public ResponseResult<String> edit(@RequestBody BmsProductCategoryEditReqDTO bmsProductCategoryEditReqDTO) {
        bmsProductCategoryService.edit(bmsProductCategoryEditReqDTO);
        return ResponseResult.getSuccess("ok");
    }
}
