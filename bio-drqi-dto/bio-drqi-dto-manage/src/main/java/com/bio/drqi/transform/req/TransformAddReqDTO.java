package com.bio.drqi.transform.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class TransformAddReqDTO  {

    /**
     * 子项目ID
     */
    private Integer vectorId;

    /**
     * 侵染数量
     */
    @NotNull(message = "侵染数量必填")
    private Integer infectNumber;

    /**
     * 侵染时间
     */
    private String infectDate;

    /**
     * 递送方式（实际使用的方式）
     */
    @NotBlank(message = "递送方式必填")
    private String deliveryMethod;

    /**
     * 转化编号
     */
    @NotBlank(message = "转化编号必填")
    private String transformCode;

    /**
     * 受体材料
     */
    @NotBlank(message = "受体材料必填")
    private String acceptorMaterial;



}
