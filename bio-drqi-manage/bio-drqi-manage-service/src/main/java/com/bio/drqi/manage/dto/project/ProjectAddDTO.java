package com.bio.drqi.manage.dto.project;


import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

@Data
public class ProjectAddDTO {


    /**
     * 项目名称
     */
    @NotBlank(message = "项目名称缺失")
    @Size(max = 255,message = "项目名称最大长度255")
    private String projectName;

    /**
     * 项目编号
     */
    @NotBlank(message = "项目编号缺失")
    @Size(max = 5,message = "项目编号5")
    private String projectCode;

    /**
     * 项目类型 1常规项目 2自研项目
     */
    @EnumValue(strValues = {"1", "2"}, message = "项目类型参数非法")
    private String projectType;

    /**
     * 编辑类型  1基因编辑 2转基因
     */
    @NotBlank(message = "编辑类型缺失")
    @EnumValue(strValues = {"1", "2"}, message = "编辑类型参数非法")
    private String geneEditMethod;

    /**
     * 项目预计开始日期
     */
    @NotBlank(message = "项目预计开始日期缺失")
    private String expectStartDate;
    @EnumValue(strValues = {"1", "2","3"}, message = "项目类型参数非法")
    private String projectCategoryCode;



}
