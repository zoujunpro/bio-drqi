package com.bio.drqi.manage.controller.project;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.sample.req.CerSampleOneResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultListPageRspDTO;
import com.bio.drqi.manage.service.project.CerSampleOneResultService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 一代测序
 */
@RestController
@RequestMapping("/sampleOneResult")
public class CerSampleOneResultController {

    @Resource
    private CerSampleOneResultService cerSampleOneResultService;

    /**
     * 一代测序-分页查询
     *
     * @param cerSampleOneResultListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "一代测序-分页查询")
    @RequirePermissions("cer:sampleOneResult:listPage")
    public ResponseResult<PageInfo<CerSampleOneResultListPageRspDTO>> listPage(@RequestBody CerSampleOneResultListPageReqDTO cerSampleOneResultListPageReqDTO) {
        return ResponseResult.getSuccess(cerSampleOneResultService.listPage(cerSampleOneResultListPageReqDTO));
    }
}
