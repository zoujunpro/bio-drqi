package com.bio.drqi.manage.dto.project;

import com.bio.cer.validator.EnumValue;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Data
public class SubProjectCreateDTO {
    @NotNull(message = "项目ID参数缺失")
    private Integer projectId;


    private String projectName;

    private String projectCode;


    @Valid
    @NotNull(message = "子项目列表参数缺失")
    private List<Content> contentList;


    @Data
    public static class Content{
        @NotBlank(message = "子项目名称不能为空")
        private String subProjectName;

        @NotBlank(message = "子项目编码不能为空")
        private String subProjectCode;

        @EnumValue(strValues = {"P1", "P2", "P3"}, message = "优先级参数非法")
        private String priorityLevel;

        /**
         * 项目物种
         */
        @NotEmpty(message = "项目物种缺失")
        private List<String> speciesList;

        private String speciesCode;

        /**附件地址 支持多个*/
        private List<String> fileUrls=new ArrayList<>();

    }
}
