package com.bio.drqi.tc.controller;


import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.oss.service.OssService;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcPollinationCreatePollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationExportPollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationCreatePollinationExcelRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPollinationApplyNumNotHarvestRspDTO;
import com.bio.drqi.tc.service.TcPollinationService;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

/**
 * 授粉收获管理
 */
@RestController
@RequestMapping("/tcPollination")
public class TcPollinationController {

    @Resource
    private TcPollinationService tcPollinationService;

    @Resource
    private OssService ossService;
    /**
     * 授粉管理-分页查询
     *
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "授粉管理-分页查询")
    @RequirePermissions("tc:tcPollination:listPage")
    public ResponseResult<PageInfo<TcPollinationListPageRspDTO>> listPage(@RequestBody @Validated TcPollinationListPageReqDTO tcPollinationListPageReqDTO) {
        return ResponseResult.getSuccess(tcPollinationService.listPage(tcPollinationListPageReqDTO));
    }

    /**
     * 授粉管理-查询未收获的授粉申请批次号
     * @return
     */
    @GetMapping("/listPollinationApplyNumNotHarvest")
    @WebLog(desc = "授粉管理-查询未收获的授粉申请批次号")
    public ResponseResult<List<TcPollinationListPollinationApplyNumNotHarvestRspDTO>> listPollinationApplyNumNotHarvest(){
        return ResponseResult.getSuccess(tcPollinationService.listPollinationApplyNumNotHarvest());
    }


    /**
     * 授粉管理-授粉列表分页查询
     *
     * @return
     */
    @PostMapping("/listPageDetail")
    @WebLog(desc = "授粉管理-授粉列表分页查询")
    public ResponseResult<PageInfo<TcPollinationListPageDetailRspDTO>> listPageDetail(@RequestBody @Validated TcPollinationListPageDetailReqDTO tcPollinationListPageDetailReqDTO) {
        return ResponseResult.getSuccess(tcPollinationService.listPageDetail(tcPollinationListPageDetailReqDTO));
    }


    /**
     * 授粉管理-生成授粉excel
     */
    @PostMapping("/createPollinationExcel")
    @WebLog(desc = "授粉管理-生成授粉excel")
    @Transactional(rollbackFor = Exception.class)
    @RequirePermissions("tc:tcPollination:createPollinationExcel")
    public ResponseResult<List<TcPollinationExcelDTO>> createPollinationExcel(@RequestBody @Validated TcPollinationCreatePollinationExcelReqDTO tcPollinationCreatePollinationExcelReqDTO) {
            return ResponseResult.getSuccess(tcPollinationService.createPollinationExcel(tcPollinationCreatePollinationExcelReqDTO));
    }




    /**
     * 授粉管理-生成最终授粉excel
     */
    @PostMapping("/exportPollinationExcel")
    @WebLog(desc = "授粉管理-生成授粉excel")
    @Transactional(rollbackFor = Exception.class)
    @RequirePermissions("tc:tcPollination:createPollinationExcel")
    public void exportPollinationExcel(@RequestBody @Validated TcPollinationExportPollinationExcelReqDTO tcPollinationExportPollinationExcelReqDTO, HttpServletResponse httpServletResponse) {
        tcPollinationService.exportPollinationExcel(tcPollinationExportPollinationExcelReqDTO,httpServletResponse);
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
