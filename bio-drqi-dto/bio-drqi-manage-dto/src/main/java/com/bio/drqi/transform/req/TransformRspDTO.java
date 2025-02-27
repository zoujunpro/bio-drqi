package com.bio.drqi.transform.req;

import lombok.Data;

@Data
public class TransformRspDTO {
    /**
     * 质粒名称
     */
    private String plasmidName;
    /**
     * 侵染数量
     */
    private Integer infectNumber;
    /**
     * 侵染时间
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

}
