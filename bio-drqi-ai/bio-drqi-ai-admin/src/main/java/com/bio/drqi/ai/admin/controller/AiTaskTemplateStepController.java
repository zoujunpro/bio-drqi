package com.bio.drqi.ai.admin.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.admin.service.AiAdminConfigService;
import com.bio.drqi.ai.dao.domain.AiTaskTemplateStep;
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
 * AI 任务模板步骤维护接口。
 */
@RestController
@RequestMapping("/ai/admin/config/task-template-steps")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiTaskTemplateStepController {

    @Resource
    private AiAdminConfigService aiAdminConfigService;

    @GetMapping("/list")
    @WebLog(desc = "AI任务模板步骤-列表")
    public ResponseResult<List<AiTaskTemplateStep>> list(String status, String templateCode) {
        return ResponseResult.getSuccess(aiAdminConfigService.listTaskTemplateSteps(status, templateCode));
    }

    @GetMapping("/detail")
    @WebLog(desc = "AI任务模板步骤-详情")
    public ResponseResult<AiTaskTemplateStep> detail(@RequestParam Long id) {
        return ResponseResult.getSuccess(aiAdminConfigService.detailTaskTemplateStep(id));
    }

    @PostMapping("/save")
    @WebLog(desc = "AI任务模板步骤-保存")
    public ResponseResult<String> save(@RequestBody AiTaskTemplateStep entity) {
        aiAdminConfigService.saveTaskTemplateStep(entity);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/enable")
    @WebLog(desc = "AI任务模板步骤-启用")
    public ResponseResult<String> enable(@RequestParam Long id) {
        aiAdminConfigService.enableTaskTemplateStep(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/disable")
    @WebLog(desc = "AI任务模板步骤-禁用")
    public ResponseResult<String> disable(@RequestParam Long id) {
        aiAdminConfigService.disableTaskTemplateStep(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/delete")
    @WebLog(desc = "AI任务模板步骤-删除")
    public ResponseResult<String> delete(@RequestParam Long id) {
        aiAdminConfigService.deleteTaskTemplateStep(id);
        return ResponseResult.getSuccess("ok");
    }
}
