package com.bio.drqi.manage.controller.project;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.sample.req.CerSampleOneResultListPageReqDTO;
import com.bio.drqi.manage.sample.req.CerSampleTwoResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleOneResultListPageRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListDetailRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListPageRspDTO;
import com.bio.drqi.manage.service.project.CerSampleOneResultService;
import com.bio.drqi.manage.service.project.CerSampleTwoResultService;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

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

    /**
     * 二代测序-列表简述
     * @param id
     * @return
     */
    @GetMapping("listDetail")
    @WebLog(desc = "一代测序-结果详情查询")
    public ResponseResult<List<CerSampleTwoResultListDetailRspDTO>>  listDetail(@RequestParam Integer id){
        return ResponseResult.getSuccess(cerSampleTwoResultService.listDetail(id));
    }

    /**
     * 二代测序-结果详情
     *
     * @param detailId
     * @return
     */
    @GetMapping("detail")
    @WebLog(desc = "取样检测-生信检测结果数据详情")
    public ResponseResult<Object> detail(@RequestParam Integer detailId) {
        return ResponseResult.getSuccess(cerSampleTwoResultService.detail(detailId));
    }
}
