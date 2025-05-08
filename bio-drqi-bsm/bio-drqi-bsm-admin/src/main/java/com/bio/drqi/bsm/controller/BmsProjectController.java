package com.bio.drqi.bsm.controller;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsProjectAddReqDTO;
import com.bio.drqi.bsm.req.BmsProjectEditReqDTO;
import com.bio.drqi.bsm.req.BmsProjectListPageReqDTO;
import com.bio.drqi.bsm.req.BmsProjectQueryAllReqDTO;
import com.bio.drqi.bsm.rsp.BmsProjectListPageRspDTO;
import com.bio.drqi.bsm.service.BmsProjectService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 耗材管理项目字典表
 */
@RestController
@RequestMapping("/bmsProject")
public class BmsProjectController {


    @Resource
    private BmsProjectService bmsProjectService;


    /**
     * 耗材管理项目字典表-分页查询
     *
     * @param bmsProjectListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "耗材管理项目字典表-分页查询")
    @RequirePermissions("bms:bmsProject:listPage")
    public ResponseResult<PageInfo<BmsProjectListPageRspDTO>> listPage(@RequestBody BmsProjectListPageReqDTO bmsProjectListPageReqDTO) {
        return ResponseResult.getSuccess(bmsProjectService.listPage(bmsProjectListPageReqDTO));
    }

    /**
     * 耗材管理项目字典表-查询所有
     *
     * @return
     */
    @GetMapping("/queryAll")
    @WebLog(desc = "耗材管理项目字典表-查询所有")
    public ResponseResult<List<BmsProjectQueryAllReqDTO>> queryAll() {
        return ResponseResult.getSuccess(bmsProjectService.queryAll());
    }

    /**
     * 耗材管理项目字典表-新增
     *
     * @param bmsProjectAddReqDTO
     * @return
     */
    @PostMapping("/add")
    @WebLog(desc = "耗材管理项目字典表-新增")
    @RequirePermissions("bms:bmsProject:add")
    public ResponseResult<String> add(@RequestBody BmsProjectAddReqDTO bmsProjectAddReqDTO) {
        bmsProjectService.add(bmsProjectAddReqDTO);
        return ResponseResult.getSuccess(null);
    }


    /**
     * 耗材管理项目字典表-编辑
     *
     * @param bmsProjectEditReqDTO
     * @return
     */
    @PostMapping("/edit")
    @WebLog(desc = "耗材管理项目字典表-编辑")
    @RequirePermissions("bms:bmsProject:edit")
    public ResponseResult<String> edit(@RequestBody BmsProjectEditReqDTO bmsProjectEditReqDTO) {
        bmsProjectService.edit(bmsProjectEditReqDTO);
        return ResponseResult.getSuccess(null);
    }

    /**
     * 耗材管理项目字典表-删除
     *
     * @param id
     * @return
     */
    @GetMapping("/delete")
    @WebLog(desc = "耗材管理项目字典表-删除")
    @RequirePermissions("bms:bmsProject:delete")
    public ResponseResult<String> delete(@RequestParam Integer id) {
        bmsProjectService.delete(id);
        return ResponseResult.getSuccess(null);
    }
}
