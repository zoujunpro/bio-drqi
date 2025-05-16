package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcExperimentListPageReqDTO;
import com.bio.drqi.tc.rsp.TcExperimentListAllRspDTO;
import com.bio.drqi.tc.rsp.TcExperimentListDetailRspDTO;
import com.bio.drqi.tc.rsp.TcExperimentListNoPollinationRspDTO;
import com.bio.drqi.tc.rsp.TcExperimentListPageRspDTO;
import com.bio.drqi.tc.service.TcExperimentService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 试验方案管理
 */
@RestController
@RequestMapping("/tcExperiment")
public class TcExperimentController {

    @Resource
    private TcExperimentService tcExperimentService;

    @Resource
    private OssService ossService;

    /**
     * 试验方案申请管理-分页查询
     * @param tcExperimentListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "试验方案申请管理-分页查询")
    public ResponseResult<PageInfo<TcExperimentListPageRspDTO>> listPage(@Validated @RequestBody TcExperimentListPageReqDTO tcExperimentListPageReqDTO) {
        return ResponseResult.getSuccess(tcExperimentService.listPage(tcExperimentListPageReqDTO));
    }

    /**
     * 试验方案申请管理-查询所有试验
     * @param
     * @return
     */
    @GetMapping("/listAll")
    @WebLog(desc = "试验方案申请管理-查询所有试验")
    public ResponseResult<List<TcExperimentListAllRspDTO>> listAll() {
        return ResponseResult.getSuccess(tcExperimentService.listAll());
    }

    /**
     * 试验方案申请管理-查询所有未授粉实验方案
     * @return
     */
    @GetMapping("/listByNoPollination")
    @WebLog(desc = "试验方案申请管理-查询所有未授粉实验方案")
    public ResponseResult<List<TcExperimentListNoPollinationRspDTO>> listByNoPollination() {
        return ResponseResult.getSuccess(tcExperimentService.listByNoPollination());
    }

    /**
     * 试验方案申请管理-文件下载
     * @param httpServletResponse
     * @return
     */
    @GetMapping("/downTemplate")
    public void downTemplate(HttpServletResponse httpServletResponse) {
        try {
            ossService.downloadFile(httpServletResponse, "template", "田间设计方案模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("田间设计方案模板下载失败，请联系管理员检测模板配置");
        }
    }

    /**
     * 试验方案申请管理-田间设计列表
     * @param experimentNum
     * @return
     */
    @GetMapping("/listDetail")
    @WebLog(desc = "试验方案申请管理-田间设计列表")
    public ResponseResult<List<TcExperimentListDetailRspDTO>> listDetail(@RequestParam @Validated String experimentNum){
        return ResponseResult.getSuccess(tcExperimentService.listDetail(experimentNum));
    }

}
