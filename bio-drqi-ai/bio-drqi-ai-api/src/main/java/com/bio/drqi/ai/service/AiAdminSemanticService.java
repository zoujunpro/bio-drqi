package com.bio.drqi.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.entity.AiBusinessTerm;
import com.bio.drqi.ai.entity.AiIntentKeyword;

public interface AiAdminSemanticService {

    Page<AiBusinessTerm> termPage(AiPageReqDTO reqDTO);

    void saveTerm(AiBusinessTerm entity);

    void deleteTerm(Long id);

    Page<AiIntentKeyword> intentKeywordPage(AiPageReqDTO reqDTO);

    void saveIntentKeyword(AiIntentKeyword entity);

    void deleteIntentKeyword(Long id);
}
