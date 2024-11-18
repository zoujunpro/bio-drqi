package com.bio.drqi.system.req;

import com.bio.drqi.base.PageDTO;
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
