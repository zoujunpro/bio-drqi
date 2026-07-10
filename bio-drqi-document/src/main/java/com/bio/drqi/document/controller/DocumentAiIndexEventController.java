package com.bio.drqi.document.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.document.domain.DocAiIndexEvent;
import com.bio.drqi.document.service.DocumentAiIndexEventService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/admin/document/ai-index-events")
public class DocumentAiIndexEventController {

    @Resource
    private DocumentAiIndexEventService documentAiIndexEventService;

    @GetMapping("/page")
    public ResponseResult<Page<DocAiIndexEvent>> page(@RequestParam(required = false) Integer pageNum,
                                                      @RequestParam(required = false) Integer pageSize,
                                                      @RequestParam(required = false) String status,
                                                      @RequestParam(required = false) String eventType) {
        return ResponseResult.getSuccess(documentAiIndexEventService.page(pageNum, pageSize, status, eventType));
    }
}
