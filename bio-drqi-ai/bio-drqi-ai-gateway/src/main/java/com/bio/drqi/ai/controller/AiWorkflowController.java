package com.bio.drqi.ai.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.dto.workflow.AiWorkflowExecuteReqDTO;
import com.bio.drqi.ai.dto.workflow.AiWorkflowExecuteRspDTO;
import com.bio.drqi.ai.dto.workflow.AiWorkflowSaveReqDTO;
import com.bio.drqi.ai.entity.AiWorkflowDefinition;
import com.bio.drqi.ai.service.AiWorkflowService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ai/admin/workflows")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true")
public class AiWorkflowController {

    @Resource
    private AiWorkflowService aiWorkflowService;

    @PostMapping("/page")
    @WebLog(desc = "AI Workflow分页")
    public ResponseResult<Page<AiWorkflowDefinition>> page(@RequestBody AiPageReqDTO reqDTO) {
        return ResponseResult.getSuccess(aiWorkflowService.page(reqDTO));
    }

    @GetMapping("/detail")
    @WebLog(desc = "AI Workflow详情")
    public ResponseResult<AiWorkflowDefinition> detail(@RequestParam Long id) {
        return ResponseResult.getSuccess(aiWorkflowService.detail(id));
    }

    @PostMapping("/save")
    @WebLog(desc = "AI Workflow保存")
    public ResponseResult<Boolean> save(@RequestBody AiWorkflowSaveReqDTO reqDTO) {
        aiWorkflowService.save(reqDTO);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @GetMapping("/delete")
    @WebLog(desc = "AI Workflow删除")
    public ResponseResult<Boolean> delete(@RequestParam Long id) {
        aiWorkflowService.delete(id);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @PostMapping("/execute")
    @WebLog(desc = "AI Workflow执行")
    public ResponseResult<AiWorkflowExecuteRspDTO> execute(@RequestBody AiWorkflowExecuteReqDTO reqDTO) {
        return ResponseResult.getSuccess(aiWorkflowService.execute(reqDTO));
    }
}
