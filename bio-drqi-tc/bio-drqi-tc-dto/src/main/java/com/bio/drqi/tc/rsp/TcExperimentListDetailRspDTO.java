package com.bio.drqi.tc.rsp;

import lombok.Data;

import java.util.Date;

@Data
public class TcExperimentListDetailRspDTO {

    private Integer id;

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 小区编号
     */
    private String regionNum;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 项目编号
     */
    private String projectCode;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;

    /**
     * 物种
     */
    private String speciesCode;

    /**
     * 品种
     */
    private String breedName;

    /**
     * 目标性状
     */
    private String targetCharacter;

    /**
     * 代次编号
     */
    private String generationCode;

    /**
     * 田测基因型
     */
    private String tcGene;

    /**
     * 小区面积
     */
    private String regionArea;

    /**
     * 面积单位
     */
    private String areaUnit;

    /**
     * 株距
     */
    private String plantSpace;

    /**
     * 行数
     */
    private String rowsNumber;

    /**
     * 行长
     */
    private String rowsLength;

    /**
     * 行距
     */
    private String rowsSpace;

    /**
     * 播种方式
     */
    private String seedingType;

    /**
     * 播种数量
     */
    private Integer seedingNumber;

    /**
     * 播种单位
     */
    private String seedingUnit;

    /**
     * 播种时间
     */
    private String seedingTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 任务编号
     */
    private String taskNum;

    /**
     * 创建人ID
     */
    private Integer createUserId;

    /**
     * 创建人
     */
    private String createUserName;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 出苗率
     */
    private String emergenceRate;

    /**
     * 移栽时间
     */
    private String transplantTime;
}
