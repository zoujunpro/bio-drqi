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
        List<String> dbKeywords = listDbKeywords(intent);
        if (!dbKeywords.isEmpty()) {
            return dbKeywords;
        }
        return listConfigKeywords(intent);
    }

    private List<String> listDbKeywords(String intent) {
        try {
            return aiIntentKeywordMapper.selectList(new LambdaQueryWrapper<AiIntentKeyword>()
                            .eq(AiIntentKeyword::getIntent, intent)
                            .eq(AiIntentKeyword::getEnabled, 1)
                            .orderByDesc(AiIntentKeyword::getWeight)
                            .orderByAsc(AiIntentKeyword::getId))
                    .stream()
                    .map(AiIntentKeyword::getKeyword)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("AI意图关键词读取数据库失败，降级为配置文件，intent={}", intent, e);
            return new ArrayList<>();
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
