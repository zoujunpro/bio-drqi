package com.bio.drqi.tc.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.tc.req.LayoutConfirmReqDTO;
import com.bio.drqi.tc.req.UploadIdentifyPrimerTemplateReqDTO;
import com.bio.drqi.tc.rsp.LayoutPreviewRspDTO;
import com.bio.drqi.tc.service.TcSampleTestService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

/**
 * 田测取样检测管理
 */
@RestController
@RequestMapping("/tcSampleTest")
public class TcSampleTestController {

    @Resource
    private TcSampleTestService tcSampleTestService;

    /**
     * 田测取样检测管理-下载填写鉴定引物模板
     *
     * @param response
     * @param applyNo
     * @return
     */
    @GetMapping("downIdentifyPrimerTemplate")
    @WebLog(desc = "田测取样检测管理-下载填写鉴定引物模板")
    public void downIdentifyPrimerTemplate(HttpServletResponse response, @RequestParam @Validated String applyNo) {
        tcSampleTestService.downIdentifyPrimerTemplate(response, applyNo);
    }

    /**
     * 上传填写鉴定引物模板
     *
     * @return
     */
    @PostMapping("uploadIdentifyPrimerTemplate")
    @WebLog(desc = "田测取样检测管理-上传填写鉴定引物模板")
    public ResponseResult<String> uploadIdentifyPrimerTemplate(@RequestBody @Validated UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO) {
        tcSampleTestService.uploadIdentifyPrimerTemplate(uploadIdentifyPrimerTemplateReqDTO);
        return ResponseResult.getSuccess("成功");
    }

    /**
     * 取样标签排版预览
     *
     * @param applyNo
     * @return
     */
    @GetMapping("layoutPreview")
    @WebLog(desc = "田测取样检测管理-取样标签排版预览")
    public ResponseResult<LayoutPreviewRspDTO> layoutPreview(@RequestParam @Validated String applyNo) {
        return ResponseResult.getSuccess(tcSampleTestService.layoutPreview(applyNo));
    }


    /**
     * 取样标签排版确认
     *
     * @param layoutConfirmReqDTO
     * @return
     */
    @PostMapping("layoutConfirm")
    @WebLog(desc = "田测取样检测管理-取样标签排版确认")
    public ResponseResult layoutConfirm(@RequestBody @Validated LayoutConfirmReqDTO layoutConfirmReqDTO) {
        tcSampleTestService.layoutConfirm(layoutConfirmReqDTO);
        return ResponseResult.getSuccess("成功");
    }


    /**
     * 下载excel96孔板
     *
     * @return
     */
    @GetMapping("dowLayoutExcel")
    @WebLog(desc = "下载excel96孔板")
    public void dowLayoutExcel(@RequestParam @Validated String applyNo, HttpServletResponse httpServletResponse) {
        tcSampleTestService.dowLayoutExcel(applyNo, httpServletResponse);
    }

}
