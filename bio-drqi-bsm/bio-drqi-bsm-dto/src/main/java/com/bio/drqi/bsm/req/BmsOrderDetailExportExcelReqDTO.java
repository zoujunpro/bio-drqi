package com.bio.drqi.bsm.req;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class BmsOrderDetailExportExcelReqDTO {

    private String orderNum;
    /**
     * 项目编号
     */
    private String projectCode;
    /**
     * 项目名称
     */
    private String projectName;
    /**
     * 供应商名称
     */
    private String supplierName;
    /**
     * 供应商编号
     */
    private String supplierCode;
    /**
     * 品牌名称
     */
    private String brandName;
    /**
     * 材料名称
     */
    private String productName;
    /**
     * 申请单位
     */
    @NotBlank(message = "申请单位缺失")
    private String applyUnitCode;


    /**
     * 采购申请人
     */
    private Integer applyUserId;

}
