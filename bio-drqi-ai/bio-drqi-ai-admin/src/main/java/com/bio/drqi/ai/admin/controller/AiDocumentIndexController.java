package com.bio.drqi.ai.admin.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.admin.service.AiDocumentIndexService;
import com.bio.drqi.ai.dto.rag.AiDocumentIndexDeleteReqDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentIndexRspDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentIndexUpsertReqDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentPermissionRefreshReqDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentSearchReqDTO;
import com.bio.drqi.ai.dto.rag.AiDocumentSearchRspDTO;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 文档 RAG 索引接口。
 */
@RestController
@RequestMapping("/ai/document")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiDocumentIndexController {

    @Resource
    private AiDocumentIndexService aiDocumentIndexService;

    @PostMapping("/index/upsert")
    @WebLog(desc = "AI文档索引写入")
    public ResponseResult<AiDocumentIndexRspDTO> upsert(@Validated @RequestBody AiDocumentIndexUpsertReqDTO reqDTO) {
        return ResponseResult.getSuccess(aiDocumentIndexService.upsert(reqDTO));
    }

    @PostMapping("/index/delete")
    @WebLog(desc = "AI文档索引删除")
    public ResponseResult<AiDocumentIndexRspDTO> delete(@Validated @RequestBody AiDocumentIndexDeleteReqDTO reqDTO) {
        return ResponseResult.getSuccess(aiDocumentIndexService.delete(reqDTO));
    }

    @PostMapping("/index/permission-refresh")
    @WebLog(desc = "AI文档索引权限刷新")
    public ResponseResult<AiDocumentIndexRspDTO> refreshPermission(@Validated @RequestBody AiDocumentPermissionRefreshReqDTO reqDTO) {
        return ResponseResult.getSuccess(aiDocumentIndexService.refreshPermission(reqDTO));
    }

    @PostMapping("/search")
    @WebLog(desc = "AI文档语义检索")
    public ResponseResult<AiDocumentSearchRspDTO> search(@Validated @RequestBody AiDocumentSearchReqDTO reqDTO) {
        return ResponseResult.getSuccess(aiDocumentIndexService.search(reqDTO));
    }
}
