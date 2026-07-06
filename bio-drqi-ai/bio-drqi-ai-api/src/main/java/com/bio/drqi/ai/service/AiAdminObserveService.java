package com.bio.drqi.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.entity.AiQueryAuditLog;

public interface AiAdminObserveService {

    Page<AiQueryAuditLog> auditPage(AiPageReqDTO reqDTO);
}
