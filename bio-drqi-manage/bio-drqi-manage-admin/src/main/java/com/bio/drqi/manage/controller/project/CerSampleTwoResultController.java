package com.bio.drqi.manage.controller.project;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.sample.req.CerSampleOneResultListPageReqDTO;
import com.bio.drqi.manage.sample.req.CerSampleTwoResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultListPageRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListPageRspDTO;
import com.bio.drqi.manage.service.project.CerSampleOneResultService;
import com.bio.drqi.manage.service.project.CerSampleTwoResultService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 二代测序
 */
@RestController
@RequestMapping("/sampleTwoResult")
public class CerSampleTwoResultController {

    @Resource
    private CerSampleTwoResultService cerSampleTwoResultService;

    /**
     * 二代测序-分页查询
     *
     * @param cerSampleTwoResultListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "一代测序-分页查询")
    @RequirePermissions("cer:sampleTwoResult:listPage")
    public ResponseResult<PageInfo<CerSampleTwoResultListPageRspDTO>> listPage(@RequestBody CerSampleTwoResultListPageReqDTO cerSampleTwoResultListPageReqDTO) {
        return ResponseResult.getSuccess(cerSampleTwoResultService.listPage(cerSampleTwoResultListPageReqDTO));
    }
}
