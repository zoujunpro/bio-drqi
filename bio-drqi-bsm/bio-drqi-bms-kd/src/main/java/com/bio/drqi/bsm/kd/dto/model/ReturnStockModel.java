package com.bio.drqi.bsm.kd.dto.model;

import com.bio.drqi.bsm.kd.contents.KdContents;
import com.bio.drqi.bsm.kd.enums.KdFBillTypeIDEnum;
import com.bio.drqi.bsm.kd.enums.KdParentGroupEnum;
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


    public ReturnStockModel(String orgCode, String FDate, KdParentGroupEnum kdParentGroupEnum, String kdSupplierId, String materialId, BigDecimal returnNumber, String stockCode,String projectCode) {
        this.FMRTYPE = "B";
        this.FMRMODE = "A";
        this.FStockOrgId = new FStockOrgIdModel(orgCode);
        this.FDate = FDate;
        this.FBillTypeID = new FBillTypeIDModel(KdFBillTypeIDEnum.ofKdParentGroupEnum(kdParentGroupEnum).code);
        this.FRequireOrgId = new FRequireOrgIdModel(orgCode);
        this.FPurchaseOrgId = new FPurchaseOrgIdModel(orgCode);
        this.FSupplierID = new FSupplierIDModel(kdSupplierId);
        this.FOwnerTypeIdHead = KdContents.OWNER;
        this.FOwnerIdHead = new FOwnerIdHeadModel(orgCode);
        this.FPURMRBENTRY = new FPURMRBENTRYModel(materialId, returnNumber, stockCode, orgCode,projectCode);
    }

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

        private FLotModel FLot;

        public FPURMRBENTRYModel(String materialId, BigDecimal FRMREALQTY, String stockCode, String orgCode,String projectCode) {
            this.FMaterialId = new FMaterialIdModel(materialId);
            this.FRMREALQTY = FRMREALQTY;
            this.FSTOCKID = new FSTOCKIDModel(stockCode);
            this.FOWNERTYPEID = KdContents.OWNER;
            this.FOWNERID = new FOWNERIDModel(orgCode);
            this.FLot=new FLotModel(projectCode);
        }
    }


    @Data
    private class FLotModel{
        private String FNumber;

        public FLotModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }
    @Data
    private class FOWNERIDModel {
        private String FNumber;

        public FOWNERIDModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FSTOCKIDModel {
        private String FNumber;

        public FSTOCKIDModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FRequireOrgIdModel {
        private String FNumber;

        public FRequireOrgIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FPurchaseOrgIdModel {
        private String FNumber;

        public FPurchaseOrgIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FSupplierIDModel {
        private String FNumber;

        public FSupplierIDModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FOwnerIdHeadModel {
        private String FNumber;

        public FOwnerIdHeadModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FMaterialIdModel {
        private String FNumber;

        public FMaterialIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Override
    public List<String> buildModifyFields() {
        return null;
    }


    @Data
    private class FBillTypeIDModel {
        private String FNumber;

        public FBillTypeIDModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FStockOrgIdModel {
        private String FNumber;

        public FStockOrgIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }
}
