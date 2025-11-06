package com.bio.drqi.bsm.kd.dto.model;

import com.bio.drqi.bsm.kd.enums.KdParentGroupEnum;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

@Data
public class MoveStockSaveModel extends KdModel {


    private String F_WAUJ_UUID;
    /**
     * 单据类型
     */
    private FBillTypeIDModel FBillTypeID;

    /**
     * 调拨日期
     */
    private String FDate;

    /**
     * 出入库类型
     */
    private String F_WAUJ_CRKLX;

    /**
     * 调拨类型
     */
    private String FTransferBizType;

    /**
     * 调出库存组织
     */
    private FStockOutOrgIdModel FStockOutOrgId;

    /**
     * 调出货主类型
     */
    private String FOwnerTypeOutIdHead;

    /**
     * 调出货主
     */
    private FOwnerOutIdHeadModel FOwnerOutIdHead;

    /**
     * 调入库存组织
     */
    private FStockOrgIdModel FStockOrgId;

    /**
     * 调入货主类型
     */
    private String FOwnerTypeIdHead;

    /**
     * 调入货主
     */
    private FOwnerIdHeadModel FOwnerIdHead;

    private List<FBillEntryModel> FBillEntry;

    private String FNote;



    public MoveStockSaveModel(String f_WAUJ_UUID, String FDate, KdParentGroupEnum kdParentGroupEnum, String orgCode, String productInnerCode, BigDecimal moveNumber, String srcStockId, String destStockId) {
        F_WAUJ_UUID = f_WAUJ_UUID;
        this.FBillTypeID = new FBillTypeIDModel("ZJDB01_SYS");
        this.FDate = FDate;
        F_WAUJ_CRKLX = kdParentGroupEnum.type;
        this.FTransferBizType = "InnerOrgTransfer";
        this.FStockOutOrgId = new FStockOutOrgIdModel(orgCode);
        this.FOwnerTypeOutIdHead = "BD_OwnerOrg";
        this.FOwnerOutIdHead = new FOwnerOutIdHeadModel(orgCode);
        this.FStockOrgId = new FStockOrgIdModel(orgCode);
        this.FOwnerTypeIdHead = "BD_OwnerOrg";
        this.FOwnerIdHead = new FOwnerIdHeadModel(orgCode);
        this.FBillEntry = Arrays.asList(new FBillEntryModel(productInnerCode, moveNumber, srcStockId, destStockId, orgCode));
        this.FNote="";
    }

    @Data
    private class FBillEntryModel {

        /**
         * 物料编码
         */
        private FMaterialIdModel FMaterialId;

        /**
         * 调拨数量
         */
        private BigDecimal FQty;
        /**
         * 调出仓库
         */

        private FSrcStockIdModel FSrcStockId;


        /**
         * 调入仓库
         */
        private FDestStockIdModel FDestStockId;


        /**
         * 调出货主类型
         */
        private String FOwnerTypeOutId;

        /**
         * 调出货主
         */
        private FOwnerOutIdModel FOwnerOutId;

        /**
         * 调入货主类型
         */
        private String FOWNERTYPEID;

        /**
         * 调入货主
         */
        private FOWNERIDModel FOWNERID;

        /**
         * 调入保管者类型
         */
        private String FKeeperTypeId;

        /**
         * 调入保管者
         */
        private FKeeperIdModel FKeeperId;


        /**
         * 调出保管者类型
         */
        private String FKeeperTypeOutId;

        /**
         * 调出保管者
         */
        private FKeeperOutIdModel FKeeperOutId;


        public FBillEntryModel(String productInnerCode, BigDecimal FQty, String FSrcStockId, String fDestStockId, String orgCode) {
            this.FMaterialId = new FMaterialIdModel(productInnerCode);
            this.FQty = FQty;
            this.FSrcStockId = new FSrcStockIdModel(FSrcStockId);
            this.FDestStockId = new FDestStockIdModel(fDestStockId);
            this.FOwnerTypeOutId = "BD_OwnerOrg";
            this.FOwnerOutId = new FOwnerOutIdModel(orgCode);
            this.FOWNERTYPEID = "BD_OwnerOrg";
            this.FOWNERID = new FOWNERIDModel(orgCode);
            this.FKeeperTypeId = "BD_KeeperOrg";
            this.FKeeperId = new FKeeperIdModel(orgCode);
            this.FKeeperTypeOutId = "BD_KeeperOrg";
            this.FKeeperOutId = new FKeeperOutIdModel(orgCode);
        }
    }


    @Data
    private class FKeeperOutIdModel {
        private String FNumber;

        public FKeeperOutIdModel(String FNumber) {
            this.FNumber = FNumber;
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
    private class FOwnerIdHeadModel {
        private String FNumber;

        public FOwnerIdHeadModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FOwnerOutIdModel {
        private String FNumber;

        public FOwnerOutIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    /**
     * 调入仓库
     */
    @Data
    private class FSrcStockIdModel {

        private String FNumber;

        public FSrcStockIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    /**
     * 调出仓库
     */
    @Data
    private class FDestStockIdModel {

        private String FNumber;

        public FDestStockIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    /**
     * 物料编码
     */
    @Data
    private class FMaterialIdModel {
        private String FNumber;

        public FMaterialIdModel(String FNumber) {
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

    @Data
    private class FStockOutOrgIdModel {

        private String FNumber;

        public FStockOutOrgIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data

    private class FOwnerOutIdHeadModel {
        private String FNumber;

        public FOwnerOutIdHeadModel(String FNumber) {
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
