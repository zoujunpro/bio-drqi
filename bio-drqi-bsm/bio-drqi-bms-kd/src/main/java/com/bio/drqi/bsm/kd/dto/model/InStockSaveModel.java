package com.bio.drqi.bsm.kd.dto.model;

import com.bio.drqi.bsm.kd.contents.KdContents;
import com.bio.drqi.bsm.kd.enums.KdFBillTypeIDEnum;
import com.bio.drqi.bsm.kd.enums.KdParentGroupEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;
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

    private String F_WAUJ_UUID;

    /**
     * 单据编号
     */
    private String FBillNo;

    private FStockOrgIdModel FStockOrgId;


    /**
     * 入库日期
     */
    private String FDate;

    /**
     * 出入库类型
     */
    private String F_WAUJ_CRKLX;

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


    private List<FInStockEntryModel> FInStockEntry;


    public InStockSaveModel(String kdProjectType, String orderDetailId, String FDate, KdParentGroupEnum kdParentGroupEnum, String orgCode, String kdSupplierId, String kdMaterialId, BigDecimal fTaxPrice, BigDecimal reqlQty, String projectCode, String stockCode, BigDecimal taxRate,String orderNum) {
        this.FID = 0;
        this.FBillNo = null;
        this.FDate = FDate;
        this.F_WAUJ_CRKLX = kdParentGroupEnum.type;
        this.FBillTypeID = new FBillTypeIDModel(KdFBillTypeIDEnum.ofKdParentGroupEnum(kdParentGroupEnum).code);
        this.FOwnerTypeIdHead = KdContents.OWNER;
        this.F_WAUJ_UUID = orderDetailId;
        this.FOwnerIdHead = new FOwnerIdHeadModel(orgCode);
        this.FPurchaseOrgId = new FPurchaseOrgIdModel(orgCode);
        this.FSupplierId = new FSupplierIdModel(kdSupplierId);
        this.FStockOrgId = new FStockOrgIdModel(orgCode);
        this.FInStockEntry = Arrays.asList(new FInStockEntryModel(kdProjectType, kdMaterialId, fTaxPrice, reqlQty, orgCode, projectCode, stockCode, taxRate,orderNum));
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
         * 项目类型
         */
        private String F_WAUJ_XMLX;

        /**
         * 收获数量
         */
        private BigDecimal FRealQty;

        private String FOWNERTYPEID;

        private FOWNERIDModel FOWNERID;


        private FLotModel FLot;


        private FStockIdModel FStockId;

        private BigDecimal FEntryTaxRate;

        private Boolean FGiveAway;

        /**
         * 订单编号
         */
        private String F_WAUJ_QBSDDH;

        public FInStockEntryModel(String kdProjectType, String MaterialId, BigDecimal fTaxPrice, BigDecimal reqlQty, String orgCode, String projectCode, String stockCode, BigDecimal taxRate,String orderNum) {
            this.setFMaterialId(new FMaterialIdModel(MaterialId));
            this.setFTaxPrice(fTaxPrice);
            this.F_WAUJ_QBSDDH = orderNum;
            this.setFRealQty(reqlQty);
            this.setFOWNERTYPEID(KdContents.OWNER);
            this.setFOWNERID(new FOWNERIDModel(orgCode));
            this.FLot = new FLotModel(projectCode);
            this.FStockId = new FStockIdModel(stockCode);
            this.FEntryTaxRate = taxRate;
            this.F_WAUJ_XMLX = kdProjectType;
            if (fTaxPrice.doubleValue() == 0) {
                FGiveAway = true;
            } else {
                FGiveAway = false;
            }
        }
    }


    @Data
    private class FStockOrgIdModel {
        private String FNumber;

        public FStockOrgIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FStockIdModel {
        private String FNumber;

        public FStockIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    /**
     * 批号（项目号）
     */
    @Data
    private class FLotModel {
        private String FNumber;

        public FLotModel(String FNumber) {
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
