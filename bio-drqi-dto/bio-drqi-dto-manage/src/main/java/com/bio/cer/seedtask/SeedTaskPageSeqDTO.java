package com.bio.cer.seedtask;

import com.bio.cer.base.PageDTO;
import com.bio.cer.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class SeedTaskPageSeqDTO extends PageDTO {

    /**申请编号*/
    private String taskNum;

    private String taskTypeCode;
}
