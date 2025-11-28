package com.bio.drqi.manage.controller.bio;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.drqi.manage.bio.req.BioSampleApplyListPageReqDTO;
import com.bio.drqi.manage.bio.rsp.BioSampleApplyListPageRspDTO;
import com.bio.drqi.manage.sample.req.SampleApplyListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleApplyListPageRspDTO;
import com.bio.drqi.manage.service.bio.BioSampleApplyService;
import com.bio.drqi.manage.service.project.SampleApplyService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 分子检测申请
 */
@RestController
@RequestMapping("/sampleApply")
public class BioSampleApplyController {

    @Resource
    private BioSampleApplyService bioSampleApplyService;

    /**
     * 分子检测申请-分页查询
     *
     * @param bioSampleApplyListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    public ResponseResult<PageInfo<BioSampleApplyListPageRspDTO>> listPage(@RequestBody @Validated BioSampleApplyListPageReqDTO bioSampleApplyListPageReqDTO) {
        return ResponseResult.getSuccess(bioSampleApplyService.listPage(bioSampleApplyListPageReqDTO));
    }
}
