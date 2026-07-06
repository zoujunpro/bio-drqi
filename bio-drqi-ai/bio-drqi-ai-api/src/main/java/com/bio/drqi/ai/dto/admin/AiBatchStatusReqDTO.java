package com.bio.drqi.ai.dto.admin;

import lombok.Data;

import java.util.List;

@Data
public class AiBatchStatusReqDTO {

    /**
     * 需要批量处理的主键 ID。
     */
    private List<Long> ids;

    /**
     * 目标 AI 开通状态：1 开通，0 禁用。
     */
    private Integer aiEnabled;

    /**
     * 目标必填状态：1 必填，0 非必填。
     */
    private Integer required;
}
