package com.bio.drqi.bsm.kd.dto.model;

import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.bsm.kd.contents.KdContents;
import com.bio.drqi.bsm.kd.enums.KdFBillTypeIDEnum;
import com.bio.drqi.bsm.kd.enums.KdParentGroupEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

/**
 * 物料
 */
@Data
public class InStockSaveModel extends KdModel {


    /**
     * 实体主键
     */
    private Integer FID = 0;

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


    public InStockSaveModel(String FDate, KdParentGroupEnum kdParentGroupEnum, String orgCode, String kdSupplierId, String kdMaterialId, BigDecimal fTaxPrice, BigDecimal reqlQty) {
        this.FID = 0;
        this.FBillNo = null;
        this.FDate = FDate;
        this.FBillTypeID = new FBillTypeIDModel(KdFBillTypeIDEnum.ofKdParentGroupEnum(kdParentGroupEnum).code);
        this.FOwnerTypeIdHead = KdContents.OWNER;
        this.FOwnerIdHead = new FOwnerIdHeadModel(orgCode);
        this.FPurchaseOrgId = new FPurchaseOrgIdModel(orgCode);
        this.FSupplierId = new FSupplierIdModel(kdSupplierId);
        this.FInStockEntry = new FInStockEntryModel(kdMaterialId, fTaxPrice, reqlQty, orgCode);
    }

    @Override
    public List<String> buildModifyFields() {
        return null;
    }


    @Data
    private class FInStockEntryModel {
        /**
         * 物料编码
         */
        private FMaterialIdModel FMaterialId;

        /**
         * 含税单价
         */
        private BigDecimal FTaxPrice;

        /**
         * 收获数量
         */
        private BigDecimal FRealQty;

        private String FOWNERTYPEID;

        private FOWNERIDModel FOWNERID;

        public FInStockEntryModel(String MaterialId, BigDecimal fTaxPrice, BigDecimal reqlQty, String orgCode) {
            this.setFMaterialId(new FMaterialIdModel(MaterialId));
            this.setFTaxPrice(fTaxPrice);
            this.setFRealQty(reqlQty);
            this.setFOWNERTYPEID(KdContents.OWNER);
            this.setFOWNERID(new FOWNERIDModel(orgCode));
        }
    }


    @Data
    private class FBillTypeIDModel {
        private String FNUMBER;

        public FBillTypeIDModel(String FNUMBER) {
            this.FNUMBER = FNUMBER;
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
    private class FPurchaseOrgIdModel {
        private String FNumber;

        public FPurchaseOrgIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FSupplierIdModel {
        private String FNumber;

        public FSupplierIdModel(String FNumber) {
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
}
