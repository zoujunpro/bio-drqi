package com.bio.drqi.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.entity.AiIntentKeyword;
import com.bio.drqi.ai.mapper.AiIntentKeywordMapper;
import com.bio.drqi.ai.router.IntentRouter;
import com.bio.drqi.ai.service.AiIntentKeywordService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class AiIntentKeywordServiceImpl implements AiIntentKeywordService {

    @Resource
    private AiProperties aiProperties;

    @Resource
    private AiIntentKeywordMapper aiIntentKeywordMapper;

    @Override
    public List<String> listKeywords(String intent) {
        List<AiIntentKeyword> dbKeywords = listDbKeywordRules(intent);
        if (!dbKeywords.isEmpty()) {
            return dbKeywords.stream().map(AiIntentKeyword::getKeyword).collect(Collectors.toList());
        }
        return listConfigKeywords(intent);
    }

    @Override
    public List<AiIntentKeyword> listKeywordRules() {
        List<AiIntentKeyword> dbRules = listDbKeywordRules(null);
        if (!dbRules.isEmpty()) {
            return dbRules;
        }
        List<AiIntentKeyword> rules = new ArrayList<>();
        addConfigRules(rules, IntentRouter.INTENT_REPORT_EXPORT, aiProperties.getIntent().getReportExportKeywords(), 10);
        addConfigRules(rules, IntentRouter.INTENT_WORKFLOW, aiProperties.getIntent().getWorkflowKeywords(), 8);
        addConfigRules(rules, IntentRouter.INTENT_BUSINESS_QUERY, aiProperties.getIntent().getBusinessQueryKeywords(), 6);
        addConfigRules(rules, IntentRouter.INTENT_CHAT, aiProperties.getIntent().getChatKeywords(), 5);
        return rules;
    }

    private List<AiIntentKeyword> listDbKeywordRules(String intent) {
        try {
            LambdaQueryWrapper<AiIntentKeyword> wrapper = new LambdaQueryWrapper<AiIntentKeyword>()
                            .eq(AiIntentKeyword::getEnabled, 1)
                            .orderByDesc(AiIntentKeyword::getWeight)
                            .orderByAsc(AiIntentKeyword::getId);
            if (intent != null) {
                wrapper.eq(AiIntentKeyword::getIntent, intent);
            }
            return aiIntentKeywordMapper.selectList(wrapper);
        } catch (Exception e) {
            log.warn("AI意图关键词读取数据库失败，降级为配置文件，intent={}", intent, e);
            return new ArrayList<>();
        }
    }

    private void addConfigRules(List<AiIntentKeyword> rules, String intent, List<String> keywords, int weight) {
        if (keywords == null || keywords.isEmpty()) {
            return;
        }
        for (String keyword : keywords) {
            AiIntentKeyword item = new AiIntentKeyword();
            item.setIntent(intent);
            item.setKeyword(keyword);
            item.setWeight(weight);
            item.setEnabled(1);
            rules.add(item);
        }
    }

    private List<String> listConfigKeywords(String intent) {
        if (IntentRouter.INTENT_REPORT_EXPORT.equals(intent)) {
            return aiProperties.getIntent().getReportExportKeywords();
        }
        if (IntentRouter.INTENT_WORKFLOW.equals(intent)) {
            return aiProperties.getIntent().getWorkflowKeywords();
        }
        if (IntentRouter.INTENT_BUSINESS_QUERY.equals(intent)) {
            return aiProperties.getIntent().getBusinessQueryKeywords();
        }
        if (IntentRouter.INTENT_CHAT.equals(intent)) {
            return aiProperties.getIntent().getChatKeywords();
        }
        return new ArrayList<>();
    }
}
