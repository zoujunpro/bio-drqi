package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.sample.req.*;
import com.bio.drqi.manage.sample.rsp.*;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface SampleTestService {

    PageInfo<SampleTestListDetailRspDTO> listPage(SampleTestListDetailReqDTO sampleTestListDetailReqDTO);

    List<SampleApplyRspDTO> listByVectorTask(SampleTestByVectorTaskReqDTO sampleTestByVectorTaskReqDTO);

    /**
     * 取样数据模板下载
     */
    void downSampleTemplate(DownloadSampleTemplateReqDTO downloadSampleTemplateReqDTO, HttpServletResponse response);

    void uploadSampleTemplate(UploadSampleTemplateReqDTO uploadSampleTemplateReqDTO);

    /**
     * 样品数据模板下载
     */
    void downTestTemplate(DownTestTemplateReqDTO downTestTemplateReqDTO, HttpServletResponse response);


    void uploadTestTemplate(UploadTestTemplateReqDTO uploadTestTemplateReqDTO);

    List<SampleTestListDetailRspDTO> checkList(CheckListReqDTO checkListReqDTO);

    void approveSampleResult(ApproveSampleResultReqDTO approveSampleResultReqDTO);

    List<SampleCodeListRspDTO> findAllSampleCodeList(String vectorTaskCode);


    void downIdentifyPrimerTemplate(HttpServletResponse response, String applyNo);

    void uploadIdentifyPrimerTemplate(UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO);

    LayoutPreviewRspDTO layoutPreview(String applyNo);

    void layoutConfirm(LayoutConfirmReqDTO layoutConfirmReqDTO);

    void dowLayoutExcel(String applyNo, HttpServletResponse httpServletResponse);


    CountNumByApplyNoRspDTO countNumByApplyNo(String applyNo);

    void uploadBioInfoSampleTestResult(UploadBioInfoSampleTestResultReqDTO uploadBioInfoSampleTestResultReqDTO);

    List<QueryBioInfoSampleTestResultRspDTO> queryBioInfoSampleTestResult(Integer id);

    void bioInfoSampleTestResultConfirm(BioInfoSampleTestResultConfirmReqDTO bioInfoSampleTestResultConfirmReqDTO);

    void synBioInfoSampleTestResult(Integer id);


    void remark(SampleRemarkReqDTO sampleRemarkReqDTO);

    void uploadTargetResultTemplate(SampleTestUploadTargetResultTemplateReqDTO sampleTestUploadTargetResultTemplateReqDTO);

    List<CountCheckResultRspDTO> countCheckResult(String applyNo);

    CountTestResultRspDTO countTestResult( String applyNo);
}
