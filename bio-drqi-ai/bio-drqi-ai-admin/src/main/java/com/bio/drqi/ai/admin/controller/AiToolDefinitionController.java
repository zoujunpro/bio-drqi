package com.bio.drqi.ai.admin.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.admin.service.AiAdminConfigService;
import com.bio.drqi.ai.dao.domain.AiToolDefinition;
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
 * AI 工具定义维护接口。
 */
@RestController
@RequestMapping("/ai/admin/config/tools")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiToolDefinitionController {

    @Resource
    private AiAdminConfigService aiAdminConfigService;

    @GetMapping("/list")
    @WebLog(desc = "AI工具定义-列表")
    public ResponseResult<List<AiToolDefinition>> list(String status, String toolCode, String toolType) {
        return ResponseResult.getSuccess(aiAdminConfigService.listTools(status, toolCode, toolType));
    }

    @GetMapping("/detail")
    @WebLog(desc = "AI工具定义-详情")
    public ResponseResult<AiToolDefinition> detail(@RequestParam Long id) {
        return ResponseResult.getSuccess(aiAdminConfigService.detailTool(id));
    }

    @PostMapping("/save")
    @WebLog(desc = "AI工具定义-保存")
    public ResponseResult<String> save(@RequestBody AiToolDefinition entity) {
        aiAdminConfigService.saveTool(entity);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/enable")
    @WebLog(desc = "AI工具定义-启用")
    public ResponseResult<String> enable(@RequestParam Long id) {
        aiAdminConfigService.enableTool(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/disable")
    @WebLog(desc = "AI工具定义-禁用")
    public ResponseResult<String> disable(@RequestParam Long id) {
        aiAdminConfigService.disableTool(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/delete")
    @WebLog(desc = "AI工具定义-删除")
    public ResponseResult<String> delete(@RequestParam Long id) {
        aiAdminConfigService.deleteTool(id);
        return ResponseResult.getSuccess("ok");
    }
}
