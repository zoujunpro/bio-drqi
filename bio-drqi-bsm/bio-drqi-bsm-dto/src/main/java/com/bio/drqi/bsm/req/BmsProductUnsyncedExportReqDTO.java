package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class BmsProductUnsyncedExportReqDTO {

    /**
     * 所属月份，格式：yyyy-MM
     */
    @NotBlank(message = "所属月份不能为空")
    @Pattern(regexp = "\\d{4}-(0[1-9]|1[0-2])", message = "所属月份格式必须为yyyy-MM")
    private String month;
}
