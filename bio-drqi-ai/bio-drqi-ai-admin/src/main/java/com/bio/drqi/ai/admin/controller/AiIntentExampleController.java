package com.bio.drqi.ai.admin.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.admin.service.AiAdminConfigService;
import com.bio.drqi.ai.dao.domain.AiIntentExample;
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
 * AI 意图样例维护接口。
 */
@RestController
@RequestMapping("/ai/admin/config/intent-examples")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiIntentExampleController {

    @Resource
    private AiAdminConfigService aiAdminConfigService;

    @GetMapping("/list")
    @WebLog(desc = "AI意图样例-列表")
    public ResponseResult<List<AiIntentExample>> list(String status, String intentCode) {
        return ResponseResult.getSuccess(aiAdminConfigService.listIntentExamples(status, intentCode));
    }

    @GetMapping("/detail")
    @WebLog(desc = "AI意图样例-详情")
    public ResponseResult<AiIntentExample> detail(@RequestParam Long id) {
        return ResponseResult.getSuccess(aiAdminConfigService.detailIntentExample(id));
    }

    @PostMapping("/save")
    @WebLog(desc = "AI意图样例-保存")
    public ResponseResult<String> save(@RequestBody AiIntentExample entity) {
        aiAdminConfigService.saveIntentExample(entity);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/enable")
    @WebLog(desc = "AI意图样例-启用")
    public ResponseResult<String> enable(@RequestParam Long id) {
        aiAdminConfigService.enableIntentExample(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/disable")
    @WebLog(desc = "AI意图样例-禁用")
    public ResponseResult<String> disable(@RequestParam Long id) {
        aiAdminConfigService.disableIntentExample(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/delete")
    @WebLog(desc = "AI意图样例-删除")
    public ResponseResult<String> delete(@RequestParam Long id) {
        aiAdminConfigService.deleteIntentExample(id);
        return ResponseResult.getSuccess("ok");
    }
}
