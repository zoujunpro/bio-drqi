package com.bio.drqi.manage.controller.bio;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.sample.req.CerSampleOneResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultListPageRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultQueryBySampleCodeRspDTO;
import com.bio.drqi.manage.service.bio.BioSampleOneResultService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

/**
 * 一代测序
 */
@RestController
@RequestMapping("/sampleOneResult")
public class BioSampleOneResultController {

    @Resource
    private BioSampleOneResultService bioSampleOneResultService;

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
        return ResponseResult.getSuccess(bioSampleOneResultService.listPage(cerSampleOneResultListPageReqDTO));
    }

    @GetMapping("/queryOneResultBySampleCode")
    @WebLog(desc = "一代测序-根据取样编号查询")
    public ResponseResult<CerSampleOneResultQueryBySampleCodeRspDTO>  queryOneResultBySampleCode(@RequestParam @Validated @NotBlank(message = "取样编号入参缺失") String sampleCode){
        return ResponseResult.getSuccess(bioSampleOneResultService.queryOneResultBySampleCode(sampleCode));

    }

}
