package com.bio.cer.common;

import lombok.Data;

@Data
public class OssUploadRspDTO {
    /**文件原始名称*/
    private String orgFileName;
    /**oss目录地址*/
    private String ossFileObject;
}
