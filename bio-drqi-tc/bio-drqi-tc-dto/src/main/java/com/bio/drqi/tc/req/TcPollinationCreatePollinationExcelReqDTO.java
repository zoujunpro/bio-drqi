package com.bio.drqi.tc.req;


import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class TcPollinationCreatePollinationExcelReqDTO {

    @NotBlank(message = "参数缺失：试验编号")
    private String experimentNum;

    private String sampleApplyNum;

    @NotEmpty(message = "缺失授粉内容")
    private List<Content> contentList;


    @Data
    @Valid
    public static class Content{
        /**
         * 小区编号
         */
        private String regionNum;

        /**
         * 种子编号
         */
        private String seedNum;

        /**
         * 实施方案编号
         */
        private String vectorTaskCode;


        /**
         * 田测基因型
         */
        private String tcGene;

        /**
         * 保留苗数量
         */
        private Integer stayNumber;

        /**
         * 亲本选择     mother ,father,parent
         */
        @EnumValue(message = "参数非法：亲本选择" ,strValues = {"mother","father","parent"})
        private String parentFlag;

        /**
         * 是否区分单株 Y N
         */
        @EnumValue(message = "参数非法：是否区分单株" ,strValues = {"Y","N"})
        private String  singlePlantFlag;

        /**
         * 单株数量
         */
        private Integer singlePlantNumber;







    }

}
