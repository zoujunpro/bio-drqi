package com.bio.drqi.applet.dto.common;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class OssUploadBase64ReqDTO {

    private String fileName;
    /**
     * 文件路径可以为空，保存到临时目录
     */
    private String filePath;

    private String base64;
}
