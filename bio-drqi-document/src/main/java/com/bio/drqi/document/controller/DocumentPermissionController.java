package com.bio.drqi.document.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.document.domain.DocPermissionInfo;
import com.bio.drqi.document.dto.DocumentPermissionSaveDTO;
import com.bio.drqi.document.service.DocumentPermissionService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/admin/document/permission")
public class DocumentPermissionController {

    @Resource
    private DocumentPermissionService documentPermissionService;

    @GetMapping("/list")
    public ResponseResult<List<DocPermissionInfo>> list(@RequestParam String resourceType, @RequestParam Long resourceId) {
        return ResponseResult.getSuccess(documentPermissionService.list(resourceType, resourceId));
    }

    @PostMapping("/save")
    public ResponseResult<String> save(@RequestBody DocumentPermissionSaveDTO dto) {
        documentPermissionService.save(dto);
        return ResponseResult.getSuccess("成功");
    }
}
