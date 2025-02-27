package com.bio.drqi.manage.freemarker.service;

import javax.servlet.http.HttpServletResponse;

public interface SeedPdfService {
    public void generatePDF(Integer taskId, HttpServletResponse httpServletResponse);
}
