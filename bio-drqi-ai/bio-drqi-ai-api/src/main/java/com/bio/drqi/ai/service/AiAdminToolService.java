package com.bio.drqi.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.drqi.ai.dto.admin.AiApiSyncResultDTO;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.entity.AiApiParam;
import com.bio.drqi.ai.entity.AiApiRegistry;

import java.util.List;

public interface AiAdminToolService {

    Page<AiApiRegistry> apiPage(AiPageReqDTO reqDTO);

    void saveApi(AiApiRegistry entity);

    void deleteApi(Long id);

    void batchUpdateApiAiEnabled(List<Long> ids, Integer aiEnabled);

    Page<AiApiParam> paramPage(AiPageReqDTO reqDTO);

    void saveParam(AiApiParam entity);

    void deleteParam(Long id);

    void batchUpdateParamRequired(List<Long> ids, Integer required);

    AiApiSyncResultDTO syncApis(List<AiApiRegistry> apiList, List<AiApiParam> paramList);
}
