package com.bio.drqi.common.util;

import cn.hutool.core.io.IoUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.freemarker.util.HtmlToPdfUtils;

import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public final class PdfUtil {

    private PdfUtil() {
    }

    public static void htmlToPdf(String html, HttpServletResponse response, String fileName, String fontPath) {
        if (StringUtils.isBlank(html)) {
            throw new IllegalArgumentException("html不能为空");
        }
        try (InputStream inputStream = IoUtil.toStream(html, StandardCharsets.UTF_8)) {
            htmlToPdf(inputStream, response, fileName, fontPath);
        } catch (Exception e) {
            throw new RuntimeException("PDF生成失败", e);
        }
    }

    public static void htmlToPdf(InputStream htmlInputStream, HttpServletResponse response, String fileName, String fontPath) {
        if (htmlInputStream == null) {
            throw new IllegalArgumentException("htmlInputStream不能为空");
        }
        if (response == null) {
            throw new IllegalArgumentException("response不能为空");
        }
        if (StringUtils.isBlank(fileName)) {
            throw new IllegalArgumentException("fileName不能为空");
        }
        if (StringUtils.isBlank(fontPath)) {
            throw new IllegalArgumentException("fontPath不能为空");
        }
        try {
            //region Description
            HtmlToPdfUtils.html2Pdf(htmlInputStream, response, fileName, fontPath);
            //endregion
        } catch (Exception e) {
            throw new RuntimeException("PDF生成失败", e);
        }
    }
}
