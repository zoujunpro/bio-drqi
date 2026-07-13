package com.bio.drqi.ai.admin.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.admin.service.AiAdminConfigService;
import com.bio.drqi.ai.dao.domain.AiIntent;
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
 * AI 意图维护接口。
 */
@RestController
@RequestMapping("/ai/admin/config/intents")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiIntentController {

    @Resource
    private AiAdminConfigService aiAdminConfigService;

    @GetMapping("/list")
    @WebLog(desc = "AI意图-列表")
    public ResponseResult<List<AiIntent>> list(String status, String intentCode, String domain) {
        return ResponseResult.getSuccess(aiAdminConfigService.listIntents(status, intentCode, domain));
    }

    @GetMapping("/detail")
    @WebLog(desc = "AI意图-详情")
    public ResponseResult<AiIntent> detail(@RequestParam Long id) {
        return ResponseResult.getSuccess(aiAdminConfigService.detailIntent(id));
    }

    @PostMapping("/save")
    @WebLog(desc = "AI意图-保存")
    public ResponseResult<Void> save(@RequestBody AiIntent entity) {
        aiAdminConfigService.saveIntent(entity);
        return success();
    }

    @GetMapping("/enable")
    @WebLog(desc = "AI意图-启用")
    public ResponseResult<Void> enable(@RequestParam Long id) {
        aiAdminConfigService.enableIntent(id);
        return success();
    }

    @GetMapping("/disable")
    @WebLog(desc = "AI意图-禁用")
    public ResponseResult<Void> disable(@RequestParam Long id) {
        aiAdminConfigService.disableIntent(id);
        return success();
    }

    @GetMapping("/delete")
    @WebLog(desc = "AI意图-删除")
    public ResponseResult<Void> delete(@RequestParam Long id) {
        aiAdminConfigService.deleteIntent(id);
        return success();
    }

    private ResponseResult<Void> success() {
        return ResponseResult.getSuccess(null);
    }
}
