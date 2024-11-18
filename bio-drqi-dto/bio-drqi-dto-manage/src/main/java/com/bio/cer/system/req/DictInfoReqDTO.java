package com.bio.cer.system.req;

import com.bio.cer.base.PageDTO;
import lombok.Data;


@Data
public class DictInfoReqDTO extends PageDTO {

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;




}
