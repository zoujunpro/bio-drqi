package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcExperimentApplyListPageReqDTO;
import com.bio.drqi.tc.rsp.TcExperimentApplyListPageRspDTO;
import com.bio.drqi.tc.service.TcExperimentApplyService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 试验方案申请管理
 */
@RestController
@RequestMapping("/tcExperimentApply")
public class TcExperimentApplyController {

    @Resource
    private TcExperimentApplyService tcExperimentApplyService;
    /**
     * 试验方案申请管理-分页查询
     * @param tcExperimentApplyListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "试验方案申请管理-分页查询")
    public ResponseResult<PageInfo<TcExperimentApplyListPageRspDTO>> listPage(@Validated @RequestBody TcExperimentApplyListPageReqDTO tcExperimentApplyListPageReqDTO) {
        return ResponseResult.getSuccess(tcExperimentApplyService.listPage(tcExperimentApplyListPageReqDTO));
    }


}
