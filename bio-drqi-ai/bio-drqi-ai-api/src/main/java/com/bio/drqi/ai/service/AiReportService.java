package com.bio.drqi.ai.service;

import com.bio.drqi.ai.dto.req.AiReportReqDTO;

import javax.servlet.http.HttpServletResponse;

public interface AiReportService {

    void export(AiReportReqDTO reqDTO, HttpServletResponse response);
}
