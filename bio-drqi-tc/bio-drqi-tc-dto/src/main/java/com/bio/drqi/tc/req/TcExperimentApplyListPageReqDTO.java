package com.bio.drqi.tc.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bio.drqi.common.dto.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class TcExperimentApplyListPageReqDTO extends PageDTO {

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 物种编码
     */
    private String speciesCode;

    /**
     * 实验编号
     */
    private String experimentCode;
}
