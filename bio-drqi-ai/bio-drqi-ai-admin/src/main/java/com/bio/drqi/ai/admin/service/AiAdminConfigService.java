package com.bio.drqi.ai.admin.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.dao.domain.AiBusinessDictionary;
import com.bio.drqi.ai.dao.domain.AiIntent;
import com.bio.drqi.ai.dao.domain.AiIntentExample;
import com.bio.drqi.ai.dao.domain.AiIntentToolRel;
import com.bio.drqi.ai.dao.domain.AiSemanticPattern;
import com.bio.drqi.ai.dao.domain.AiTaskTemplate;
import com.bio.drqi.ai.dao.domain.AiTaskTemplateStep;
import com.bio.drqi.ai.dao.domain.AiToolDefinition;
import com.bio.drqi.ai.dao.mapper.AiBusinessDictionaryMapper;
import com.bio.drqi.ai.dao.mapper.AiIntentExampleMapper;
import com.bio.drqi.ai.dao.mapper.AiIntentMapper;
import com.bio.drqi.ai.dao.mapper.AiIntentToolRelMapper;
import com.bio.drqi.ai.dao.mapper.AiSemanticPatternMapper;
import com.bio.drqi.ai.dao.mapper.AiTaskTemplateMapper;
import com.bio.drqi.ai.dao.mapper.AiTaskTemplateStepMapper;
import com.bio.drqi.ai.dao.mapper.AiToolDefinitionMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * AI 后台配置维护服务。
 */
@Service
public class AiAdminConfigService {

    private static final String STATUS_ACTIVE = "ACTIVE";

    private static final String STATUS_DISABLED = "DISABLED";

    private static final String STATUS_DELETED = "DELETED";

    @Resource
    private AiToolDefinitionMapper aiToolDefinitionMapper;

    @Resource
    private AiIntentMapper aiIntentMapper;

    @Resource
    private AiIntentExampleMapper aiIntentExampleMapper;

    @Resource
    private AiIntentToolRelMapper aiIntentToolRelMapper;

    @Resource
    private AiTaskTemplateMapper aiTaskTemplateMapper;

    @Resource
    private AiTaskTemplateStepMapper aiTaskTemplateStepMapper;

    @Resource
    private AiBusinessDictionaryMapper aiBusinessDictionaryMapper;

    @Resource
    private AiSemanticPatternMapper aiSemanticPatternMapper;

    public List<AiToolDefinition> listTools(String status, String toolCode, String toolType) {
        QueryWrapper<AiToolDefinition> wrapper = activeWrapper(status);
        likeIfHasText(wrapper, "tool_code", toolCode);
        eqIfHasText(wrapper, "tool_type", toolType);
        wrapper.orderByDesc("id");
        return aiToolDefinitionMapper.selectList(wrapper);
    }

    public AiToolDefinition detailTool(Long id) {
        return getRequired(aiToolDefinitionMapper, id, "工具不存在");
    }

    public void saveTool(AiToolDefinition entity) {
        validateTool(entity);
        saveOrUpdate(aiToolDefinitionMapper, entity, entity.getId());
    }

    public void enableTool(Long id) {
        updateStatus(aiToolDefinitionMapper, getRequired(aiToolDefinitionMapper, id, "工具不存在"), STATUS_ACTIVE);
    }

    public void disableTool(Long id) {
        updateStatus(aiToolDefinitionMapper, getRequired(aiToolDefinitionMapper, id, "工具不存在"), STATUS_DISABLED);
    }

    public void deleteTool(Long id) {
        updateStatus(aiToolDefinitionMapper, getRequired(aiToolDefinitionMapper, id, "工具不存在"), STATUS_DELETED);
    }

    public List<AiIntent> listIntents(String status, String intentCode, String domain) {
        QueryWrapper<AiIntent> wrapper = activeWrapper(status);
        likeIfHasText(wrapper, "intent_code", intentCode);
        eqIfHasText(wrapper, "domain", domain);
        wrapper.orderByDesc("id");
        return aiIntentMapper.selectList(wrapper);
    }

    public AiIntent detailIntent(Long id) {
        return getRequired(aiIntentMapper, id, "意图不存在");
    }

    public void saveIntent(AiIntent entity) {
        validateIntent(entity);
        saveOrUpdate(aiIntentMapper, entity, entity.getId());
    }

    public void enableIntent(Long id) {
        updateStatus(aiIntentMapper, getRequired(aiIntentMapper, id, "意图不存在"), STATUS_ACTIVE);
    }

