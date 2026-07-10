package com.bio.drqi.document.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.document.domain.DocFileInfo;
import com.bio.drqi.document.domain.DocOperationLog;
import com.bio.drqi.document.domain.DocShareInfo;
import com.bio.drqi.document.domain.DocVersionHis;
import com.bio.drqi.document.dto.DocumentDownloadRspDTO;
import com.bio.drqi.document.dto.DocumentPageReqDTO;
import com.bio.drqi.document.dto.DocumentUploadRspDTO;
import com.bio.drqi.document.service.DocumentFileService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/document/file")
public class DocumentFileController {

    @Resource
    private DocumentFileService documentFileService;

    @PostMapping("/page")
    public ResponseResult<Page<DocFileInfo>> page(@RequestBody DocumentPageReqDTO req) {
        return ResponseResult.getSuccess(documentFileService.page(req));
    }

    @PostMapping("/my-page")
    public ResponseResult<Page<DocFileInfo>> myPage(@RequestBody DocumentPageReqDTO req) {
        return ResponseResult.getSuccess(documentFileService.myPage(req));
    }

    @PostMapping("/favorite-page")
    public ResponseResult<Page<DocFileInfo>> favoritePage(@RequestBody DocumentPageReqDTO req) {
        return ResponseResult.getSuccess(documentFileService.favoritePage(req));
    }

    @PostMapping("/share-page")
    public ResponseResult<Page<DocFileInfo>> sharePage(@RequestBody DocumentPageReqDTO req) {
        return ResponseResult.getSuccess(documentFileService.sharePage(req));
    }

    @PostMapping("/recent-page")
    public ResponseResult<Page<DocFileInfo>> recentPage(@RequestBody DocumentPageReqDTO req) {
        return ResponseResult.getSuccess(documentFileService.recentPage(req));
    }

    @PostMapping("/recycle-page")
    public ResponseResult<Page<DocFileInfo>> recyclePage(@RequestBody DocumentPageReqDTO req) {
        return ResponseResult.getSuccess(documentFileService.recyclePage(req));
    }

    @PostMapping("/operation-logs")
    public ResponseResult<Page<DocOperationLog>> operationLogs(@RequestParam(required = false) Long id,
                                                               @RequestBody DocumentPageReqDTO req) {
        return ResponseResult.getSuccess(documentFileService.operationLogPage(id, req));
    }

    @GetMapping("/detail")
    public ResponseResult<DocFileInfo> detail(@RequestParam Long id) {
        return ResponseResult.getSuccess(documentFileService.detail(id));
    }

    @GetMapping("/versions")
    public ResponseResult<List<DocVersionHis>> versions(@RequestParam Long id) {
        return ResponseResult.getSuccess(documentFileService.versions(id));
    }

    @PostMapping("/upload")
    public ResponseResult<DocumentUploadRspDTO> upload(@RequestParam(required = false) Long categoryId,
                                                       @RequestParam(required = false) String spaceType,
                                                       @RequestParam(required = false) String remark,
                                                       @RequestParam("file") MultipartFile file) {
        return ResponseResult.getSuccess(documentFileService.upload(categoryId, spaceType, remark, file));
    }

    @PostMapping("/uploadVersion")
    public ResponseResult<DocumentUploadRspDTO> uploadVersion(@RequestParam Long id,
                                                              @RequestParam(required = false) String changeLog,
                                                              @RequestParam("file") MultipartFile file) {
        return ResponseResult.getSuccess(documentFileService.uploadVersion(id, changeLog, file));
    }

    @GetMapping("/downloadUrl")
    public ResponseResult<DocumentDownloadRspDTO> downloadUrl(@RequestParam Long id,
                                                              @RequestParam(required = false) Long versionId) {
        return ResponseResult.getSuccess(documentFileService.downloadUrl(id, versionId));
    }

    @GetMapping("/delete")
    public ResponseResult<String> delete(@RequestParam Long id) {
        documentFileService.delete(id);
        return ResponseResult.getSuccess("成功");
    }

    @GetMapping("/restore")
    public ResponseResult<String> restore(@RequestParam Long id) {
        documentFileService.restore(id);
        return ResponseResult.getSuccess("成功");
    }

    @GetMapping("/deleteForever")
    public ResponseResult<String> deleteForever(@RequestParam Long id) {
        documentFileService.deleteForever(id);
        return ResponseResult.getSuccess("成功");
    }

    @GetMapping("/favorite")
    public ResponseResult<String> favorite(@RequestParam Long id) {
        documentFileService.addFavorite(id);
        return ResponseResult.getSuccess("成功");
    }

    @GetMapping("/unfavorite")
    public ResponseResult<String> unfavorite(@RequestParam Long id) {
        documentFileService.removeFavorite(id);
        return ResponseResult.getSuccess("成功");
    }

    @GetMapping("/share")
    public ResponseResult<DocShareInfo> share(@RequestParam Long id,
                                              @RequestParam(required = false) Integer expireDays) {
        return ResponseResult.getSuccess(documentFileService.addShare(id, expireDays));
    }

    @GetMapping("/cancelShare")
    public ResponseResult<String> cancelShare(@RequestParam Long id) {
        documentFileService.cancelShare(id);
        return ResponseResult.getSuccess("成功");
    }

    @GetMapping("/shareDetail")
    public ResponseResult<DocFileInfo> shareDetail(@RequestParam String token) {
        return ResponseResult.getSuccess(documentFileService.shareDetail(token));
    }

    @GetMapping("/shareDownloadUrl")
    public ResponseResult<DocumentDownloadRspDTO> shareDownloadUrl(@RequestParam String token) {
        return ResponseResult.getSuccess(documentFileService.shareDownloadUrl(token));
    }
}
