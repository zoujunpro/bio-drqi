package com.bio.drqi.ai.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bio.drqi.ai.dto.admin.AiPageReqDTO;
import com.bio.drqi.ai.dto.workflow.AiWorkflowExecuteReqDTO;
import com.bio.drqi.ai.dto.workflow.AiWorkflowExecuteRspDTO;
import com.bio.drqi.ai.dto.workflow.AiWorkflowSaveReqDTO;
import com.bio.drqi.ai.entity.AiWorkflowDefinition;

public interface AiWorkflowService {

    Page<AiWorkflowDefinition> page(AiPageReqDTO reqDTO);

    AiWorkflowDefinition detail(Long id);

    void save(AiWorkflowSaveReqDTO reqDTO);

    void delete(Long id);

    AiWorkflowExecuteRspDTO execute(AiWorkflowExecuteReqDTO reqDTO);
}