    public void disableIntent(Long id) {
        updateStatus(aiIntentMapper, getRequired(aiIntentMapper, id, "意图不存在"), STATUS_DISABLED);
    }

    public void deleteIntent(Long id) {
        updateStatus(aiIntentMapper, getRequired(aiIntentMapper, id, "意图不存在"), STATUS_DELETED);
    }

    public List<AiIntentExample> listIntentExamples(String status, String intentCode) {
        QueryWrapper<AiIntentExample> wrapper = activeWrapper(status);
        eqIfHasText(wrapper, "intent_code", intentCode);
        wrapper.orderByDesc("id");
        return aiIntentExampleMapper.selectList(wrapper);
    }

    public AiIntentExample detailIntentExample(Long id) {
        return getRequired(aiIntentExampleMapper, id, "意图样例不存在");
    }

    public void saveIntentExample(AiIntentExample entity) {
        if (entity == null || !hasText(entity.getIntentCode()) || !hasText(entity.getExampleText())) {
            throw new BusinessException("意图编码和样例文本不能为空");
        }
        saveOrUpdate(aiIntentExampleMapper, entity, entity.getId());
    }

    public void enableIntentExample(Long id) {
        updateStatus(aiIntentExampleMapper, getRequired(aiIntentExampleMapper, id, "意图样例不存在"), STATUS_ACTIVE);
    }

    public void disableIntentExample(Long id) {
        updateStatus(aiIntentExampleMapper, getRequired(aiIntentExampleMapper, id, "意图样例不存在"), STATUS_DISABLED);
    }

    public void deleteIntentExample(Long id) {
        updateStatus(aiIntentExampleMapper, getRequired(aiIntentExampleMapper, id, "意图样例不存在"), STATUS_DELETED);
    }

    public List<AiIntentToolRel> listIntentToolRels(String status, String intentCode, String toolCode) {
        QueryWrapper<AiIntentToolRel> wrapper = activeWrapper(status);
        eqIfHasText(wrapper, "intent_code", intentCode);
        eqIfHasText(wrapper, "tool_code", toolCode);
        wrapper.orderByAsc("priority").orderByDesc("id");
        return aiIntentToolRelMapper.selectList(wrapper);
    }

    public AiIntentToolRel detailIntentToolRel(Long id) {
        return getRequired(aiIntentToolRelMapper, id, "意图工具关系不存在");
    }

    public void saveIntentToolRel(AiIntentToolRel entity) {
        if (entity == null || !hasText(entity.getIntentCode()) || !hasText(entity.getToolCode())) {
            throw new BusinessException("意图编码和工具编码不能为空");
        }
        if (entity.getPriority() == null) {
            entity.setPriority(100);
        }
        saveOrUpdate(aiIntentToolRelMapper, entity, entity.getId());
    }

    public void enableIntentToolRel(Long id) {
        updateStatus(aiIntentToolRelMapper, getRequired(aiIntentToolRelMapper, id, "意图工具关系不存在"), STATUS_ACTIVE);
    }

    public void disableIntentToolRel(Long id) {
        updateStatus(aiIntentToolRelMapper, getRequired(aiIntentToolRelMapper, id, "意图工具关系不存在"), STATUS_DISABLED);
    }

    public void deleteIntentToolRel(Long id) {
        updateStatus(aiIntentToolRelMapper, getRequired(aiIntentToolRelMapper, id, "意图工具关系不存在"), STATUS_DELETED);
    }

    public List<AiTaskTemplate> listTaskTemplates(String status, String templateCode, String intentCode) {
        QueryWrapper<AiTaskTemplate> wrapper = activeWrapper(status);
        likeIfHasText(wrapper, "template_code", templateCode);
        eqIfHasText(wrapper, "intent_code", intentCode);
        wrapper.orderByDesc("id");
        return aiTaskTemplateMapper.selectList(wrapper);
    }

    public AiTaskTemplate detailTaskTemplate(Long id) {
        return getRequired(aiTaskTemplateMapper, id, "任务模板不存在");
    }

    public void saveTaskTemplate(AiTaskTemplate entity) {
        if (entity == null || !hasText(entity.getTemplateCode()) || !hasText(entity.getIntentCode())) {
            throw new BusinessException("模板编码和意图编码不能为空");
        }
        saveOrUpdate(aiTaskTemplateMapper, entity, entity.getId());
    }

    public void enableTaskTemplate(Long id) {
        updateStatus(aiTaskTemplateMapper, getRequired(aiTaskTemplateMapper, id, "任务模板不存在"), STATUS_ACTIVE);
    }

