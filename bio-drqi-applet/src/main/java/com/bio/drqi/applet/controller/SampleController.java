package com.bio.drqi.applet.controller;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.applet.dto.req.QueryBySampleCodeReqDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.service.SampleService;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;

/**
 * 取样信息相关接口
 */
@RestController
@RequestMapping("sample")
public class SampleController {

    @Resource
    private SampleService sampleService;

    /**
     * 根据取样编号查询信息
     *
     * @param queryBySampleCodeReqDTO
     * @return
     */
    @PostMapping("/queryBySampleCode")
    @WebLog(desc = "根据取样编号查询信息")
    public ResponseResult<ScanCodeSampleTestRspDTO> queryBySampleCode(@RequestBody QueryBySampleCodeReqDTO queryBySampleCodeReqDTO) {
        return ResponseResult.getSuccess(sampleService.queryBySampleCode(queryBySampleCodeReqDTO));
    }
}
