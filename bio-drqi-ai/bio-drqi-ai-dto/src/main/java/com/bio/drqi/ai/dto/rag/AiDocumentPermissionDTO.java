package com.bio.drqi.ai.dto.rag;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.io.Serializable;

/**
 * 文档可读权限入参。
 */
@Data
public class AiDocumentPermissionDTO implements Serializable {

    /**
     * 权限主体类型。
     * USER 表示指定用户可读；DEPT 表示指定部门可读；ROLE 表示指定角色可读；PUBLIC 表示所有用户可读。
     */
    @NotBlank(message = "权限主体类型不能为空")
    private String principalType;

    /**
     * 权限主体编码。
     * principalType 为 USER/DEPT/ROLE 时必传对应 ID；principalType 为 PUBLIC 时可以为空。
     */
    private String principalId;

    private static final long serialVersionUID = 1L;
}
