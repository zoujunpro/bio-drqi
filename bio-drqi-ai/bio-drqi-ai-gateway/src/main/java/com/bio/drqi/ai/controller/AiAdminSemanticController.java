package com.bio.drqi.ai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.entity.AiBusinessTerm;
import com.bio.drqi.ai.entity.AiIntentKeyword;
import com.bio.drqi.ai.service.AiAdminSemanticService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ai/admin")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true")
public class AiAdminSemanticController {

    @Resource
    private AiAdminSemanticService aiAdminSemanticService;

    @PostMapping("/terms/page")
    @WebLog(desc = "AI业务术语分页")
    public ResponseResult<Page<AiBusinessTerm>> termPage(@RequestBody AiPageReqDTO reqDTO) {
        return ResponseResult.getSuccess(aiAdminSemanticService.termPage(reqDTO));
    }

    @PostMapping("/terms/save")
    @WebLog(desc = "AI业务术语保存")
    public ResponseResult<Boolean> saveTerm(@RequestBody AiBusinessTerm entity) {
        aiAdminSemanticService.saveTerm(entity);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @GetMapping("/terms/delete")
    @WebLog(desc = "AI业务术语删除")
    public ResponseResult<Boolean> deleteTerm(@RequestParam Long id) {
        aiAdminSemanticService.deleteTerm(id);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @PostMapping("/intent-keywords/page")
    @WebLog(desc = "AI意图关键词分页")
    public ResponseResult<Page<AiIntentKeyword>> intentKeywordPage(@RequestBody AiPageReqDTO reqDTO) {
        return ResponseResult.getSuccess(aiAdminSemanticService.intentKeywordPage(reqDTO));
    }

    @PostMapping("/intent-keywords/save")
    @WebLog(desc = "AI意图关键词保存")
    public ResponseResult<Boolean> saveIntentKeyword(@RequestBody AiIntentKeyword entity) {
        aiAdminSemanticService.saveIntentKeyword(entity);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @GetMapping("/intent-keywords/delete")
    @WebLog(desc = "AI意图关键词删除")
    public ResponseResult<Boolean> deleteIntentKeyword(@RequestParam Long id) {
        aiAdminSemanticService.deleteIntentKeyword(id);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }
}
