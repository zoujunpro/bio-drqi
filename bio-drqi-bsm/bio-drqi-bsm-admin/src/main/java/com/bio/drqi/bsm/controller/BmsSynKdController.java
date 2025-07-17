package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.bsm.req.BmsSynKdExecuteReqDTO;
import com.bio.drqi.bsm.req.BmsSynKdListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsSynKdListPageRspDTO;
import com.bio.drqi.bsm.service.BmsSynKdService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 星空云数据同步
 */
@RestController
@RequestMapping("/bmsSynKd")
public class BmsSynKdController {

    @Resource
    private BmsSynKdService bmsSynKdService;


    /**
     * 星空云数据同步-分页查询
     *
     * @param bmsSynKdListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @RequirePermissions("bms:bmsSynKd:listPage")
    @WebLog(desc = "星空云数据同步-分页查询")
    public ResponseResult<PageInfo<BmsSynKdListPageRspDTO>> listPage(@RequestBody @Validated BmsSynKdListPageReqDTO bmsSynKdListPageReqDTO) {
        return ResponseResult.getSuccess(bmsSynKdService.listPage(bmsSynKdListPageReqDTO));
    }

    /**
     * 星空云数据同步-执行同步
     *
     * @param bmsSynKdExecuteReqDTO
     * @return
     */
    @PostMapping("execute")
    @RequirePermissions("bms:bmsSynKd:execute")
    @WebLog(desc = "星空云数据同步-执行同步")
    public ResponseResult<String> execute(@Validated @RequestBody BmsSynKdExecuteReqDTO bmsSynKdExecuteReqDTO) {
        bmsSynKdService.execute(bmsSynKdExecuteReqDTO);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 星空云数据同步-查询上次同步成功时间
     *
     * @return
     */
    @GetMapping("findLastSuccessTime")
    @WebLog(desc = "星空云数据同步-查询上次同步成功时间")
    public ResponseResult<String> findLastSuccessTime() {
        return ResponseResult.getSuccess(findLastSuccessTime());
    }
}
