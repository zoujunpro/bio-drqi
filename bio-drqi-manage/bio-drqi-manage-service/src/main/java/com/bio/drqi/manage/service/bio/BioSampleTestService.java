package com.bio.drqi.manage.service.bio;


import com.bio.drqi.manage.bio.req.BioSampleTestListDetailReqDTO;
import com.bio.drqi.manage.bio.req.BioSampleTestUploadTestTemplateReqDTO;

import com.bio.drqi.manage.bio.rsp.BioSampleTestListDetailRspDTO;
import com.bio.drqi.manage.bio.rsp.BioSampleTestQuerySpeciesByApplyTypeRspDTO;
import com.bio.drqi.manage.sample.req.*;

import com.bio.drqi.manage.sample.rsp.*;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Service
public interface BioSampleTestService {

    PageInfo<BioSampleTestListDetailRspDTO> listPage(BioSampleTestListDetailReqDTO bioSampleTestListDetailReqDTO);

    List<BioSampleTestQuerySpeciesByApplyTypeRspDTO> querySpeciesByApplyType(String applyType);

    void downRepeatSampleTemplate(HttpServletResponse httpServletResponse);

    ;

    void downTestTemplate(DownTestTemplateReqDTO downTestTemplateReqDTO, HttpServletResponse response);


    void uploadTestTemplate(BioSampleTestUploadTestTemplateReqDTO bioSampleTestUploadTestTemplateReqDTO);

    void downIdentifyPrimerTemplate(HttpServletResponse response, String applyNo);

    void uploadIdentifyPrimerTemplate(UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO);

    LayoutPreviewRspDTO layoutPreview(String applyNo);

    void layoutConfirm(LayoutConfirmReqDTO layoutConfirmReqDTO);

    void dowLayoutExcel(String applyNo, HttpServletResponse httpServletResponse);


    CountNumByApplyNoRspDTO countNumByApplyNo(String applyNo);


    List<QueryBioInfoSampleTestResultRspDTO> queryBioInfoSampleTestResult(Integer id);

    void synBioInfoSampleTestResult(Integer id);


    void uploadBioInfoSampleTestResult(UploadBioInfoSampleTestResultReqDTO uploadBioInfoSampleTestResultReqDTO);


    List<CountCheckResultRspDTO> countCheckResult(String applyNo);

    CountTestResultRspDTO countTestResult(String applyNo);
}
