package com.bio.drqi.manage.feign;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class PushAgrobacteriumToTJDBDTO {

    /**
     * 质粒ID
     */
    @JsonProperty("plasmidID")
    private String plasmidId;

    /**
     * 储存位置
     */
    private String local;

    /**
     * 模板ID
     */
    private String temid;

    /**
     * 抗性
     */
    private String resistance;

    /**
     * 菌株名称
     */
    private String strain;

    /**
     * 制备日期
     */
    @JsonProperty("Making_date")
    private String makingDate;

    /**
     * 备注/补充说明
     */
    @JsonProperty("Supplement")
    private String supplement;
}
