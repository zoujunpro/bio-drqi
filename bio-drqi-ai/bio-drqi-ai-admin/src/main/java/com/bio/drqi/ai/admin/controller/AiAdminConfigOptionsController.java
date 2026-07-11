package com.bio.drqi.ai.admin.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.common.enums.AiIntentHandlerTypeEnum;
import com.bio.drqi.ai.common.enums.AiPlanTaskTypeEnum;
import com.bio.drqi.ai.common.enums.AiSemanticPatternTypeEnum;
import com.bio.drqi.ai.common.enums.AiToolRiskLevelEnum;
import com.bio.drqi.ai.common.enums.AiToolTypeEnum;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI 配置后台通用选项接口。
 */
@RestController
@RequestMapping("/ai/admin/config")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", exposedHeaders = "Content-Disposition", allowCredentials = "true")
public class AiAdminConfigOptionsController {

    @GetMapping("/options")
    @WebLog(desc = "AI配置-下拉选项")
    public ResponseResult<Map<String, List<Map<String, String>>>> options() {
        Map<String, List<Map<String, String>>> options = new LinkedHashMap<String, List<Map<String, String>>>();
        options.put("statuses", statusOptions());
        options.put("toolTypes", toolTypeOptions());
        options.put("riskLevels", riskLevelOptions());
        options.put("intentHandlerTypes", intentHandlerTypeOptions());
        options.put("taskTypes", taskTypeOptions());
        options.put("semanticPatternTypes", semanticPatternTypeOptions());
        return ResponseResult.getSuccess(options);
    }

    private List<Map<String, String>> statusOptions() {
        List<Map<String, String>> options = new ArrayList<Map<String, String>>();
        options.add(option("ACTIVE", "启用"));
        options.add(option("DISABLED", "禁用"));
        options.add(option("DELETED", "删除"));
        return options;
    }

    private List<Map<String, String>> toolTypeOptions() {
        List<Map<String, String>> options = new ArrayList<Map<String, String>>();
        for (AiToolTypeEnum value : AiToolTypeEnum.values()) {
            options.add(option(value.getCode(), value.getDesc()));
        }
        return options;
    }

    private List<Map<String, String>> riskLevelOptions() {
        List<Map<String, String>> options = new ArrayList<Map<String, String>>();
        for (AiToolRiskLevelEnum value : AiToolRiskLevelEnum.values()) {
            options.add(option(value.getCode(), value.getDesc()));
        }
        return options;
    }

    private List<Map<String, String>> intentHandlerTypeOptions() {
        List<Map<String, String>> options = new ArrayList<Map<String, String>>();
        for (AiIntentHandlerTypeEnum value : AiIntentHandlerTypeEnum.values()) {
            options.add(option(value.getCode(), value.getDesc()));
        }
        return options;
    }

    private List<Map<String, String>> taskTypeOptions() {
        List<Map<String, String>> options = new ArrayList<Map<String, String>>();
        for (AiPlanTaskTypeEnum value : AiPlanTaskTypeEnum.values()) {
            options.add(option(value.getCode(), value.getDesc()));
        }
        return options;
    }

    private List<Map<String, String>> semanticPatternTypeOptions() {
        List<Map<String, String>> options = new ArrayList<Map<String, String>>();
        for (AiSemanticPatternTypeEnum value : AiSemanticPatternTypeEnum.values()) {
            options.add(option(value.getCode(), value.getDesc()));
        }
        return options;
    }

    private Map<String, String> option(String value, String label) {
        Map<String, String> option = new LinkedHashMap<String, String>();
        option.put("value", value);
        option.put("label", label);
        return option;
    }
}
