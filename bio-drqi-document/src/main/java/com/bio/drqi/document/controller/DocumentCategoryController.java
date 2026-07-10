package com.bio.drqi.document.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.document.domain.DocCategoryInfo;
import com.bio.drqi.document.dto.DocumentCategorySaveDTO;
import com.bio.drqi.document.service.DocumentCategoryService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin/document/category")
public class DocumentCategoryController {

    @Resource
    private DocumentCategoryService documentCategoryService;

    @GetMapping("/list")
    public ResponseResult<List<DocCategoryInfo>> list(@RequestParam(required = false) String categoryType) {
        return ResponseResult.getSuccess(documentCategoryService.list(categoryType));
    }

    @GetMapping("/counts")
    public ResponseResult<Map<Long, Long>> counts(@RequestParam(required = false) String categoryType) {
        return ResponseResult.getSuccess(documentCategoryService.countDocuments(categoryType));
    }

    @PostMapping("/save")
    public ResponseResult<Long> save(@RequestBody DocumentCategorySaveDTO dto) {
        return ResponseResult.getSuccess(documentCategoryService.save(dto));
    }

    @GetMapping("/delete")
    public ResponseResult<String> delete(@RequestParam Long id) {
        documentCategoryService.delete(id);
        return ResponseResult.getSuccess("成功");
    }
}
