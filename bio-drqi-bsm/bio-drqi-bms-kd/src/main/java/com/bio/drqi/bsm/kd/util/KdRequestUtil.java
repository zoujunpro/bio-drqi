package com.bio.drqi.bsm.kd.util;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.bsm.kd.dto.*;
import com.bio.drqi.bsm.kd.enums.FormIdEnum;
import com.bio.drqi.bsm.kd.properties.KdProperties;
import com.google.gson.Gson;
import com.kingdee.bos.webapi.entity.RepoResult;
import com.kingdee.bos.webapi.entity.RepoRet;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Slf4j
public class KdRequestUtil {

    private static KdProperties kdProperties;
    private static final ThreadLocal<String> LAST_REQUEST_PARAM = new ThreadLocal<>();

    public KdRequestUtil(KdProperties kdProperties) {
        KdRequestUtil.kdProperties = kdProperties;
    }

    public static String getLastRequestParam() {
        return LAST_REQUEST_PARAM.get();
    }

    public static void clearLastRequestParam() {
        LAST_REQUEST_PARAM.remove();
    }

    public static String save(FormIdEnum formIdEnum, KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO) {
        K3CloudApi k3CloudApi = new K3CloudApi(kdProperties.getIdentifyInfo(), false);
        Long start = System.currentTimeMillis();
        String requestParam = JSONUtil.toJsonStr(kdApiBaseSaveRequestDTO);
        LAST_REQUEST_PARAM.set(requestParam);
        log.info("调用金蝶保存接口save开始, formid={},参数={}", formIdEnum, requestParam);
        String result = null;
        try {
            result = k3CloudApi.save(formIdEnum.name(), requestParam);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("调用金蝶保存接口save结束，返回={},耗时={}ms", result, (System.currentTimeMillis() - start));
        Gson gson = new Gson();
        RepoRet<RepoResult> sRet = gson.fromJson(result, RepoRet.class);
        if (sRet.isSuccessfully()) {
            return sRet.getResult().getId();
        } else {
            throw new BusinessException("调用金蝶保存接口save失败: " + gson.toJson(sRet.getResult()));
        }

    }

    public static List<List<Object>> query(ExecuteBillQueryModelDTO executeBillQueryModelDTO) {
        K3CloudApi k3CloudApi = new K3CloudApi(kdProperties.getIdentifyInfo(), false);
        try {
            Long start = System.currentTimeMillis();
            String requestParam = JSONUtil.toJsonStr(executeBillQueryModelDTO);
            LAST_REQUEST_PARAM.set(requestParam);
            log.info("调用金蝶查询接口query开始, formid={},参数={}", executeBillQueryModelDTO.getFormId(), requestParam);
            List<List<Object>> result = k3CloudApi.executeBillQuery(requestParam);
            log.info("调用金蝶查询接口query结束，返回={},耗时={}ms", result, (System.currentTimeMillis() - start));
            if (JSONUtil.toJsonStr(result).contains("ErrorCode")) {
                throw new BusinessException("金蝶查询接口query调用失败");
            }
            return result;
        } catch (Exception e) {
            log.error("金蝶查询接口query调用失败:{}", e);
            throw new BusinessException("金蝶查询接口query调用失败");
        }
    }

    public static String disable(FormIdEnum formIdEnum, KdApiBaseDisableRequestDTO kdApiBaseDisableRequestDTO) {
        K3CloudApi k3CloudApi = new K3CloudApi(kdProperties.getIdentifyInfo(), false);
        try {
            Long start = System.currentTimeMillis();
            String requestParam = JSONUtil.toJsonStr(kdApiBaseDisableRequestDTO);
            LAST_REQUEST_PARAM.set(requestParam);
            log.info("调用金蝶禁用接口disable开始, formid={},参数={}", formIdEnum, requestParam);
            String result = k3CloudApi.excuteOperation(formIdEnum.name(), "Forbid", requestParam);
            log.info("调用金蝶禁用接口disable结束，返回={},耗时={}ms", result, (System.currentTimeMillis() - start));
            Gson gson = new Gson();
            RepoRet<RepoResult> sRet = gson.fromJson(result, RepoRet.class);
            if (sRet.isSuccessfully()) {
                return sRet.getResult().getId();
            } else {
                throw new BusinessException("金蝶禁用接口disable调用失败: " + gson.toJson(sRet.getResult()));
            }
        } catch (Exception e) {
            log.error("金蝶禁用接口调用disable失败:{}", e);
            throw new BusinessException("金蝶禁用接口disable调用失败");
        }
    }

    public static String groupSave(FormIdEnum formIdEnum, GroupSaveDTO groupSaveDTO) {
        K3CloudApi k3CloudApi = new K3CloudApi(kdProperties.getIdentifyInfo(), false);
        Long start = System.currentTimeMillis();
        String requestParam = JSONUtil.toJsonStr(groupSaveDTO);
        LAST_REQUEST_PARAM.set(requestParam);
        log.info("调用金蝶分组接口groupSave开始, formid={},参数={}", formIdEnum, requestParam);
        String result = null;
        try {
            result = k3CloudApi.groupSave(formIdEnum.name(), requestParam);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        log.info("调用金蝶分组接口groupSave结束，返回={},耗时={}ms", result, (System.currentTimeMillis() - start));
        Gson gson = new Gson();
        RepoRet sRet = gson.fromJson(result, RepoRet.class);
        if (sRet.isSuccessfully()) {
            return sRet.getResult().getResponseStatus().getSuccessEntitys().get(0).getId();
        } else {
            throw new BusinessException("金蝶分组接口groupSave调用失败: " + gson.toJson(sRet.getResult()));
        }


    }

    public static List<QuerySupplierDTO> executeQuerySupplier() {
        List<QuerySupplierDTO> resultList = new ArrayList<>();
        K3CloudApi k3CloudApi = new K3CloudApi(kdProperties.getIdentifyInfo(), false);
        Map<String, String> query = new HashMap();
        query.put("FormId", FormIdEnum.BD_Supplier.name());
        query.put("FieldKeys", "FNUMBER,FNAME,FDocumentStatus,FForbidStatus");
        query.put("FilterString", "FDocumentStatus='C' and FForbidStatus='A'");
        try {
            String requestParam = JSONUtil.toJsonStr(query);
            LAST_REQUEST_PARAM.set(requestParam);
            List<List<Object>> result = k3CloudApi.executeBillQuery(requestParam);
            log.info("金蝶供应商拉去结果={}", JSONUtil.toJsonStr(result));
            if (CollectionUtil.isNotEmpty(result)) {
                result.forEach(list -> {
                    QuerySupplierDTO querySupplierDTO = new QuerySupplierDTO();
                    querySupplierDTO.setFNumber(list.get(0).toString());
                    querySupplierDTO.setFName(list.get(1).toString());
                    resultList.add(querySupplierDTO);
                });
            }
            return resultList;
        } catch (Exception e) {
            log.error("金蝶拉去供应商调用失败:{}", e);
            throw new BusinessException("金蝶拉去供应商调用失败");
        }

    }

}
