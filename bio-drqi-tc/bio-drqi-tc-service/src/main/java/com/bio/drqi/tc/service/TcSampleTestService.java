package com.bio.drqi.tc.service;

import com.bio.drqi.tc.req.*;
import com.bio.drqi.tc.rsp.TcSampleTestLayoutPreviewRspDTO;
import com.bio.drqi.tc.rsp.TcSampleTestListPageDetailRspDTO;
import com.bio.drqi.tc.rsp.TcSampleTestListPageRspDTO;
import com.bio.drqi.tc.rsp.TcSampleTestQueryListBySampleCodeListRspDTO;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface TcSampleTestService {


    /**
     * 田测取样检测管理-取样申请列表
     *
     * @param tcSampleTestListPageReqDTO
     * @return
     */
    PageInfo<TcSampleTestListPageRspDTO> listPage(TcSampleTestListPageReqDTO tcSampleTestListPageReqDTO);


    /**
     * 田测取样检测管理-取样详情分页
     *
     * @param tcSampleTestListPageDetailReqDTO
     * @return
     */
    PageInfo<TcSampleTestListPageDetailRspDTO> listPageDetail(TcSampleTestListPageDetailReqDTO tcSampleTestListPageDetailReqDTO);


    List<String> listByExperimentNum( String experimentNum);


    /**
     * 检测数据模板下载
     */
    void downTestTemplate(TcSampleTestDownTestTemplateReqDTO tcSampleTestDownTestTemplateReqDTO, HttpServletResponse response);


    /**
     * 上传检测数据
     */
    void uploadTestTemplate(TcSampleTestUploadTestTemplateReqDTO tcSampleTestUploadTestTemplateReqDTO);


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
    void uploadIdentifyPrimerTemplate(TcSampleTestUploadIdentifyPrimerTemplateReqDTO tcSampleTestUploadIdentifyPrimerTemplateReqDTO);

    /**
     * 取样标签排版预览
     *
     * @param applyNo
     * @return
     */
    TcSampleTestLayoutPreviewRspDTO layoutPreview(String applyNo);


    /**
     * 取样标签排版确认
     *
     * @param tcSampleTestLayoutConfirmReqDTO
     * @return
     */
    void layoutConfirm(TcSampleTestLayoutConfirmReqDTO tcSampleTestLayoutConfirmReqDTO);


    /**
     * 下载孔板信息
     *
     * @param applyNo
     * @param httpServletResponse
     */
    void dowLayoutExcel(String applyNo, HttpServletResponse httpServletResponse);


    /**
     * 取样检测审批
     */
    void approveSampleResult(TcSampleTestApproveSampleResultReqDTO tcSampleTestApproveSampleResultReqDTO);


    List<TcSampleTestQueryListBySampleCodeListRspDTO> queryListBySampleCodeList(TcSampleTestQueryListBySampleCodeListReqDTO tcSampleTestQueryListBySampleCodeListReqDTO);
}
