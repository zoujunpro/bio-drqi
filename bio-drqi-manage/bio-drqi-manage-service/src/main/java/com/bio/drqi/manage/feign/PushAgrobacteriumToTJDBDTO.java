package com.bio.drqi.manage.feign;

import lombok.Data;

@Data
public class PushAgrobacteriumToTJDBDTO {

    /**
     * 质粒ID
     */
    private String plasmidID;

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
    private String Making_date;

    /**
     * 备注/补充说明
     */
    private String Supplement;
}
