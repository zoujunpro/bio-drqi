package com.bio.drqi.ai.admin.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.admin.service.AiAdminConfigService;
import com.bio.drqi.ai.dao.domain.AiIntentToolRel;
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
 * AI 意图工具关系维护接口。
 */
@RestController
@RequestMapping("/ai/admin/config/intent-tool-rels")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiIntentToolRelController {

    @Resource
    private AiAdminConfigService aiAdminConfigService;

    @GetMapping("/list")
    @WebLog(desc = "AI意图工具关系-列表")
    public ResponseResult<List<AiIntentToolRel>> list(String status, String intentCode, String toolCode) {
        return ResponseResult.getSuccess(aiAdminConfigService.listIntentToolRels(status, intentCode, toolCode));
    }

    @GetMapping("/detail")
    @WebLog(desc = "AI意图工具关系-详情")
    public ResponseResult<AiIntentToolRel> detail(@RequestParam Long id) {
        return ResponseResult.getSuccess(aiAdminConfigService.detailIntentToolRel(id));
    }

    @PostMapping("/save")
    @WebLog(desc = "AI意图工具关系-保存")
    public ResponseResult<String> save(@RequestBody AiIntentToolRel entity) {
        aiAdminConfigService.saveIntentToolRel(entity);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/enable")
    @WebLog(desc = "AI意图工具关系-启用")
    public ResponseResult<String> enable(@RequestParam Long id) {
        aiAdminConfigService.enableIntentToolRel(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/disable")
    @WebLog(desc = "AI意图工具关系-禁用")
    public ResponseResult<String> disable(@RequestParam Long id) {
        aiAdminConfigService.disableIntentToolRel(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/delete")
    @WebLog(desc = "AI意图工具关系-删除")
    public ResponseResult<String> delete(@RequestParam Long id) {
        aiAdminConfigService.deleteIntentToolRel(id);
        return ResponseResult.getSuccess("ok");
    }
}
