package com.bio.drqi.manage.controller.project;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.drqi.manage.sample.req.SampleApplyListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleApplyListPageRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;


/**
 * 取样检测申请
 */
@RestController
@RequestMapping("/bioSampleApply")
public class SampleApplyController {


    @Resource
    private SampleApplyService sampleApplyService;

    /**
     * 取样检测申请-分页查询
     *
     * @param sampleApplyListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @RequirePermissions("cer:sampleApply:listPage")
    public ResponseResult<PageInfo<SampleApplyListPageRspDTO>> listPage(@RequestBody @Validated SampleApplyListPageReqDTO sampleApplyListPageReqDTO) {
        return ResponseResult.getSuccess(sampleApplyService.listPage(sampleApplyListPageReqDTO));
    }
}