    public void disableTaskTemplate(Long id) {
        updateStatus(aiTaskTemplateMapper, getRequired(aiTaskTemplateMapper, id, "任务模板不存在"), STATUS_DISABLED);
    }

    public void deleteTaskTemplate(Long id) {
        updateStatus(aiTaskTemplateMapper, getRequired(aiTaskTemplateMapper, id, "任务模板不存在"), STATUS_DELETED);
    }

    public List<AiTaskTemplateStep> listTaskTemplateSteps(String status, String templateCode) {
        QueryWrapper<AiTaskTemplateStep> wrapper = activeWrapper(status);
        eqIfHasText(wrapper, "template_code", templateCode);
        wrapper.orderByAsc("step_no").orderByDesc("id");
        return aiTaskTemplateStepMapper.selectList(wrapper);
    }

    public AiTaskTemplateStep detailTaskTemplateStep(Long id) {
        return getRequired(aiTaskTemplateStepMapper, id, "任务模板步骤不存在");
    }

    public void saveTaskTemplateStep(AiTaskTemplateStep entity) {
        if (entity == null || !hasText(entity.getTemplateCode()) || entity.getStepNo() == null
                || !hasText(entity.getTaskCode()) || !hasText(entity.getTaskType())) {
            throw new BusinessException("模板编码、步骤序号、任务编码和任务类型不能为空");
        }
        saveOrUpdate(aiTaskTemplateStepMapper, entity, entity.getId());
    }

    public void enableTaskTemplateStep(Long id) {
        updateStatus(aiTaskTemplateStepMapper, getRequired(aiTaskTemplateStepMapper, id, "任务模板步骤不存在"), STATUS_ACTIVE);
    }

    public void disableTaskTemplateStep(Long id) {
        updateStatus(aiTaskTemplateStepMapper, getRequired(aiTaskTemplateStepMapper, id, "任务模板步骤不存在"), STATUS_DISABLED);
    }

    public void deleteTaskTemplateStep(Long id) {
        updateStatus(aiTaskTemplateStepMapper, getRequired(aiTaskTemplateStepMapper, id, "任务模板步骤不存在"), STATUS_DELETED);
    }

    public List<AiBusinessDictionary> listBusinessDictionaries(String status, String dictType, String domain) {
        QueryWrapper<AiBusinessDictionary> wrapper = activeWrapper(status);
        eqIfHasText(wrapper, "dict_type", dictType);
        eqIfHasText(wrapper, "domain", domain);
        wrapper.orderByDesc("id");
        return aiBusinessDictionaryMapper.selectList(wrapper);
    }

    public AiBusinessDictionary detailBusinessDictionary(Long id) {
        return getRequired(aiBusinessDictionaryMapper, id, "业务词典不存在");
    }

    public void saveBusinessDictionary(AiBusinessDictionary entity) {
        if (entity == null || !hasText(entity.getDictType()) || !hasText(entity.getDictCode())
                || !hasText(entity.getDictName())) {
            throw new BusinessException("词典类型、业务编码和标准名称不能为空");
        }
        saveOrUpdate(aiBusinessDictionaryMapper, entity, entity.getId());
    }

    public void enableBusinessDictionary(Long id) {
        updateStatus(aiBusinessDictionaryMapper, getRequired(aiBusinessDictionaryMapper, id, "业务词典不存在"), STATUS_ACTIVE);
    }

    public void disableBusinessDictionary(Long id) {
        updateStatus(aiBusinessDictionaryMapper, getRequired(aiBusinessDictionaryMapper, id, "业务词典不存在"), STATUS_DISABLED);
    }

    public void deleteBusinessDictionary(Long id) {
        updateStatus(aiBusinessDictionaryMapper, getRequired(aiBusinessDictionaryMapper, id, "业务词典不存在"), STATUS_DELETED);
    }

    public List<AiSemanticPattern> listSemanticPatterns(String status, String patternType, String domain) {
        QueryWrapper<AiSemanticPattern> wrapper = activeWrapper(status);
        eqIfHasText(wrapper, "pattern_type", patternType);
        eqIfHasText(wrapper, "domain", domain);
        wrapper.orderByDesc("id");
        return aiSemanticPatternMapper.selectList(wrapper);
    }

    public AiSemanticPattern detailSemanticPattern(Long id) {
        return getRequired(aiSemanticPatternMapper, id, "语义规则不存在");
    }

