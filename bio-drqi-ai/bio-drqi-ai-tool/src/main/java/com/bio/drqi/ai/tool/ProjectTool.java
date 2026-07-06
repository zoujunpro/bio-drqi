package com.bio.drqi.ai.tool;

import com.bio.common.core.dto.BusinessException;
import org.springframework.stereotype.Component;

/**
 * 项目管理能力工具。
 * 后续把项目、载体、转化、取样、收获等现有 service 查询封装到这里。
 */
@Component
public class ProjectTool {

    public Object queryProjectData(String question) {
        throw new BusinessException("项目管理AI工具暂未接入具体业务查询");
    }
}
