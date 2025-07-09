package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

import java.util.List;

/**
 * 物料
 */
@Data
public class InStockSaveModel extends KdModel {


    /**
     * 实体主键
     */
    private String FID;

    /**
     * 单据编号
     */
    private String FBillNo;


    /**
     * 入库日期
     */
    private String FDate;

    /**
     * 单据类型
     */
    private FBillTypeIDModel FBillTypeID;


    /**
     * 货主类型
     */
    private String FOwnerTypeIdHead;


    /**
     * 货主
     */
    private FOwnerIdHeadModel FOwnerIdHead;


    /**
     * 采购组织
     */
    private FPurchaseOrgIdModel FPurchaseOrgId;

    /**
     * 供应商
     */
    private FSupplierIdModel FSupplierId;


    private FInStockEntryModel FInStockEntry;






    @Override
    public List<String> buildModifyFields() {
        return null;
    }


    @Data
    private class FBillTypeIDModel{
        private String FNUMBER;
    }

    @Data
    private class  FOwnerIdHeadModel{
        private String FNumber;
    }

    @Data
    private class FPurchaseOrgIdModel{
        private String FNumber;
    }

    @Data
    private class FSupplierIdModel{
        private String FNumber;
    }


    @Data
    private class FOWNERIDModel{
        private String FNumber;
    }

    @Data
    private class FInStockEntryModel{
        /**
         * 物料编码
         */
        private String FMaterialId;

        /**
         * 含税单价
         */
        private String FTaxPrice;

        /**
         * 收获数量
         */
        private String FRealQty;

        private String FOWNERTYPEID;

        private FOWNERIDModel FOWNERID;

    }
}
