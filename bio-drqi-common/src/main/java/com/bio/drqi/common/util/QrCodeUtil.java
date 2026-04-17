package com.bio.drqi.common.util;

import cn.hutool.extra.qrcode.QrConfig;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;

public final class QrCodeUtil {

    private QrCodeUtil() {
    }

    public static String toBase64DataUri(String content) {
        return toBase64DataUri(content, 144, 144);
    }

    public static String toBase64DataUri(String content, int width, int height) {
        if (content == null || content.trim().isEmpty()) {
            throw new IllegalArgumentException("二维码内容不能为空");
        }
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            QrConfig config = new QrConfig(width, height);
            BufferedImage bufferedImage = cn.hutool.extra.qrcode.QrCodeUtil.generate(content, config);
            ImageIO.write(bufferedImage, "png", outputStream);
            String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
            return "data:image/png;base64," + base64;
        } catch (Exception e) {
            throw new RuntimeException("二维码生成失败", e);
        }
    }
}
