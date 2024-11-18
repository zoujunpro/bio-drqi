package com.bio.cer.common;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @author ZouJun
 * @version V1.0
 * @Description: TODO
 * @Date: 2022/12/14 22:03
 * @ClassName: PresignedObjectUrlReqDTO
 */
@Data
public class PresignedObjectUrlReqDTO {
    /**上传的文件路径 比如 demo/test.pdf 不带前缀斜杠*/
    @NotBlank(message = "参数必传")
    private String objectName;
}
