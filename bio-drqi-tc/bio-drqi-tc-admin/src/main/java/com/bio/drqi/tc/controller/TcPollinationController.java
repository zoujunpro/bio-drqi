package com.bio.drqi.tc.controller;


import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.oss.service.OssService;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.TcPollinationCreatePollinationExcelReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageDetailReqDTO;
import com.bio.drqi.tc.req.TcPollinationListPageReqDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcPollinationListPageRspDTO;
import com.bio.drqi.tc.service.TcPollinationService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 授粉管理
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
    public ResponseResult<PageInfo<TcPollinationListPageRspDTO>> listPage(@RequestBody @Validated TcPollinationListPageReqDTO tcPollinationListPageReqDTO) {
        return ResponseResult.getSuccess(tcPollinationService.listPage(tcPollinationListPageReqDTO));
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
    public void createPollinationExcel(@RequestBody @Validated TcPollinationCreatePollinationExcelReqDTO tcPollinationCreatePollinationExcelReqDTO, HttpServletResponse httpServletResponse) {
            tcPollinationService.createPollinationExcel(tcPollinationCreatePollinationExcelReqDTO,httpServletResponse);
    }


    /**
     * 授粉管理-田测授粉数据表单模板下载
     * @param httpServletResponse
     * @return
     */
    @PostMapping("/downTemplate")
    @WebLog(desc = "试验方案申请管理-文件下载")
    public void downTemplate(HttpServletResponse httpServletResponse) {
        try {
            ossService.downloadFile(httpServletResponse, "template", "田测授粉数据表单模板V1.0.xlsx");
        } catch (Exception e) {
            throw new BusinessException("田测授粉数据表单模板下载失败，请联系管理员检测模板配置");
        }
    }

}
