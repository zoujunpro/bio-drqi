package com.bio.drqi.applet.dto.rsp;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class ScanCodeTransformTransRspDTO {

    private String projectCode;

    private String projectName;

    private String subProjectCode;

    private String vectorTaskCode;

    private Transform transform;

    private List<String> urls;


    @Data
    public static class Transform{

        /**
         * 侵染数量
         */
        private Integer infectNumber;

        /**
         * 侵染日期
         */
        private String infectDate;

        /**
         * 递送方式（实际使用的方式）
         */
        private String deliveryMethod;

        /**
         * 转化编号
         */
        private String transformCode;

        /**
         * 受体材料
         */
        private String acceptorMaterial;

        /**
         * 创建时间
         */
        private Date createTime;

        /**
         * 更新时间
         */
        private Date updateTime;

        /**
         * 创建人名称
         */
        private String createUserName;

        /**
         * 创建人ID
         */
        private Integer createUserId;

        /**
         * 质粒名称
         */
        private String plasmidName;

        /*转化名称*/
        private String transformName;

        private String speciesCode;

    }

}
