package com.bio.drqi.ai.admin.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.admin.service.AiAdminConfigService;
import com.bio.drqi.ai.dao.domain.AiSemanticPattern;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

/**
 * AI 语义规则维护接口。
 */
@RestController
@RequestMapping("/ai/admin/config/semantic-patterns")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiSemanticPatternController {

    @Resource
    private AiAdminConfigService aiAdminConfigService;

    @GetMapping("/list")
    @WebLog(desc = "AI语义规则-列表")
    public ResponseResult<List<AiSemanticPattern>> list(String status, String patternType, String domain) {
        return ResponseResult.getSuccess(aiAdminConfigService.listSemanticPatterns(status, patternType, domain));
    }

    @GetMapping("/detail")
    @WebLog(desc = "AI语义规则-详情")
    public ResponseResult<AiSemanticPattern> detail(@RequestParam Long id) {
        return ResponseResult.getSuccess(aiAdminConfigService.detailSemanticPattern(id));
    }

    @PostMapping("/save")
    @WebLog(desc = "AI语义规则-保存")
    public ResponseResult<String> save(@RequestBody AiSemanticPattern entity) {
        aiAdminConfigService.saveSemanticPattern(entity);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/enable")
    @WebLog(desc = "AI语义规则-启用")
    public ResponseResult<String> enable(@RequestParam Long id) {
        aiAdminConfigService.enableSemanticPattern(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/disable")
    @WebLog(desc = "AI语义规则-禁用")
    public ResponseResult<String> disable(@RequestParam Long id) {
        aiAdminConfigService.disableSemanticPattern(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/delete")
    @WebLog(desc = "AI语义规则-删除")
    public ResponseResult<String> delete(@RequestParam Long id) {
        aiAdminConfigService.deleteSemanticPattern(id);
        return ResponseResult.getSuccess("ok");
    }
}
