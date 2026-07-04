package com.bio.drqi.ai.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.entity.AiBusinessTerm;
import com.bio.drqi.ai.entity.AiIntentKeyword;
import com.bio.drqi.ai.entity.AiQueryAuditLog;
import com.bio.drqi.ai.mapper.AiBusinessTermMapper;
import com.bio.drqi.ai.mapper.AiIntentKeywordMapper;
import com.bio.drqi.ai.mapper.AiQueryAuditLogMapper;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@RestController
@RequestMapping("/ai/admin")
@CrossOrigin(originPatterns = "*", allowedHeaders = "*", allowCredentials = "true")
public class AiAdminController {

    @Resource
    private AiBusinessTermMapper aiBusinessTermMapper;

    @Resource
    private AiIntentKeywordMapper aiIntentKeywordMapper;

    @Resource
    private AiQueryAuditLogMapper aiQueryAuditLogMapper;

    @PostMapping("/terms/page")
    @WebLog(desc = "AI业务术语分页")
    public ResponseResult<Page<AiBusinessTerm>> termPage(@RequestBody AiPageReqDTO reqDTO) {
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
        return ResponseResult.getSuccess(aiBusinessTermMapper.selectPage(page(reqDTO), wrapper));
    }

    @PostMapping("/terms/save")
    @WebLog(desc = "AI业务术语保存")
    public ResponseResult<Boolean> saveTerm(@RequestBody AiBusinessTerm entity) {
        if (entity.getEnabled() == null) {
            entity.setEnabled(1);
        }
        if (entity.getId() == null) {
            aiBusinessTermMapper.insert(entity);
        } else {
            aiBusinessTermMapper.updateById(entity);
        }
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @GetMapping("/terms/delete")
    @WebLog(desc = "AI业务术语删除")
    public ResponseResult<Boolean> deleteTerm(@RequestParam Long id) {
        aiBusinessTermMapper.deleteById(id);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @PostMapping("/intent-keywords/page")
    @WebLog(desc = "AI意图关键词分页")
    public ResponseResult<Page<AiIntentKeyword>> intentKeywordPage(@RequestBody AiPageReqDTO reqDTO) {
        LambdaQueryWrapper<AiIntentKeyword> wrapper = new LambdaQueryWrapper<AiIntentKeyword>()
                .eq(reqDTO.getEnabled() != null, AiIntentKeyword::getEnabled, reqDTO.getEnabled())
                .eq(StrUtil.isNotBlank(reqDTO.getIntent()), AiIntentKeyword::getIntent, reqDTO.getIntent())
                .like(StrUtil.isNotBlank(reqDTO.getKeyword()), AiIntentKeyword::getKeyword, reqDTO.getKeyword())
                .orderByDesc(AiIntentKeyword::getWeight)
                .orderByDesc(AiIntentKeyword::getId);
        return ResponseResult.getSuccess(aiIntentKeywordMapper.selectPage(page(reqDTO), wrapper));
    }

    @PostMapping("/intent-keywords/save")
    @WebLog(desc = "AI意图关键词保存")
    public ResponseResult<Boolean> saveIntentKeyword(@RequestBody AiIntentKeyword entity) {
        if (entity.getEnabled() == null) {
            entity.setEnabled(1);
        }
        if (entity.getWeight() == null) {
            entity.setWeight(1);
        }
        if (entity.getId() == null) {
            aiIntentKeywordMapper.insert(entity);
        } else {
            aiIntentKeywordMapper.updateById(entity);
        }
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @GetMapping("/intent-keywords/delete")
    @WebLog(desc = "AI意图关键词删除")
    public ResponseResult<Boolean> deleteIntentKeyword(@RequestParam Long id) {
        aiIntentKeywordMapper.deleteById(id);
        return ResponseResult.getSuccess(Boolean.TRUE);
    }

    @PostMapping("/audit/page")
    @WebLog(desc = "AI查询审计分页")
    public ResponseResult<Page<AiQueryAuditLog>> auditPage(@RequestBody AiPageReqDTO reqDTO) {
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
        return ResponseResult.getSuccess(aiQueryAuditLogMapper.selectPage(page(reqDTO), wrapper));
    }

    private <T> Page<T> page(AiPageReqDTO reqDTO) {
        long pageNum = reqDTO.getPageNum() == null || reqDTO.getPageNum() <= 0 ? 1 : reqDTO.getPageNum();
        long pageSize = reqDTO.getPageSize() == null || reqDTO.getPageSize() <= 0 ? 20 : reqDTO.getPageSize();
        if (pageSize > 200) {
            pageSize = 200;
        }
        return new Page<>(pageNum, pageSize);
    }
}
