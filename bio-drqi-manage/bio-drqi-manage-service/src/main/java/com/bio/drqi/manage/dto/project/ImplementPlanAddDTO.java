package com.bio.drqi.manage.dto.project;

import com.bio.drqi.common.validator.EnumValue;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class ImplementPlanAddDTO {

    /**
     * 项目ID
     */
    @NotNull(message = "项目ID必填")
    private Integer projectId;

    private String projectName;

    private String projectCode;

    private String subProjectCode;
    /**
     * 子项目ID
     */
    @NotNull(message = "子项目ID必填")
    private Integer subProjectId;
    /**
     * 载体构建任务编码
     */
    @NotBlank(message = "实施方案编码必填")
    private String vectorTaskCode;


    @NotBlank(message = "物种必选")
    private String speciesCode;

    @NotBlank(message = "品种必填")
    private String breedCode;
    /**
     * 预计开始日期
     */
    @NotBlank(message = "预期开始时间必填")
    private String expectStartDate;

    /**
     * 预期项目周期必填
     */
    @NotBlank(message = "预期项目周期必填")
    private String expectPeriod;

    /**
     * 递送方式  B基因枪、P原生质体转化、A农杆菌转化、V病毒载体
     */
    @EnumValue(strValues = {"A", "B", "P", "V"}, message = "递送方式参数非法")
    private String deliveryMethod;
    /**
     * 受体材料
     */
    private String acceptorMaterial;


    /**
     * 监管级别 1 无，2 DNA-free； 3 transgene-free
     */
    @EnumValue(strValues = {"1", "2", "3"}, message = "监管级别参数非法")
    private String supervisionLevelCode;

    /**
     * 编辑类型  1 KO，2点突变，3精准小，4精准大
     */
    @EnumValue(strValues = {"1", "2", "3"}, message = "编辑类型参数非法")
    private String editType;

    @NotBlank(message = "期望阳性表必填")
    private String expectedPositiveSeed;

    private String  sampleCodePrefix;


}
