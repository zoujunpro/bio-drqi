package com.bio.drqi.ai.dto.rag;

import lombok.Data;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 文档语义检索入参。
 * 由 AI 对话、业务系统或文档系统调用；检索前会根据用户、部门、角色做权限过滤。
 */
@Data
public class AiDocumentSearchReqDTO implements Serializable {

    /**
     * 用户问题或检索关键词，会先向量化再到 pgvector 中召回相似片段。
     */
    @NotBlank(message = "检索问题不能为空")
    private String query;

    /**
     * 当前用户 ID，用于匹配 USER 权限。
     */
    private String userId;

    /**
     * 当前用户所属部门 ID 列表，用于匹配 DEPT 权限。
     */
    private List<String> deptIds = new ArrayList<String>();

    /**
     * 当前用户拥有的角色 ID 列表，用于匹配 ROLE 权限。
     */
    private List<String> roleIds = new ArrayList<String>();

    /**
     * 业务类型过滤条件，例如 PROJECT、CER、SOP；为空时不过滤。
     */
    private String bizType;

    /**
     * 业务 ID 过滤条件，例如项目 ID、实施方案 ID；为空时不过滤。
     */
    private String bizId;

    /**
     * 返回片段数量，默认 8，最大 50。
     */
    @Min(value = 1, message = "topK 不能小于 1")
    @Max(value = 50, message = "topK 不能大于 50")
    private Integer topK;

    private static final long serialVersionUID = 1L;
}
