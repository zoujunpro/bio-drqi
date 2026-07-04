package com.bio.drqi.ai.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.entity.AiBusinessTerm;
import com.bio.drqi.ai.entity.AiIntentKeyword;
import com.bio.drqi.ai.mapper.AiBusinessTermMapper;
import com.bio.drqi.ai.mapper.AiIntentKeywordMapper;
import com.bio.drqi.ai.router.IntentRouter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@Slf4j
public class AiMetadataInitializer implements ApplicationRunner {

    @Resource
    private AiIntentKeywordMapper aiIntentKeywordMapper;

    @Resource
    private AiBusinessTermMapper aiBusinessTermMapper;

    @Resource
    private AiProperties aiProperties;

    @Override
    public void run(ApplicationArguments args) {
        try {
            initIntentKeywords();
            initBusinessTerms();
        } catch (Exception e) {
            log.warn("AI元数据初始化失败，不影响服务启动", e);
        }
    }

    private void initIntentKeywords() {
        insertKeywords(IntentRouter.INTENT_REPORT_EXPORT, aiProperties.getIntent().getReportExportKeywords());
        insertKeywords(IntentRouter.INTENT_WORKFLOW, aiProperties.getIntent().getWorkflowKeywords());
        insertKeywords(IntentRouter.INTENT_BUSINESS_QUERY, aiProperties.getIntent().getBusinessQueryKeywords());
        insertKeywords(IntentRouter.INTENT_CHAT, aiProperties.getIntent().getChatKeywords());
    }

    private void insertKeywords(String intent, List<String> keywords) {
        if (CollectionUtil.isEmpty(keywords)) {
            return;
        }
        int weight = keywords.size();
        for (String keyword : keywords) {
            if (StrUtil.isBlank(keyword)) {
                continue;
            }
            AiIntentKeyword existing = aiIntentKeywordMapper.selectOne(new LambdaQueryWrapper<AiIntentKeyword>()
                    .eq(AiIntentKeyword::getIntent, intent)
                    .eq(AiIntentKeyword::getKeyword, keyword)
                    .last("limit 1"));
            if (existing == null) {
                AiIntentKeyword entity = new AiIntentKeyword();
                entity.setIntent(intent);
                entity.setKeyword(keyword);
                entity.setWeight(weight--);
                entity.setEnabled(1);
                aiIntentKeywordMapper.insert(entity);
            } else {
                existing.setWeight(weight--);
                existing.setEnabled(1);
                aiIntentKeywordMapper.updateById(existing);
            }
        }
    }

    private void initBusinessTerms() {
        if (CollectionUtil.isEmpty(aiProperties.getTerms())) {
            return;
        }
        for (AiProperties.Term term : aiProperties.getTerms()) {
            if (term == null || StrUtil.isBlank(term.getPhrase()) || StrUtil.isBlank(term.getMeaning())) {
                continue;
            }
            Long count = aiBusinessTermMapper.selectCount(new LambdaQueryWrapper<AiBusinessTerm>()
                    .eq(AiBusinessTerm::getPhrase, term.getPhrase())
                    .eq(StrUtil.isNotBlank(term.getDomain()), AiBusinessTerm::getDomain, term.getDomain())
                    .eq(StrUtil.isNotBlank(term.getMetric()), AiBusinessTerm::getMetric, term.getMetric())
                    .eq(StrUtil.isNotBlank(term.getField()), AiBusinessTerm::getField, term.getField()));
            if (count != null && count > 0) {
                continue;
            }
            AiBusinessTerm entity = new AiBusinessTerm();
            entity.setPhrase(term.getPhrase());
            entity.setDomain(term.getDomain());
            entity.setMeaning(term.getMeaning());
            entity.setMetric(term.getMetric());
            entity.setField(term.getField());
            entity.setEnabled(1);
            aiBusinessTermMapper.insert(entity);
        }
    }
}
