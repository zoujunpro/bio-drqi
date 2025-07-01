package com.bio.drqi.bsm.kd.util;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.bsm.kd.dto.base.KdApiBaseSaveRequestDTO;
import com.bio.drqi.bsm.kd.enums.FormIdEnum;
import com.bio.drqi.bsm.kd.properties.KdProperties;
import com.google.gson.Gson;
import com.kingdee.bos.webapi.entity.RepoRet;
import com.kingdee.bos.webapi.sdk.K3CloudApi;
import lombok.extern.slf4j.Slf4j;


@Slf4j
public class KdRequestUtil {

    private static KdProperties kdProperties;

    public KdRequestUtil(KdProperties kdProperties) {
        KdRequestUtil.kdProperties = kdProperties;
    }

    public static String save(FormIdEnum formIdEnum, KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO) {
        K3CloudApi k3CloudApi = new K3CloudApi(kdProperties.getIdentifyInfo(), false);
        try {
            Long start = System.currentTimeMillis();
            log.info("调用金蝶接口开始, fordId={},参数={}", formIdEnum, kdApiBaseSaveRequestDTO);
            String result = k3CloudApi.save(formIdEnum.name(), JSONUtil.toJsonStr(kdApiBaseSaveRequestDTO));
            log.info("调用金蝶接口结束，返回={},耗时={}ms", result, (System.currentTimeMillis() - start));
            Gson gson = new Gson();
            RepoRet sRet = gson.fromJson(result, RepoRet.class);
            if (sRet.isSuccessfully()) {
                return sRet.getResult().getId();
            } else {
                throw new BusinessException("同步数据到金蝶失败: " + gson.toJson(sRet.getResult()));
            }
        } catch (Exception e) {
            log.error("金蝶接口调用失败:{}", e);
            throw new BusinessException("金蝶接口调用失败");
        }
    }
}
