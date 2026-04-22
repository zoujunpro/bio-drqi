package com.bio.drqi.common.util;

import cn.hutool.core.io.IoUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.freemarker.util.HtmlToPdfUtils;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

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
            HtmlToPdfUtils.html2Pdf(htmlInputStream, new PdfFileNameResponseWrapper(response, fileName), fileName, fontPath);
        } catch (Exception e) {
            throw new RuntimeException("PDF生成失败", e);
        }
    }

    private static class PdfFileNameResponseWrapper extends HttpServletResponseWrapper {
        private final String encodedFileName;

        private PdfFileNameResponseWrapper(HttpServletResponse response, String fileName) {
            super(response);
            try {
                this.encodedFileName = URLEncoder.encode(fileName + ".pdf", StandardCharsets.UTF_8.name());
            } catch (Exception e) {
                throw new IllegalArgumentException("文件名编码失败", e);
            }
        }

        @Override
        public void setHeader(String name, String value) {
            super.setHeader(name, rewriteContentDisposition(name, value));
        }

        @Override
        public void addHeader(String name, String value) {
            super.addHeader(name, rewriteContentDisposition(name, value));
        }

        private String rewriteContentDisposition(String name, String value) {
            if (name == null || value == null) {
                return value;
            }
            if (!"content-disposition".equalsIgnoreCase(name)) {
                return value;
            }
            String lowerValue = value.toLowerCase(Locale.ROOT);
            if (!lowerValue.startsWith("attachment")) {
                return value;
            }
            return "attachment;filename=" + encodedFileName;
        }
    }
}
