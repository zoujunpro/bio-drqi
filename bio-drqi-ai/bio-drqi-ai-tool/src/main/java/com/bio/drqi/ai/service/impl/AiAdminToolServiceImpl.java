package com.bio.drqi.ai.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.ai.dto.admin.AiApiSyncResultDTO;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.entity.AiApiParam;
import com.bio.drqi.ai.entity.AiApiRegistry;
import com.bio.drqi.ai.mapper.AiApiParamMapper;
import com.bio.drqi.ai.mapper.AiApiRegistryMapper;
import com.bio.drqi.ai.service.AiAdminToolService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AiAdminToolServiceImpl implements AiAdminToolService {

    @Resource
    private AiApiRegistryMapper aiApiRegistryMapper;

    @Resource
    private AiApiParamMapper aiApiParamMapper;

    @Override
    public Page<AiApiRegistry> apiPage(AiPageReqDTO reqDTO) {
        LambdaQueryWrapper<AiApiRegistry> wrapper = new LambdaQueryWrapper<AiApiRegistry>()
                .eq(AiApiRegistry::getDeleted, 0)
                .eq(reqDTO.getAiEnabled() != null, AiApiRegistry::getAiEnabled, reqDTO.getAiEnabled())
                .eq(reqDTO.getReadOnly() != null, AiApiRegistry::getReadOnly, reqDTO.getReadOnly())
                .eq(StrUtil.isNotBlank(reqDTO.getServiceName()), AiApiRegistry::getServiceName, reqDTO.getServiceName())
                .eq(StrUtil.isNotBlank(reqDTO.getRiskLevel()), AiApiRegistry::getRiskLevel, reqDTO.getRiskLevel())
                .and(StrUtil.isNotBlank(reqDTO.getKeyword()), item -> item
                        .like(AiApiRegistry::getApiCode, reqDTO.getKeyword())
                        .or()
                        .like(AiApiRegistry::getApiName, reqDTO.getKeyword())
                        .or()
                        .like(AiApiRegistry::getPath, reqDTO.getKeyword())
                        .or()
                        .like(AiApiRegistry::getDescription, reqDTO.getKeyword()))
                .orderByDesc(AiApiRegistry::getUpdateTime)
                .orderByDesc(AiApiRegistry::getId);
        return aiApiRegistryMapper.selectPage(page(reqDTO), wrapper);
    }

    @Override
    public void saveApi(AiApiRegistry entity) {
        validateApi(entity);
        Date now = new Date();
        entity.setApiCode(entity.getApiCode().trim());
        entity.setServiceName(entity.getServiceName().trim());
        entity.setPath(entity.getPath().trim());
        entity.setMethod(entity.getMethod().trim().toUpperCase());
        entity.setApiName(StrUtil.trimToNull(entity.getApiName()));
        entity.setDescription(StrUtil.trimToNull(entity.getDescription()));
        entity.setRiskLevel(StrUtil.blankToDefault(entity.getRiskLevel(), "low"));
        if (entity.getAiEnabled() == null) {
            entity.setAiEnabled(0);
        }
        if (entity.getReadOnly() == null) {
            entity.setReadOnly(1);
        }
        if (entity.getDeleted() == null) {
            entity.setDeleted(0);
        }
        entity.setUpdateTime(now);
        if (entity.getId() == null) {
            entity.setCreateTime(now);
            aiApiRegistryMapper.insert(entity);
        } else {
            aiApiRegistryMapper.updateById(entity);
        }
    }

    @Override
    public void deleteApi(Long id) {
        AiApiRegistry entity = aiApiRegistryMapper.selectById(id);
        if (entity == null) {
            return;
        }
        Date now = new Date();
        entity.setDeleted(1);
        entity.setAiEnabled(0);
        entity.setUpdateTime(now);
        aiApiRegistryMapper.updateById(entity);
        if (StrUtil.isNotBlank(entity.getApiCode())) {
            List<AiApiParam> paramList = aiApiParamMapper.selectList(new LambdaQueryWrapper<AiApiParam>()
                    .eq(AiApiParam::getApiCode, entity.getApiCode())
                    .eq(AiApiParam::getDeleted, 0));
            for (AiApiParam param : paramList) {
                param.setDeleted(1);
                param.setAiEnabled(0);
                param.setUpdateTime(now);
                aiApiParamMapper.updateById(param);
            }
        }
    }

    @Override
    public void batchUpdateApiAiEnabled(List<Long> ids, Integer aiEnabled) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择需要处理的接口");
        }
        if (aiEnabled == null || (aiEnabled != 0 && aiEnabled != 1)) {
            throw new BusinessException("AI调用状态只能是开通或禁用");
        }
        List<AiApiRegistry> apiList = aiApiRegistryMapper.selectList(new LambdaQueryWrapper<AiApiRegistry>()
                .in(AiApiRegistry::getId, ids)
                .eq(AiApiRegistry::getDeleted, 0));
        if (apiList == null || apiList.isEmpty()) {
            throw new BusinessException("未找到可处理的接口");
        }
        if (aiEnabled == 1) {
            for (AiApiRegistry api : apiList) {
                if (api.getReadOnly() == null || api.getReadOnly() != 1) {
                    throw new BusinessException("批量开通失败：只能开通只读查询接口，非只读接口请先人工确认");
                }
            }
        }

        Date now = new Date();
        for (AiApiRegistry api : apiList) {
            api.setAiEnabled(aiEnabled);
            api.setUpdateTime(now);
            aiApiRegistryMapper.updateById(api);
            if (StrUtil.isNotBlank(api.getApiCode())) {
                List<AiApiParam> paramList = aiApiParamMapper.selectList(new LambdaQueryWrapper<AiApiParam>()
                        .eq(AiApiParam::getApiCode, api.getApiCode())
                        .eq(AiApiParam::getDeleted, 0));
                for (AiApiParam param : paramList) {
                    param.setAiEnabled(aiEnabled);
                    param.setUpdateTime(now);
                    aiApiParamMapper.updateById(param);
                }
            }
        }
    }

    @Override
    public Page<AiApiParam> paramPage(AiPageReqDTO reqDTO) {
        LambdaQueryWrapper<AiApiParam> wrapper = new LambdaQueryWrapper<AiApiParam>()
                .eq(AiApiParam::getDeleted, 0)
                .eq(reqDTO.getAiEnabled() != null, AiApiParam::getAiEnabled, reqDTO.getAiEnabled())
                .eq(reqDTO.getRequired() != null, AiApiParam::getRequired, reqDTO.getRequired())
                .eq(StrUtil.isNotBlank(reqDTO.getApiCode()), AiApiParam::getApiCode, reqDTO.getApiCode())
                .and(StrUtil.isNotBlank(reqDTO.getKeyword()), item -> item
                        .like(AiApiParam::getParamName, reqDTO.getKeyword())
                        .or()
                        .like(AiApiParam::getBusinessName, reqDTO.getKeyword())
                        .or()
                        .like(AiApiParam::getAliases, reqDTO.getKeyword())
                        .or()
                        .like(AiApiParam::getSourceField, reqDTO.getKeyword()))
                .orderByAsc(AiApiParam::getApiCode)
                .orderByAsc(AiApiParam::getId);
        return aiApiParamMapper.selectPage(page(reqDTO), wrapper);
    }

    @Override
    public void saveParam(AiApiParam entity) {
        validateParam(entity);
        Date now = new Date();
        entity.setApiCode(entity.getApiCode().trim());
        entity.setParamName(entity.getParamName().trim());
        entity.setParamType(StrUtil.trimToNull(entity.getParamType()));
        entity.setJavaField(StrUtil.blankToDefault(StrUtil.trimToNull(entity.getJavaField()), entity.getParamName()));
        entity.setBusinessName(StrUtil.trimToNull(entity.getBusinessName()));
        entity.setAliases(StrUtil.trimToNull(entity.getAliases()));
        entity.setSourceType(StrUtil.trimToNull(entity.getSourceType()));
        entity.setSourceField(StrUtil.trimToNull(entity.getSourceField()));
        entity.setDefaultValue(StrUtil.trimToNull(entity.getDefaultValue()));
        if (entity.getRequired() == null) {
            entity.setRequired(0);
        }
        if (entity.getAiEnabled() == null) {
            entity.setAiEnabled(0);
        }
        if (entity.getDeleted() == null) {
            entity.setDeleted(0);
        }
        entity.setUpdateTime(now);
        if (entity.getId() == null) {
            entity.setCreateTime(now);
            aiApiParamMapper.insert(entity);
        } else {
            aiApiParamMapper.updateById(entity);
        }
    }

    @Override
    public void deleteParam(Long id) {
        AiApiParam entity = aiApiParamMapper.selectById(id);
        if (entity == null) {
            return;
        }
        entity.setDeleted(1);
        entity.setAiEnabled(0);
        entity.setUpdateTime(new Date());
        aiApiParamMapper.updateById(entity);
    }

    @Override
    public void batchUpdateParamRequired(List<Long> ids, Integer required) {
        if (ids == null || ids.isEmpty()) {
            throw new BusinessException("请选择需要处理的参数");
        }
        if (required == null || (required != 0 && required != 1)) {
            throw new BusinessException("必填状态只能是必填或非必填");
        }
        List<AiApiParam> paramList = aiApiParamMapper.selectList(new LambdaQueryWrapper<AiApiParam>()
                .in(AiApiParam::getId, ids)
                .eq(AiApiParam::getDeleted, 0));
        if (paramList == null || paramList.isEmpty()) {
            throw new BusinessException("未找到可处理的参数");
        }
        Date now = new Date();
        for (AiApiParam param : paramList) {
            param.setRequired(required);
            param.setUpdateTime(now);
            aiApiParamMapper.updateById(param);
        }
    }

    @Override
    public AiApiSyncResultDTO syncApis(List<AiApiRegistry> apiList, List<AiApiParam> paramList) {
        AiApiSyncResultDTO result = new AiApiSyncResultDTO();
        Date now = new Date();
        Set<String> scannedApiCodes = new HashSet<String>();
        Set<String> scannedServiceNames = new HashSet<String>();
        if (apiList != null) {
            result.setScannedApiCount(apiList.size());
            for (AiApiRegistry scanned : apiList) {
                if (scanned == null || StrUtil.isBlank(scanned.getApiCode())) {
                    continue;
                }
                scannedApiCodes.add(scanned.getApiCode());
                if (StrUtil.isNotBlank(scanned.getServiceName())) {
                    scannedServiceNames.add(scanned.getServiceName());
                }
                AiApiRegistry existed = aiApiRegistryMapper.selectOne(new LambdaQueryWrapper<AiApiRegistry>()
                        .eq(AiApiRegistry::getApiCode, scanned.getApiCode())
                        .last("limit 1"));
                if (existed == null) {
                    scanned.setAiEnabled(scanned.getAiEnabled() == null ? 0 : scanned.getAiEnabled());
                    scanned.setDeleted(0);
                    scanned.setCreateTime(now);
                    scanned.setUpdateTime(now);
                    scanned.setSyncTime(now);
                    aiApiRegistryMapper.insert(scanned);
                    result.setInsertedApiCount(result.getInsertedApiCount() + 1);
                } else {
                    existed.setServiceName(scanned.getServiceName());
                    existed.setPath(scanned.getPath());
                    existed.setMethod(scanned.getMethod());
                    existed.setControllerClass(scanned.getControllerClass());
                    existed.setMethodName(scanned.getMethodName());
                    existed.setRequestDto(scanned.getRequestDto());
                    existed.setResponseType(scanned.getResponseType());
                    existed.setApiName(StrUtil.blankToDefault(existed.getApiName(), scanned.getApiName()));
                    existed.setDescription(StrUtil.blankToDefault(existed.getDescription(), scanned.getDescription()));
                    existed.setReadOnly(existed.getReadOnly() == null ? scanned.getReadOnly() : existed.getReadOnly());
                    existed.setRiskLevel(StrUtil.blankToDefault(existed.getRiskLevel(), scanned.getRiskLevel()));
                    existed.setOwnerModule(StrUtil.blankToDefault(existed.getOwnerModule(), scanned.getOwnerModule()));
                    existed.setDeleted(0);
                    existed.setUpdateTime(now);
                    existed.setSyncTime(now);
                    aiApiRegistryMapper.updateById(existed);
                    result.setUpdatedApiCount(result.getUpdatedApiCount() + 1);
                }
            }
            disableStaleApis(scannedServiceNames, scannedApiCodes, now);
        }
        if (paramList != null) {
            for (AiApiParam scanned : paramList) {
                if (scanned == null || StrUtil.isBlank(scanned.getApiCode()) || StrUtil.isBlank(scanned.getParamName())) {
                    continue;
                }
                AiApiParam existed = aiApiParamMapper.selectOne(new LambdaQueryWrapper<AiApiParam>()
                        .eq(AiApiParam::getApiCode, scanned.getApiCode())
                        .eq(AiApiParam::getParamName, scanned.getParamName())
                        .last("limit 1"));
                if (existed == null) {
                    scanned.setAiEnabled(scanned.getAiEnabled() == null ? 0 : scanned.getAiEnabled());
                    scanned.setDeleted(0);
                    scanned.setCreateTime(now);
                    scanned.setUpdateTime(now);
                    scanned.setSyncTime(now);
                    aiApiParamMapper.insert(scanned);
                    result.setInsertedParamCount(result.getInsertedParamCount() + 1);
                } else {
                    boolean manuallyMaintained = manuallyMaintained(existed);
                    existed.setParamType(scanned.getParamType());
                    if (!manuallyMaintained || existed.getRequired() == null) {
                        existed.setRequired(scanned.getRequired());
                    }
                    existed.setJavaField(scanned.getJavaField());
                    existed.setBusinessName(StrUtil.blankToDefault(existed.getBusinessName(), scanned.getBusinessName()));
                    existed.setAliases(StrUtil.blankToDefault(existed.getAliases(), scanned.getAliases()));
                    existed.setSourceType(scanned.getSourceType());
                    existed.setSourceField(scanned.getSourceField());
                    existed.setDefaultValue(StrUtil.blankToDefault(existed.getDefaultValue(), scanned.getDefaultValue()));
                    existed.setDeleted(0);
                    existed.setUpdateTime(now);
                    existed.setSyncTime(now);
                    aiApiParamMapper.updateById(existed);
                    result.setUpdatedParamCount(result.getUpdatedParamCount() + 1);
                }
            }
        }
        return result;
    }

    private boolean manuallyMaintained(AiApiParam param) {
        return param != null
                && param.getUpdateTime() != null
                && param.getSyncTime() != null
                && param.getUpdateTime().after(param.getSyncTime());
    }

    private void disableStaleApis(Set<String> scannedServiceNames, Set<String> scannedApiCodes, Date now) {
        if (scannedServiceNames == null || scannedServiceNames.isEmpty()
                || scannedApiCodes == null || scannedApiCodes.isEmpty()) {
            return;
        }
        List<AiApiRegistry> staleApiList = aiApiRegistryMapper.selectList(new LambdaQueryWrapper<AiApiRegistry>()
                .in(AiApiRegistry::getServiceName, scannedServiceNames)
                .notIn(AiApiRegistry::getApiCode, scannedApiCodes)
                .eq(AiApiRegistry::getDeleted, 0));
        if (staleApiList == null || staleApiList.isEmpty()) {
            return;
        }
        for (AiApiRegistry api : staleApiList) {
            api.setDeleted(1);
            api.setAiEnabled(0);
            api.setUpdateTime(now);
            aiApiRegistryMapper.updateById(api);
            if (StrUtil.isBlank(api.getApiCode())) {
                continue;
            }
            List<AiApiParam> paramList = aiApiParamMapper.selectList(new LambdaQueryWrapper<AiApiParam>()
                    .eq(AiApiParam::getApiCode, api.getApiCode())
                    .eq(AiApiParam::getDeleted, 0));
            if (paramList == null || paramList.isEmpty()) {
                continue;
            }
            for (AiApiParam param : paramList) {
                param.setDeleted(1);
                param.setAiEnabled(0);
                param.setUpdateTime(now);
                aiApiParamMapper.updateById(param);
            }
        }
    }

    private void validateApi(AiApiRegistry entity) {
        if (entity == null) {
            throw new BusinessException("API配置不能为空");
        }
        if (StrUtil.isBlank(entity.getApiCode())) {
            throw new BusinessException("API编码不能为空");
        }
        if (StrUtil.isBlank(entity.getServiceName())) {
            throw new BusinessException("服务名不能为空");
        }
        if (StrUtil.isBlank(entity.getPath())) {
            throw new BusinessException("接口路径不能为空");
        }
        if (StrUtil.isBlank(entity.getMethod())) {
            throw new BusinessException("请求方法不能为空");
        }
    }

    private void validateParam(AiApiParam entity) {
        if (entity == null) {
            throw new BusinessException("API参数配置不能为空");
        }
        if (StrUtil.isBlank(entity.getApiCode())) {
            throw new BusinessException("API编码不能为空");
        }
        if (StrUtil.isBlank(entity.getParamName())) {
            throw new BusinessException("参数名不能为空");
        }
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
