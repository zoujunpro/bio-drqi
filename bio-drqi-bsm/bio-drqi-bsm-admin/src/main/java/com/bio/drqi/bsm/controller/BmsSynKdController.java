package com.bio.drqi.bsm.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.bsm.req.BmsSynKdExecuteReqDTO;
import com.bio.drqi.bsm.req.BmsSynKdListPageReqDTO;
import com.bio.drqi.bsm.rsp.BmsSynKdListPageRspDTO;
import com.bio.drqi.bsm.service.BmsSynKdService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
     * @param bmsSynKdListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    public ResponseResult<PageInfo<BmsSynKdListPageRspDTO>> listPage(@RequestBody @Validated BmsSynKdListPageReqDTO bmsSynKdListPageReqDTO) {
        return ResponseResult.getSuccess(bmsSynKdService.listPage(bmsSynKdListPageReqDTO));
    }

    /**
     * 星空云数据同步-执行同步
     * @param bmsSynKdExecuteReqDTO
     * @return
     */
    @PostMapping("execute")
    public ResponseResult<String> execute(@Validated @RequestBody BmsSynKdExecuteReqDTO bmsSynKdExecuteReqDTO) {
        bmsSynKdService.execute(bmsSynKdExecuteReqDTO);
        return ResponseResult.getSuccess("ok");
    }
}
