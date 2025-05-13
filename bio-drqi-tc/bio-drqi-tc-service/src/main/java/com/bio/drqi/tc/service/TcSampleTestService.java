package com.bio.drqi.tc.service;
import com.bio.drqi.tc.req.LayoutConfirmReqDTO;
import com.bio.drqi.tc.req.UploadIdentifyPrimerTemplateReqDTO;
import com.bio.drqi.tc.rsp.LayoutPreviewRspDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import javax.servlet.http.HttpServletResponse;

public interface TcSampleTestService {

    /**
     * 下载填写鉴定引物模板
     *
     * @param response
     * @param applyNo
     * @return
     */
    void downIdentifyPrimerTemplate(HttpServletResponse response, String applyNo);

    /**
     * 上传填写鉴定引物模板
     *
     * @return
     */
    void uploadIdentifyPrimerTemplate(UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO);

    /**
     * 取样标签排版预览
     *
     * @param applyNo
     * @return
     */
    LayoutPreviewRspDTO layoutPreview(@RequestParam @Validated String applyNo);


    /**
     * 取样标签排版确认
     *
     * @param layoutConfirmReqDTO
     * @return
     */
    void layoutConfirm(@RequestBody @Validated LayoutConfirmReqDTO layoutConfirmReqDTO);


    /**
     * 下载孔板信息
     * @param applyNo
     * @param httpServletResponse
     */
  void   dowLayoutExcel(String applyNo, HttpServletResponse httpServletResponse);
}
