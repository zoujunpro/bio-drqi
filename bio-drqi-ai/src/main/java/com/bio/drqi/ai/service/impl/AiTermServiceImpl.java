package com.bio.drqi.ai.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import cn.hutool.core.util.StrUtil;
import com.bio.drqi.ai.config.AiProperties;
import com.bio.drqi.ai.entity.AiBusinessTerm;
import com.bio.drqi.ai.mapper.AiBusinessTermMapper;
import com.bio.drqi.ai.service.AiTermService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class AiTermServiceImpl implements AiTermService {

    @Resource
    private AiProperties aiProperties;

    @Resource
    private AiBusinessTermMapper aiBusinessTermMapper;

    @Override
    public List<AiProperties.Term> recall(String question) {
        List<AiProperties.Term> dbTerms = recallDbTerms(question);
        if (!dbTerms.isEmpty()) {
            return dbTerms;
        }
        return recallConfigTerms(question);
    }

    private List<AiProperties.Term> recallDbTerms(String question) {
        List<AiProperties.Term> result = new ArrayList<>();
        if (StrUtil.isBlank(question)) {
            return result;
        }
        try {
            List<AiBusinessTerm> terms = aiBusinessTermMapper.selectList(new LambdaQueryWrapper<AiBusinessTerm>()
                    .eq(AiBusinessTerm::getEnabled, 1)
                    .orderByAsc(AiBusinessTerm::getId)
                    .last("limit 200"));
            for (AiBusinessTerm item : terms) {
                if (item == null || StrUtil.isBlank(item.getPhrase())) {
                    continue;
                }
                AiProperties.Term term = toTerm(item);
                if (question.contains(term.getPhrase())) {
                    result.add(term);
                }
                if (result.size() >= 10) {
                    break;
                }
            }
        } catch (Exception e) {
            log.warn("AI业务术语读取数据库失败，降级为配置文件", e);
            return new ArrayList<>();
        }
        return result;
    }

    private AiProperties.Term toTerm(AiBusinessTerm item) {
        AiProperties.Term term = new AiProperties.Term();
        term.setPhrase(item.getPhrase());
        term.setDomain(item.getDomain());
        term.setMeaning(item.getMeaning());
        term.setMetric(item.getMetric());
        term.setField(item.getField());
        return term;
    }

    private List<AiProperties.Term> recallConfigTerms(String question) {
        List<AiProperties.Term> result = new ArrayList<>();
        if (StrUtil.isBlank(question) || aiProperties.getTerms() == null) {
            return result;
        }
        for (AiProperties.Term term : aiProperties.getTerms()) {
            if (term == null || StrUtil.isBlank(term.getPhrase())) {
                continue;
            }
            if (question.contains(term.getPhrase())) {
                result.add(term);
            }
            if (result.size() >= 10) {
                break;
            }
        }
        return result;
    }
}
