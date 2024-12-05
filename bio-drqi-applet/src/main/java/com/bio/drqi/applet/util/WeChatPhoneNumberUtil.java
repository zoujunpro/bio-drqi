package com.bio.drqi.applet.util;

import com.bio.common.core.dto.BusinessException;
import lombok.extern.slf4j.Slf4j;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

import java.util.Base64;

@Slf4j
public class WeChatPhoneNumberUtil {
    private static final String ALGORITHM = "AES/CBC/PKCS5Padding";

    public static String decryptPhoneNumber(String encryptedData, String sessionKey, String iv) {
        // Base64 解码
        try {
            byte[] encryptedDataBytes = Base64.getDecoder().decode(encryptedData);
            byte[] sessionKeyBytes = Base64.getDecoder().decode(sessionKey);
            byte[] ivBytes = Base64.getDecoder().decode(iv);

            // 设置AES密钥和初始化向量
            SecretKeySpec secretKeySpec = new SecretKeySpec(sessionKeyBytes, "AES");
            IvParameterSpec ivParameterSpec = new IvParameterSpec(ivBytes);

            // 创建Cipher实例并初始化
            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivParameterSpec);

            // 解密
            byte[] decryptedBytes = cipher.doFinal(encryptedDataBytes);

            // 将解密后的字节转换为字符串
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("微信手机号解谜失败：",e);
            throw new BusinessException("微信手机号解谜失败");
        }
    }

}
