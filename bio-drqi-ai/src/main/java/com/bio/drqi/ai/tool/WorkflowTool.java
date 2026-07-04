package com.bio.drqi.ai.tool;

import com.bio.common.core.dto.BusinessException;
import org.springframework.stereotype.Component;

/**
 * 审批/工单能力工具。
 * 后续接 BioTaskController 背后的 service 或现有审批查询接口。
 */
@Component
public class WorkflowTool {

    public Object queryWorkflowData(String question) {
        throw new BusinessException("审批AI工具暂未接入具体业务查询");
    }
}
