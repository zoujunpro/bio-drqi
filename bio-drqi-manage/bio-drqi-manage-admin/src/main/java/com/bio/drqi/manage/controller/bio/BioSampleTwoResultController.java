package com.bio.drqi.manage.controller.bio;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.common.aspect.RequestLog;
import com.bio.drqi.manage.sample.req.CerSampleTwoResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListDetailRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListPageRspDTO;
import com.bio.drqi.manage.service.bio.BioSampleTwoResultService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * 二代测序
 */
@RestController
@RequestMapping("/sampleTwoResult")
public class BioSampleTwoResultController {

    @Resource
    private BioSampleTwoResultService bioSampleTwoResultService;

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
        return ResponseResult.getSuccess(bioSampleTwoResultService.listPage(cerSampleTwoResultListPageReqDTO));
    }

    /**
     * 二代测序-列表简述
     *
     * @param id
     * @return
     */
    @GetMapping("listDetail")
    @WebLog(desc = "一代测序-结果详情查询")
    public ResponseResult<List<CerSampleTwoResultListDetailRspDTO>> listDetail(@RequestParam Integer id) {
        return ResponseResult.getSuccess(bioSampleTwoResultService.listDetail(id));
    }

    /**
     * 二代测序-结果详情
     *
     * @param detailId
     * @return
     */
    @GetMapping("detail")
    @WebLog(desc = "二代测序-结果详情")
    public ResponseResult<Object> detail(@RequestParam Integer detailId) {
        return ResponseResult.getSuccess(bioSampleTwoResultService.detail(detailId));
    }

    /**
     * 二代测序-同步某一个生信结果
     *
     * @param id
     * @return
     */
    @GetMapping("synOne")
    @WebLog(desc = "二代测序-同步某一个生信结果")
    public ResponseResult<String> synOne(@RequestParam Integer id) {
        bioSampleTwoResultService.synOne(id);
        return ResponseResult.getSuccess("成功");
    }

    @GetMapping("deleteNgsResult")
    @WebLog(desc = "取样检测-删除无效的NGS结果")
    @RequestLog("取样检测-删除无效的NGS结果")
    public ResponseResult<String> deleteNgsResult(@RequestParam @Validated @NotBlank(message = "参数缺失：uniqueDbCode") String uniqueDbCode) {
        bioSampleTwoResultService.deleteNgsResult(uniqueDbCode);
        return ResponseResult.getSuccess("ok");
    }


}
