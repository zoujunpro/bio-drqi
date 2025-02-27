package com.bio.drqi.seed;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class AliasNameSeedReqDTO {

    /**
     * 种子编号
     */
    @NotEmpty(message = "参数缺失：种子编号")
    private List<String> seedNumList;

    /**
     * 别名
     */
    @NotBlank(message = "参数缺失：别名")
    private String aliasName;
}

