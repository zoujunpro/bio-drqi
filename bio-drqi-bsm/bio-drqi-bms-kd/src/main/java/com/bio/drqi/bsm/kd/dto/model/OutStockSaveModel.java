package com.bio.drqi.bsm.kd.dto.model;

import com.bio.drqi.bsm.kd.contents.KdContents;
import com.bio.drqi.bsm.kd.enums.KdFBillTypeIDEnum;
import com.bio.drqi.bsm.kd.enums.KdParentGroupEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 出库
 */
@Data
public class OutStockSaveModel extends KdModel {


    /**
     * 出库日期
     */
    private String FDate;

    /**
     * 单据类型
     */
    private FBillTypeIDModel fFBillTypeID;

    /**
     * 需求组织
     */
    private FRequireOrgIdModel FRequireOrgId;

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


    private FEntityModel FEntity;


    public OutStockSaveModel(String FDate, KdParentGroupEnum kdParentGroupEnum, String orgCode, String kdMaterialId, BigDecimal FQty, String stockId) {
        this.FDate = FDate;
        this.fFBillTypeID = new FBillTypeIDModel(KdFBillTypeIDEnum.ofKdParentGroupEnum(kdParentGroupEnum).code);
        this.FRequireOrgId = new FRequireOrgIdModel(orgCode);
        this.FOwnerTypeIdHead = KdContents.OWNER;
        this.FOwnerIdHead = new FOwnerIdHeadModel(orgCode);
        this.FPurchaseOrgId = new FPurchaseOrgIdModel(orgCode);
        this.FEntity = new FEntityModel(kdMaterialId, FQty, stockId, orgCode);
    }

    @Data
    private class FEntityModel {

        /**
         * 物料编码
         */
        private FMaterialIdModel FMaterialId;

        /**
         * 实发数量
         */
        private BigDecimal FQty;

        /**
         * 发货仓库
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

        /**
         * 保管者类型
         */
        private String FKeeperTypeId;

        /**
         * 保管者
         */
        private FKeeperIdModel FKeeperId;

        public FEntityModel(String kdMaterialId, BigDecimal FQty, String stockId, String orgCode) {
            this.FMaterialId = new FMaterialIdModel(kdMaterialId);
            this.FQty = FQty;
            this.FSTOCKID = new FSTOCKIDModel(stockId);
            this.FOWNERTYPEID = KdContents.OWNER;
            this.FOWNERID = new FOWNERIDModel(orgCode);
            this.FKeeperTypeId = KdContents.KEEP;
            this.FKeeperId = new FKeeperIdModel(orgCode);
        }
    }


    @Data
    private class FKeeperIdModel {
        private String FNumber;

        public FKeeperIdModel(String FNumber) {
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
    private class FMaterialIdModel {
        private String FNumber;

        public FMaterialIdModel(String FNumber) {
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

    private class FPurchaseOrgIdModel {
        private String FNumber;

        public FPurchaseOrgIdModel(String FNumber) {
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
    private class FRequireOrgIdModel {
        private String FNumber;

        public FRequireOrgIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FBillTypeIDModel {
        private String FNUMBER;

        public FBillTypeIDModel(String FNUMBER) {
            this.FNUMBER = FNUMBER;
        }
    }


    @Override
    public List<String> buildModifyFields() {
        return null;
    }


}
