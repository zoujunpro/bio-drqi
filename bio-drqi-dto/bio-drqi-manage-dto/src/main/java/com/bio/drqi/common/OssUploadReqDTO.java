package com.bio.drqi.common;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class OssUploadReqDTO {

    private MultipartFile file;
    /**文件路径可以为空，保存到临时目录*/
    private String filePath;
}
