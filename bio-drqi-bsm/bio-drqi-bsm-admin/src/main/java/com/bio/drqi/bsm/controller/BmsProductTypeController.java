package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsProductTyAddReqDTO;
import com.bio.drqi.bsm.req.BmsProductTyEditReqDTO;
import com.bio.drqi.bsm.req.BmsProductTyListPageReqDTO;
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

    @PostMapping("/listPage")
    @WebLog(desc = "库存类型管理-分页查询")
    public ResponseResult<PageInfo<BmsProductTyListPageRspDTO>> listPage(@RequestBody BmsProductTyListPageReqDTO bmsProductTyListPageReqDTO){
        return null;
    }
    @GetMapping("/listAll")
    @WebLog(desc = "库存类型管理-查询所有")
    public ResponseResult<List<BmsProductTyListAllRspDTO>> listAll(){
        return null;
    }

    @PostMapping("/add")
    @WebLog(desc = "库存类型管理-新增")
    public ResponseResult add(@RequestBody BmsProductTyAddReqDTO bmsProductTyAddReqDTO){
        return null;
    }

    @GetMapping("/delete")
    @WebLog(desc = "库存类型管理-删除")
    public ResponseResult delete(@RequestParam Integer id){
        return null;
    }

    @PostMapping("/edit")
    @WebLog(desc = "库存类型管理-编辑")
    public ResponseResult edit(@RequestBody BmsProductTyEditReqDTO bmsProductTyEditReqDTO){
        return null;
    }
}
