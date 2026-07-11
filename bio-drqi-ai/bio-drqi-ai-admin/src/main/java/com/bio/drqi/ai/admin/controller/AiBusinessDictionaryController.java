package com.bio.drqi.ai.admin.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.admin.service.AiAdminConfigService;
import com.bio.drqi.ai.dao.domain.AiBusinessDictionary;
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
 * AI 业务词典维护接口。
 */
@RestController
@RequestMapping("/ai/admin/config/business-dictionaries")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiBusinessDictionaryController {

    @Resource
    private AiAdminConfigService aiAdminConfigService;

    @GetMapping("/list")
    @WebLog(desc = "AI业务词典-列表")
    public ResponseResult<List<AiBusinessDictionary>> list(String status, String dictType, String domain) {
        return ResponseResult.getSuccess(aiAdminConfigService.listBusinessDictionaries(status, dictType, domain));
    }

    @GetMapping("/detail")
    @WebLog(desc = "AI业务词典-详情")
    public ResponseResult<AiBusinessDictionary> detail(@RequestParam Long id) {
        return ResponseResult.getSuccess(aiAdminConfigService.detailBusinessDictionary(id));
    }

    @PostMapping("/save")
    @WebLog(desc = "AI业务词典-保存")
    public ResponseResult<String> save(@RequestBody AiBusinessDictionary entity) {
        aiAdminConfigService.saveBusinessDictionary(entity);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/enable")
    @WebLog(desc = "AI业务词典-启用")
    public ResponseResult<String> enable(@RequestParam Long id) {
        aiAdminConfigService.enableBusinessDictionary(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/disable")
    @WebLog(desc = "AI业务词典-禁用")
    public ResponseResult<String> disable(@RequestParam Long id) {
        aiAdminConfigService.disableBusinessDictionary(id);
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/delete")
    @WebLog(desc = "AI业务词典-删除")
    public ResponseResult<String> delete(@RequestParam Long id) {
        aiAdminConfigService.deleteBusinessDictionary(id);
        return ResponseResult.getSuccess("ok");
    }
}
