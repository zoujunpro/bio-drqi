package com.bio.drqi.tc.controller;


import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcPollinationCreatePollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationApplyExportPollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcPollinationApplyListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationApplyListPageRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationApplyListPollinationApplyNumNotHarvestRspDTO;
import com.bio.drqi.tc.service.TcPollinationApplyService;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 授粉申请管理
 */
@RestController
@RequestMapping("/tcPollinationApply")
public class TcPollinationApplyController {

    @Resource
    private TcPollinationApplyService tcPollinationApplyService;

    @Resource
    private OssService ossService;
    /**
     * 授粉管理-分页查询
     *
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "授粉管理-分页查询")
    @RequirePermissions("tc:tcPollinationApply:listPage")
    public ResponseResult<PageInfo<TcPollinationApplyListPageRspDTO>> listPage(@RequestBody @Validated TcPollinationApplyListPageReqDTO tcPollinationApplyListPageReqDTO) {
        return ResponseResult.getSuccess(tcPollinationApplyService.listPage(tcPollinationApplyListPageReqDTO));
    }

    /**
     * 授粉管理-查询未收获的授粉申请批次号
     * @return
     */
    @GetMapping("/listPollinationApplyNumNotHarvest")
    @WebLog(desc = "授粉管理-查询未收获的授粉申请批次号")
    public ResponseResult<List<TcPollinationApplyListPollinationApplyNumNotHarvestRspDTO>> listPollinationApplyNumNotHarvest(){
        return ResponseResult.getSuccess(tcPollinationApplyService.listPollinationApplyNumNotHarvest());
    }



    /**
     * 授粉管理-生成授粉excel
     */
    @PostMapping("/createPollinationExcel")
    @WebLog(desc = "授粉管理-生成授粉excel")
    @Transactional(rollbackFor = Exception.class)
    @RequirePermissions("tc:tcPollinationApply:createPollinationExcel")
    public ResponseResult<List<TcPollinationExcelDTO>> createPollinationExcel(@RequestBody @Validated TcPollinationCreatePollinationExcelReqDTO tcPollinationCreatePollinationExcelReqDTO) {
            return ResponseResult.getSuccess(tcPollinationApplyService.createPollinationExcel(tcPollinationCreatePollinationExcelReqDTO));
    }




    /**
     * 授粉管理-生成最终授粉excel
     */
    @PostMapping("/exportPollinationExcel")
    @WebLog(desc = "授粉管理-生成授粉excel")
    @Transactional(rollbackFor = Exception.class)
    @RequirePermissions("tc:tcPollinationApply:createPollinationExcel")
    public void exportPollinationExcel(@RequestBody @Validated TcPollinationApplyExportPollinationExcelReqDTO tcPollinationApplyExportPollinationExcelReqDTO, HttpServletResponse httpServletResponse) {
        tcPollinationApplyService.exportPollinationExcel(tcPollinationApplyExportPollinationExcelReqDTO,httpServletResponse);
    }



    /**
     * 授粉管理-授粉结果表下载
     * @param httpServletResponse
     * @return
     */
    @PostMapping("/downTemplate")
    @WebLog(desc = "授粉管理-授粉结果表下载")
    public void downTemplate(HttpServletResponse httpServletResponse) {
        try {
            ossService.downloadFile(httpServletResponse, "template", "田测授粉结果表单模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("田测授粉数据表单模板下载失败，请联系管理员检测模板配置");
        }
    }


}
