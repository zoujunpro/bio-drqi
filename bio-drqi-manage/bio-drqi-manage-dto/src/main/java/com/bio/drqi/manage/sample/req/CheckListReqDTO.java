package com.bio.drqi.manage.sample.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CheckListReqDTO {
    @NotBlank(message = "参数缺失：applyNo")
    private String applyNo;


}
