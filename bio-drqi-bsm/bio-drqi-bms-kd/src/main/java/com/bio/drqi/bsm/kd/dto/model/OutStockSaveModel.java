package com.bio.drqi.bsm.kd.dto.model;

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


    }


    @Data
    private class FKeeperIdModel {
        private String FNumber;

    }

    @Data
    private class FOWNERIDModel {
        private String FNumber;

    }

    @Data
    private class FMaterialIdModel {
        private String FNumber;
    }

    @Data
    private class FSTOCKIDModel {
        private String FNumber;

    }

    @Data

    private class FPurchaseOrgIdModel {
        private String FNumber;
    }

    @Data
    private class FOwnerIdHeadModel {
        private String FNumber;

    }

    @Data
    private class FRequireOrgIdModel {
        private String FNumber;
    }

    @Data
    private class FBillTypeIDModel {
        private String FNUMBER;
    }


    @Override
    public List<String> buildModifyFields() {
        return null;
    }


}
