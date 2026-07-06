package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.entity.AiQueryAuditLog;
import com.bio.drqi.ai.mapper.AiQueryAuditLogMapper;
import com.bio.drqi.ai.service.AiAdminObserveService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class AiAdminObserveServiceImpl implements AiAdminObserveService {

    @Resource
    private AiQueryAuditLogMapper aiQueryAuditLogMapper;

    @Override
    public Page<AiQueryAuditLog> auditPage(AiPageReqDTO reqDTO) {
        LambdaQueryWrapper<AiQueryAuditLog> wrapper = new LambdaQueryWrapper<AiQueryAuditLog>()
                .eq(StrUtil.isNotBlank(reqDTO.getScenario()), AiQueryAuditLog::getScenario, reqDTO.getScenario())
                .eq(StrUtil.isNotBlank(reqDTO.getIntent()), AiQueryAuditLog::getIntent, reqDTO.getIntent())
                .eq(StrUtil.isNotBlank(reqDTO.getDomain()), AiQueryAuditLog::getDomain, reqDTO.getDomain())
                .eq(StrUtil.isNotBlank(reqDTO.getSuccess()), AiQueryAuditLog::getSuccess, reqDTO.getSuccess())
                .and(StrUtil.isNotBlank(reqDTO.getKeyword()), item -> item
                        .like(AiQueryAuditLog::getQuestion, reqDTO.getKeyword())
                        .or()
                        .like(AiQueryAuditLog::getConversationId, reqDTO.getKeyword()))
                .orderByDesc(AiQueryAuditLog::getId);
        return aiQueryAuditLogMapper.selectPage(page(reqDTO), wrapper);
    }

    private <T> Page<T> page(AiPageReqDTO reqDTO) {
        long pageNum = reqDTO.getPageNum() == null || reqDTO.getPageNum() <= 0 ? 1 : reqDTO.getPageNum();
        long pageSize = reqDTO.getPageSize() == null || reqDTO.getPageSize() <= 0 ? 20 : reqDTO.getPageSize();
        if (pageSize > 200) {
            pageSize = 200;
        }
        return new Page<T>(pageNum, pageSize);
    }
}
