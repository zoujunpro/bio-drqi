package com.bio.drqi.ai.admin.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.admin.service.AiAdminConfigService;
import com.bio.drqi.ai.dao.domain.AiTaskTemplate;
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
 * AI 任务模板维护接口。
 */
@RestController
@RequestMapping("/ai/admin/config/task-templates")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiTaskTemplateController {

    @Resource
    private AiAdminConfigService aiAdminConfigService;

    @GetMapping("/list")
    @WebLog(desc = "AI任务模板-列表")
    public ResponseResult<List<AiTaskTemplate>> list(String status, String templateCode, String intentCode) {
        return ResponseResult.getSuccess(aiAdminConfigService.listTaskTemplates(status, templateCode, intentCode));
    }

    @GetMapping("/detail")
    @WebLog(desc = "AI任务模板-详情")
    public ResponseResult<AiTaskTemplate> detail(@RequestParam Long id) {
        return ResponseResult.getSuccess(aiAdminConfigService.detailTaskTemplate(id));
    }

    @PostMapping("/save")
    @WebLog(desc = "AI任务模板-保存")
    public ResponseResult<String> save(@RequestBody AiTaskTemplate entity) {
        aiAdminConfigService.saveTaskTemplate(entity);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/enable")
    @WebLog(desc = "AI任务模板-启用")
    public ResponseResult<String> enable(@RequestParam Long id) {
        aiAdminConfigService.enableTaskTemplate(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/disable")
    @WebLog(desc = "AI任务模板-禁用")
    public ResponseResult<String> disable(@RequestParam Long id) {
        aiAdminConfigService.disableTaskTemplate(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/delete")
    @WebLog(desc = "AI任务模板-删除")
    public ResponseResult<String> delete(@RequestParam Long id) {
        aiAdminConfigService.deleteTaskTemplate(id);
        return ResponseResult.getSuccess("ok");
    }
}
