package com.bio.drqi.manage.controller;

import com.bio.cer.common.OssUploadReqDTO;
import com.bio.cer.common.OssUploadRspDTO;
import com.bio.cer.service.common.CommonService;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;

import javax.annotation.Resource;
import javax.validation.constraints.NotBlank;

/**
 * 公共
 */
@RestController
@RequestMapping("/common")
@Slf4j
public class CommonController {

    @Resource
    private CommonService commonService;

    /**
     * 文件上传
     *
     * @return
     */
    @RequestMapping("/upload")
    @WebLog(desc = "文件临时上传")
    public ResponseResult<OssUploadRspDTO> upload(OssUploadReqDTO ossUploadReqDTO) {
        return ResponseResult.getSuccess(commonService.upload(ossUploadReqDTO));
    }


    /**
     * 获取文件oss临时下载地址
     *
     * @param ossFileObject
     * @return
     */
    @GetMapping("/getOssUrl")
    @WebLog(desc = "获取文件oss临时下载地址")
    public ResponseResult<String> getOssUrl(@RequestParam @Validated @NotBlank(message = "参数缺失") String ossFileObject) {
        return ResponseResult.getSuccess(commonService.getOssUrl(ossFileObject));
    }

    /**
     * excel解析
     *
     * @return
     */

    @GetMapping("/parseExcelData")
    public ResponseResult<Object> parseExcelData(@RequestParam @Validated @NotBlank(message = "参数缺失:excelPath") String excelPath) {
        return ResponseResult.getSuccess(commonService.parseExcelData(excelPath));
    }

    /**
     * 获取miNio上传链接
     */
    @GetMapping("/getPresignedObjectUrl")
    @WebLog(desc = "获取miNio上传链接")
    public ResponseResult<String> getPresignedObjectUrl(@RequestParam String objectName) {
        return ResponseResult.getSuccess(commonService.getPresignedObjectUrl(objectName));
    }

    /**
     * 获取质粒详情
     * @param plasmidId
     * @return
     */
    @GetMapping("/getPlasmidDetail")
    @WebLog(desc = "获取质粒详情")
    public ResponseResult<Object> getPlasmidDetail(@RequestParam @Validated String plasmidId) {
        return ResponseResult.getSuccess(commonService.getPlasmidDetail(plasmidId));
    }

}
