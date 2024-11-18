package com.bio.cer.project.req;

import com.bio.cer.base.PageDTO;
import com.bio.cer.validator.EnumValue;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProjectListReqDTO extends PageDTO {
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 项目编码
     */
    private String projectCode;
    /**
     * 项目物种
     */
    private String species;

    /**
     * 编辑类型  1基因编辑 2转基因
     */
    private String geneEditMethod;

    /**
     * 项目负责人
     */
    private Integer ownerUserId;

    /**
     * 开始日期
     */
    private String beginDate;
    /**
     * 结束日期
     */
    private String endDate;

    private Order order;


    @Data
    @Valid
    public static  class  Order{

        private String fieldName;

        /**
         * 正序 asc    倒叙 desc
         */
        @EnumValue(strValues = {"asc","desc"},message = "排序参数异常")
        private String orderType;
    }

}