    public void saveSemanticPattern(AiSemanticPattern entity) {
        if (entity == null || !hasText(entity.getPatternType()) || !hasText(entity.getPatternCode())
                || !hasText(entity.getPatternText())) {
            throw new BusinessException("模式类型、模式编码和模式内容不能为空");
        }
        saveOrUpdate(aiSemanticPatternMapper, entity, entity.getId());
    }

    public void enableSemanticPattern(Long id) {
        updateStatus(aiSemanticPatternMapper, getRequired(aiSemanticPatternMapper, id, "语义规则不存在"), STATUS_ACTIVE);
    }

    public void disableSemanticPattern(Long id) {
        updateStatus(aiSemanticPatternMapper, getRequired(aiSemanticPatternMapper, id, "语义规则不存在"), STATUS_DISABLED);
    }

    public void deleteSemanticPattern(Long id) {
        updateStatus(aiSemanticPatternMapper, getRequired(aiSemanticPatternMapper, id, "语义规则不存在"), STATUS_DELETED);
    }

    private void validateTool(AiToolDefinition entity) {
        if (entity == null || !hasText(entity.getToolCode()) || !hasText(entity.getToolType())) {
            throw new BusinessException("工具编码和工具类型不能为空");
        }
        if ("API".equals(entity.getToolType())
                && (!hasText(entity.getServiceUrl()) || !hasText(entity.getHttpMethod()))) {
            throw new BusinessException("API 类型工具必须配置服务地址和 HTTP 方法");
        }
        if ("API".equals(entity.getToolType())) {
            entity.setTargetCode(entity.getToolCode());
        }
        if ("WORKFLOW".equals(entity.getToolType()) && !hasText(entity.getTargetCode())) {
            throw new BusinessException("WORKFLOW 类型工具必须配置目标工作流编码");
        }
    }

    private void validateIntent(AiIntent entity) {
        if (entity == null || !hasText(entity.getIntentCode()) || !hasText(entity.getIntentName())) {
            throw new BusinessException("意图编码和意图名称不能为空");
        }
    }

    private <T> T getRequired(BaseMapper<T> mapper, Long id, String message) {
        if (id == null) {
            throw new BusinessException(message);
        }
        T entity = mapper.selectById(id);
        if (entity == null) {
            throw new BusinessException(message);
        }
        return entity;
    }

    private <T> void saveOrUpdate(BaseMapper<T> mapper, T entity, Long id) {
        if (id == null) {
            setDefaultStatus(entity);
            setCreateTime(entity, new Date());
            setUpdateTime(entity, new Date());
            mapper.insert(entity);
            return;
        }
        setUpdateTime(entity, new Date());
        mapper.updateById(entity);
    }

    private <T> void updateStatus(BaseMapper<T> mapper, T entity, String status) {
        setStatus(entity, status);
        setUpdateTime(entity, new Date());
        mapper.updateById(entity);
    }

    private <T> QueryWrapper<T> activeWrapper(String status) {
        QueryWrapper<T> wrapper = new QueryWrapper<T>();
        if (hasText(status)) {
            wrapper.eq("status", status);
        } else {
            wrapper.ne("status", STATUS_DELETED);
        }
        return wrapper;
    }

    private <T> void eqIfHasText(QueryWrapper<T> wrapper, String field, String value) {
        if (hasText(value)) {
            wrapper.eq(field, value);
        }
    }

    private <T> void likeIfHasText(QueryWrapper<T> wrapper, String field, String value) {
        if (hasText(value)) {
            wrapper.like(field, value);
        }
    }

    private void setDefaultStatus(Object entity) {
        if (getStatus(entity) == null) {
            setStatus(entity, STATUS_ACTIVE);
        }
    }

    private String getStatus(Object entity) {
        try {
            Object value = entity.getClass().getMethod("getStatus").invoke(entity);
            return value == null ? null : String.valueOf(value);
        } catch (Exception e) {
            return null;
        }
    }

    private void setStatus(Object entity, String status) {
        invokeSetter(entity, "setStatus", String.class, status);
    }

    private void setCreateTime(Object entity, Date date) {
        invokeSetter(entity, "setCreateTime", Date.class, date);
    }

    private void setUpdateTime(Object entity, Date date) {
        invokeSetter(entity, "setUpdateTime", Date.class, date);
    }

    private void invokeSetter(Object entity, String methodName, Class<?> paramType, Object value) {
        try {
            entity.getClass().getMethod(methodName, paramType).invoke(entity, value);
        } catch (Exception ignored) {
            // 配置实体字段一致；这里保留容错，避免个别实体缺少时间字段时影响保存。
        }
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
