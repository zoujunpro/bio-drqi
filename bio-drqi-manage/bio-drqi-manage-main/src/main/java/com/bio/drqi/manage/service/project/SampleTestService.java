package com.bio.drqi.manage.service.project;

import com.bio.drqi.sample.req.*;
import com.bio.drqi.sample.rsp.*;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface SampleTestService {

    PageInfo<SampleApplyRspDTO> listPage(SampleApplyListPageReqDTO sampleApplyListPageReqDTO);

    PageInfo<SampleTestListDetailRspDTO> listDetail(SampleTestListDetailReqDTO sampleTestListDetailReqDTO);

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

    List<SampleApplyRspDTO> sampleApplyListAll(String currentStepCode);

    CountNumByApplyNoRspDTO countNumByApplyNo(String applyNo);

    void uploadBioInfoSampleTestResult(UploadBioInfoSampleTestResultReqDTO uploadBioInfoSampleTestResultReqDTO);

    List<QueryBioInfoSampleTestResultRspDTO> queryBioInfoSampleTestResult(Integer id);

    void bioInfoSampleTestResultConfirm(BioInfoSampleTestResultConfirmReqDTO bioInfoSampleTestResultConfirmReqDTO);

    void synBioInfoSampleTestResult(Integer id);

    Object bioInfoSampleTestResultDetail(Integer bioInfoId);

    Integer bioInfoHead(String applyNo);

    PageInfo<BioInfoPageRspDTO> bioInfoPage(BioInfoPageReqDTO bioInfoPageReqDTO);

    void remark(SampleRemarkReqDTO sampleRemarkReqDTO);
}
