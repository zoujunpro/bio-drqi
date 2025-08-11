package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

import java.math.BigDecimal;
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
        private  FDestStockIdModel FDestStockId;


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
         *调入保管者类型
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

        /**
         * 税率
         */
        private BigDecimal FEntryTaxRate;

    }


    @Data
    private class FKeeperOutIdModel{
        private String FNumber;
    }


    @Data
    private class FKeeperIdModel{
        private String FNumber;
    }

    @Data
    private class FOWNERIDModel{
        private String FNumber;
    }

    @Data
    private class FOwnerIdHeadModel {
        private String FNumber;

        public FOwnerIdHeadModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class FOwnerOutIdModel{
        private String FNumber;
    }

    /**
     * 调入仓库
     */
    @Data
    private class FSrcStockIdModel{

        private String FNumber;
    }
    /**
     * 调出仓库
     */
    @Data
    private class FDestStockIdModel{

        private String FNumber;
    }

    /**
     * 物料编码
     */
    @Data
    private class FMaterialIdModel {
        private String FNumber;
    }

    @Data
    private class FStockOrgIdModel {
        private String FNumber;
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
