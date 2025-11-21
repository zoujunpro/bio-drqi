package com.bio.drqi.manage.service.bio;


import com.bio.drqi.manage.bio.req.BioSampleTestUploadTestTemplateReqDTO;

import com.bio.drqi.manage.sample.req.DownTestTemplateReqDTO;

import com.bio.drqi.manage.sample.req.LayoutConfirmReqDTO;
import com.bio.drqi.manage.sample.req.UploadIdentifyPrimerTemplateReqDTO;
import com.bio.drqi.manage.sample.rsp.*;
import org.springframework.stereotype.Service;


import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public interface BioSampleTestService {

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


    List<CountCheckResultRspDTO> countCheckResult(String applyNo);

    CountTestResultRspDTO countTestResult(String applyNo);
}
