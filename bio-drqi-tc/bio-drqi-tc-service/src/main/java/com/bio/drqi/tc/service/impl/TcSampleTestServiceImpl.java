package com.bio.drqi.tc.service.impl;

import com.bio.drqi.tc.req.LayoutConfirmReqDTO;
import com.bio.drqi.tc.req.UploadIdentifyPrimerTemplateReqDTO;
import com.bio.drqi.tc.rsp.LayoutPreviewRspDTO;
import com.bio.drqi.tc.service.TcSampleTestService;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@Service
public class TcSampleTestServiceImpl implements TcSampleTestService {
    @Override
    public void downIdentifyPrimerTemplate(HttpServletResponse response, String applyNo) {

    }

    @Override
    public void uploadIdentifyPrimerTemplate(UploadIdentifyPrimerTemplateReqDTO uploadIdentifyPrimerTemplateReqDTO) {

    }

    @Override
    public LayoutPreviewRspDTO layoutPreview(String applyNo) {
        return null;
    }

    @Override
    public void layoutConfirm(LayoutConfirmReqDTO layoutConfirmReqDTO) {

    }
}
