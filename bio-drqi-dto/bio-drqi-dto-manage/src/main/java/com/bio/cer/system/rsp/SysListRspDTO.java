package com.bio.cer.system.rsp;

import lombok.Data;

@Data
public class SysListRspDTO {
    /**
     * 主键ID
     */
    private Integer id;

    /**
     * 系统名称
     */
    private String systemName;
}
