package com.bio.drqi.manage.dto.project;
import com.bio.drqi.common.dto.BaseBioTaskDTO;
import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Data
public class TransformDTO extends BaseBioTaskDTO {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID缺失")
    private Integer projectId;

    /**
     * 子项目ID
     */
    @NotNull(message = "子项目ID缺失")
    private Integer subProjectId;

    /**
     * 任务ID
     */
    @NotNull(message = "载体任务ID缺失")
    private Integer vectorTaskId;

    private String projectCode;

    private String projectName;

    private String subProjectCode;

    private String  geneEditMethod;

    private String vectorTaskCode;


    @Valid
    @NotNull(message = "转化内容必填")
    private List<Content> contentList;

    @Data
    @Valid
    public static class Content{
        /**
         * 侵染数量
         */
        @NotNull(message = "未填写转化数量")
        private Integer infectNumber;

        /**
         * 侵染日期
         */
        @NotBlank(message = "转化日期必填")
        private String infectDate;

        /**
         * 递送方式 A 农杆菌转化 B基因枪 P原生质体转化 V病毒载体
         */
        @EnumValue(strValues = {"A", "B", "P", "V"}, message = "递送方式参数非法")
        private String deliveryMethod;

        /**
         * 转化编号
         */
        private String transformCode;

        /**
         * 受体材料
         */
        @NotBlank(message = "受体材料必填")
        private String acceptorMaterial;

    }



}
