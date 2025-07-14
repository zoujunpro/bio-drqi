package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;


@Data
public class ReturnStockModel extends KdModel {

    /**
     * 退料类型
     */
    private String FMRTYPE;

    /**
     * 退料方式
     */
    private String FMRMODE;

    /**
     * 退料组织
     */
    private FStockOrgIdModel FStockOrgId;

    /**
     * 入库日期
     */
    private String FDate;

    /**
     * 单据类型
     */
    private FBillTypeIDModel FBillTypeID;


    /**
     * 需求组织
     */
    private FRequireOrgIdModel FRequireOrgId;

    /**
     * 采购组织
     */

    private FPurchaseOrgIdModel FPurchaseOrgId;

    /**
     * 供应商
     */
    private FSupplierIDModel FSupplierID;

    /**
     * 货主类型
     */
    private String FOwnerTypeIdHead;

    /**
     * 货主
     */
    private FOwnerIdHeadModel FOwnerIdHead;

    private FPURMRBENTRYModel FPURMRBENTRY;


    @Data
    private class FPURMRBENTRYModel {
        /**
         * 物料编码
         */
        private FMaterialIdModel FMaterialId;

        /**
         * 退货实收数量
         */
        private BigDecimal FRMREALQTY;


        /**
         * 仓库
         */
        private FSTOCKIDModel FSTOCKID;

        /**
         * 货主类型
         */
        private String FOWNERTYPEID;

        /**
         * 货主
         */
        private FOWNERIDModel FOWNERID;
    }


    @Data
    private class FOWNERIDModel{
        private String FNumber;
    }

    @Data
    private class FSTOCKIDModel{
        private String FNumber;
    }
    @Data
    private class FRequireOrgIdModel {
        private String FNumber;
    }

    @Data
    private class FPurchaseOrgIdModel {
        private String FNumber;
    }

    @Data
    private class FSupplierIDModel {
        private String FNumber;
    }

    @Data
    private class FOwnerIdHeadModel {
        private String FNumber;
    }

    @Data
    private class FMaterialIdModel {
        private String FNumber;
    }

    @Override
    public List<String> buildModifyFields() {
        return null;
    }


    @Data
    private class FBillTypeIDModel {
        private String FNumber;
    }

    @Data
    private class FStockOrgIdModel {
        private String FNumber;
    }
}
