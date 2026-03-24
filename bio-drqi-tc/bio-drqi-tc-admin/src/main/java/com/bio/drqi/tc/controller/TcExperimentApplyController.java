package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.common.aspect.RequestLog;
import com.bio.drqi.tc.req.TcExperimentApplyListPageReqDTO;
import com.bio.drqi.tc.req.TcExperimentQueryByPdAndVectorTaskCodeReqDTO;
import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.rsp.*;
import com.bio.drqi.tc.service.TcExperimentApplyService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 试验申请管理
 */
@RestController
@RequestMapping("/tcExperimentApply")
public class TcExperimentApplyController {

    @Resource
    private TcExperimentApplyService tcExperimentApplyService;

    /**
     * 试验申请管理-分页查询
     *
     * @param tcExperimentApplyListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "试验申请管理-分页查询")
    @RequirePermissions("tc:tcExperimentApply:listPage")
    public ResponseResult<PageInfo<TcExperimentApplyListPageRspDTO>> listPage(@Validated @RequestBody TcExperimentApplyListPageReqDTO tcExperimentApplyListPageReqDTO) {
        return ResponseResult.getSuccess(tcExperimentApplyService.listPage(tcExperimentApplyListPageReqDTO));
    }

    /**
     * 试验申请管理-根据PD和实施方案编号组合查询
     * @param tcExperimentQueryByPdAndVectorTaskCodeReqDTO
     * @return
     */
    @PostMapping("/queryByPdAndVectorTaskCode")
    @WebLog(desc = "试验申请管理-根据PD和实施方案编号组合查询")
    public ResponseResult<List<TcExperimentApplyQueryByPdAndVectorTaskCodeRspDTO>> queryByPdAndVectorTaskCode(@RequestBody @Validated TcExperimentQueryByPdAndVectorTaskCodeReqDTO tcExperimentQueryByPdAndVectorTaskCodeReqDTO) {
        return ResponseResult.getSuccess(tcExperimentApplyService.queryByPdAndVectorTaskCode(tcExperimentQueryByPdAndVectorTaskCodeReqDTO));
    }

    /**
     * 试验申请管理-查询所有启用试验
     *
     * @param
     * @return
     */
    @GetMapping("/listAll")
    @WebLog(desc = "试验申请管理-查询所有启用试验")
    public ResponseResult<List<TcExperimentApplyListAllRspDTO>> listAll() {
        return ResponseResult.getSuccess(tcExperimentApplyService.listAll());
    }


    /**
     * 试验申请管理-文件下载
     *
     * @param httpServletResponse
     * @return
     */
    @GetMapping("/downTemplate")
    public void downTemplate(HttpServletResponse httpServletResponse) {
        tcExperimentApplyService.downTemplate(httpServletResponse);
    /*    try {
            ossService.downloadFile(httpServletResponse, "template", "田间设计方案模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("田间设计方案模板下载失败，请联系管理员检测模板配置");
        }*/
    }

    /**
     * 试验申请管理-田间设计列表
     *
     * @param experimentNum
     * @return
     */
    @GetMapping("/listDetail")
    @WebLog(desc = "试验申请管理-田间设计列表")
    public ResponseResult<List<TcExperimentListDetailRspDTO>> listDetail(@RequestParam @Validated String experimentNum) {
        return ResponseResult.getSuccess(tcExperimentApplyService.listDetail(experimentNum));
    }


    /**
     * 试验申请管理-完成
     *
     * @param id
     * @return
     */

    @GetMapping("/complete")
    @WebLog(desc = "试验申请管理-完成")
    @RequestLog("试验申请管理-完成")
    @RequirePermissions("tc:tcExperimentApply:complete")
    public ResponseResult<String> complete(@RequestParam @Validated Integer id) {
        tcExperimentApplyService.complete(id);
        return ResponseResult.getSuccess("ok");
    }


    /**
     * 试验申请管理-暂停
     *
     * @param id
     * @return
     */

    @GetMapping("/stop")
    @WebLog(desc = "试验申请管理-暂停")
    @RequestLog("试验申请管理-暂停")
    @RequirePermissions("tc:tcExperimentApply:stop")
    public ResponseResult<String> stop(@RequestParam @Validated Integer id) {
        tcExperimentApplyService.stop(id);
        return ResponseResult.getSuccess("ok");
    }

    /**
     * 试验申请管理-启用
     *
     * @param id
     * @return
     */

    @GetMapping("/start")
    @WebLog(desc = "试验申请管理-启用")
    @RequestLog("试验申请管理-启用")
    @RequirePermissions("tc:tcExperimentApply:start")
    public ResponseResult<String> start(@RequestParam @Validated Integer id) {
        tcExperimentApplyService.start(id);
        return ResponseResult.getSuccess("ok");
    }

}
