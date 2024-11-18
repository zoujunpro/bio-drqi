package com.bio.cer.sample.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CheckListReqDTO {
    @NotBlank(message = "参数缺失：applyNo")
    private String applyNo;


}
