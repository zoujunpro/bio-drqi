package com.bio.drqi.tc.rsp;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

@Data
public class TcExperimentListAllRspDTO {

    /**
     * 实验编号
     */
    private String experimentNum;

    /**
     * 物种名称
     */
    private String speciesName;

    /**
     * 上传附件
     */
    private String fileUrl;

    /**
     * 实验目的
     */
    private String experimentGoal;


    /**
     * 实验地点
     */
    private String experimentAddressCode;
    /**
     * 实验地点
     */
    private String experimentAddressName;

    /**
     * 申请人
     */
    private String applyUserName;

    /**
     * 申请人iD
     */
    private Integer applyUserId;

    private String breedingFlag;

    /**
     * 创建时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd")
    private Date createTime;

    private String pdNum;
}
