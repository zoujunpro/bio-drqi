package com.bio.drqi.applet.dto.req;

import lombok.Data;

import java.util.List;

@Data
public class SeedlingReportReqDTO {

    private String vectorTaskCode;

    private String plantCode;

    /**
     * 1  正常 2异常
     */
    private String plantStatus;

    /**
     * 备注
     */
    private String remark;

    /**
     * 属性报备
     */
    private List<Attribute> attributes;

    /**
     * 图片
     */
    private List<String> pictureUrls;

    @Data
    public static class Attribute {
        private String name;
        private String desc;
        private String value;
    }


}
