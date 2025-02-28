package com.bio.drqi.manage.system.rsp;

import lombok.Data;


@Data
public class DictInfoRspDTO {

    /**
     * 字典名称
     */
    private String dictName;

    /**
     * 字典类型
     */
    private String dictType;

    /**
     * 字典值名称
     */
    private String dictValueName;

    /**
     * 字典值编码
     */
    private String dictValueCode;


}
