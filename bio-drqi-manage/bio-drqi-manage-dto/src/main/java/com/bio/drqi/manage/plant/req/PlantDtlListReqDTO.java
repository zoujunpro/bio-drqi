package com.bio.drqi.manage.plant.req;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.bio.drqi.manage.base.PageDTO;
import lombok.Data;

import java.util.Date;

@Data
public class PlantDtlListReqDTO extends PageDTO {

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 所属项目编码
     */
    private String projectCode;

    /**
     * 子项目编号
     */
    private String subProjectCode;

    /**
     * 任务编码
     */
    private String vectorTaskCode;

    /**
     * 取样编号
     */
    private String sampleCode;

    /**
     * 项目物种
     */
    private String speciesCode;



}
