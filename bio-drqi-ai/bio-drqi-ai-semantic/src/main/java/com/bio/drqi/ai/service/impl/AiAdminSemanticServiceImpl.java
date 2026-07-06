package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.entity.AiBusinessTerm;
import com.bio.drqi.ai.entity.AiIntentKeyword;
import com.bio.drqi.ai.mapper.AiBusinessTermMapper;
import com.bio.drqi.ai.mapper.AiIntentKeywordMapper;
import com.bio.drqi.ai.service.AiAdminSemanticService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

@Service
public class AiAdminSemanticServiceImpl implements AiAdminSemanticService {

    @Resource
    private AiBusinessTermMapper aiBusinessTermMapper;

    @Resource
    private AiIntentKeywordMapper aiIntentKeywordMapper;

    @Override
    public Page<AiBusinessTerm> termPage(AiPageReqDTO reqDTO) {
        LambdaQueryWrapper<AiBusinessTerm> wrapper = new LambdaQueryWrapper<AiBusinessTerm>()
                .eq(reqDTO.getEnabled() != null, AiBusinessTerm::getEnabled, reqDTO.getEnabled())
                .eq(StrUtil.isNotBlank(reqDTO.getDomain()), AiBusinessTerm::getDomain, reqDTO.getDomain())
                .and(StrUtil.isNotBlank(reqDTO.getKeyword()), item -> item
                        .like(AiBusinessTerm::getPhrase, reqDTO.getKeyword())
                        .or()
                        .like(AiBusinessTerm::getMeaning, reqDTO.getKeyword())
                        .or()
                        .like(AiBusinessTerm::getMetric, reqDTO.getKeyword())
                        .or()
                        .like(AiBusinessTerm::getField, reqDTO.getKeyword()))
                .orderByDesc(AiBusinessTerm::getId);
        return aiBusinessTermMapper.selectPage(page(reqDTO), wrapper);
    }

    @Override
    public void saveTerm(AiBusinessTerm entity) {
        validateTerm(entity);
        Date now = new Date();
        entity.setPhrase(entity.getPhrase().trim());
        entity.setDomain(StrUtil.trimToNull(entity.getDomain()));
        entity.setMeaning(StrUtil.trimToNull(entity.getMeaning()));
        entity.setMetric(StrUtil.trimToNull(entity.getMetric()));
        entity.setField(StrUtil.trimToNull(entity.getField()));
        if (entity.getEnabled() == null) {
            entity.setEnabled(1);
        }
        entity.setUpdateTime(now);
        if (entity.getId() == null) {
            entity.setCreateTime(now);
            aiBusinessTermMapper.insert(entity);
        } else {
            aiBusinessTermMapper.updateById(entity);
        }
    }

    @Override
    public void deleteTerm(Long id) {
        aiBusinessTermMapper.deleteById(id);
    }

    @Override
    public Page<AiIntentKeyword> intentKeywordPage(AiPageReqDTO reqDTO) {
        LambdaQueryWrapper<AiIntentKeyword> wrapper = new LambdaQueryWrapper<AiIntentKeyword>()
                .eq(reqDTO.getEnabled() != null, AiIntentKeyword::getEnabled, reqDTO.getEnabled())
                .eq(StrUtil.isNotBlank(reqDTO.getIntent()), AiIntentKeyword::getIntent, reqDTO.getIntent())
                .like(StrUtil.isNotBlank(reqDTO.getKeyword()), AiIntentKeyword::getKeyword, reqDTO.getKeyword())
                .orderByDesc(AiIntentKeyword::getWeight)
                .orderByDesc(AiIntentKeyword::getId);
        return aiIntentKeywordMapper.selectPage(page(reqDTO), wrapper);
    }

    @Override
    public void saveIntentKeyword(AiIntentKeyword entity) {
        validateIntentKeyword(entity);
        Date now = new Date();
        entity.setIntent(entity.getIntent().trim());
        entity.setKeyword(entity.getKeyword().trim());
        if (entity.getEnabled() == null) {
            entity.setEnabled(1);
        }
        if (entity.getWeight() == null) {
            entity.setWeight(1);
        }
        entity.setUpdateTime(now);
        if (entity.getId() == null) {
            entity.setCreateTime(now);
            aiIntentKeywordMapper.insert(entity);
        } else {
            aiIntentKeywordMapper.updateById(entity);
        }
    }

    @Override
    public void deleteIntentKeyword(Long id) {
        aiIntentKeywordMapper.deleteById(id);
    }

    private <T> Page<T> page(AiPageReqDTO reqDTO) {
        long pageNum = reqDTO.getPageNum() == null || reqDTO.getPageNum() <= 0 ? 1 : reqDTO.getPageNum();
        long pageSize = reqDTO.getPageSize() == null || reqDTO.getPageSize() <= 0 ? 20 : reqDTO.getPageSize();
        if (pageSize > 200) {
            pageSize = 200;
        }
        return new Page<T>(pageNum, pageSize);
    }

    private void validateTerm(AiBusinessTerm entity) {
        if (entity == null) {
            throw new BusinessException("业务术语不能为空");
        }
        if (StrUtil.isBlank(entity.getPhrase())) {
            throw new BusinessException("业务术语短语不能为空");
        }
        if (StrUtil.isBlank(entity.getMeaning()) && StrUtil.isBlank(entity.getMetric()) && StrUtil.isBlank(entity.getField())) {
            throw new BusinessException("业务术语至少需要填写含义、指标或字段");
        }
    }

    private void validateIntentKeyword(AiIntentKeyword entity) {
        if (entity == null) {
            throw new BusinessException("意图关键词不能为空");
        }
        if (StrUtil.isBlank(entity.getIntent())) {
            throw new BusinessException("意图不能为空");
        }
        if (StrUtil.isBlank(entity.getKeyword())) {
            throw new BusinessException("关键词不能为空");
        }
    }
}
