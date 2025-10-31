package com.bio.drqi.manage.controller.project;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.security.annotation.RequirePermissions;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.manage.sample.req.SampleResultFileListPageReqDTO;
import com.bio.drqi.manage.sample.req.SampleResultFileUploadFileReqDTO;
import com.bio.drqi.manage.sample.rsp.SampleResultFileListPageRspDTO;
import com.bio.drqi.manage.service.project.SampleResultFileService;
import com.github.pagehelper.PageInfo;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 取样检测批量检测结果上送管理
 */

@RestController
@RequestMapping("sampleResultFile")
public class SampleResultFileController {

    @Resource
    private SampleResultFileService sampleResultFileService;

    /**
     * 取样检测批量检测结果上送管理-分页查询
     *
     * @param sampleResultFileListPageReqDTO
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "取样检测批量检测结果上送管理-分页查询")
    @RequirePermissions("cer:sampleResultFile:listPage")
    public ResponseResult<PageInfo<SampleResultFileListPageRspDTO>> listPage(SampleResultFileListPageReqDTO sampleResultFileListPageReqDTO) {
        return ResponseResult.getSuccess(sampleResultFileService.listPage(sampleResultFileListPageReqDTO));
    }


    /**
     * 取样检测批量检测结果上送管理-结果文件上送
     *
     * @param sampleResultFileUploadFileReqDTO
     * @return
     */
    @PostMapping("uploadFile")
    @WebLog(desc = "取样检测批量检测结果上送管理-结果文件上送")
    @RequirePermissions("cer:sampleResultFile:uploadFile")
    public synchronized ResponseResult<String> uploadFile(@RequestBody @Validated SampleResultFileUploadFileReqDTO sampleResultFileUploadFileReqDTO) {
        sampleResultFileService.uploadFile(sampleResultFileUploadFileReqDTO);
        return ResponseResult.getSuccess("ok");
    }

}
