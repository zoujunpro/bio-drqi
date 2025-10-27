package com.bio.drqi.bsm.kd.dto.model;

import cn.hutool.json.JSONUtil;
import com.bio.drqi.bsm.kd.enums.KdFCategoryIDEnum;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 物料
 */
@Data
public class MaterialSaveModel extends KdModel {
    /**
     * 实体主键
     */
    private Integer FMATERIALID;

    /**
     * 编码
     */
    private String Fnumber;

    /**
     * 名称
     */
    private String Fname;

    /**
     * 规格型号
     */
    private String Fspecification;

    /**
     * 品牌
     */
    private String F_WAUJ_PP;

    /**
     * 物料分组
     */
    private FMaterialGroupModel FMaterialGroup;


    private SubHeadEntityModel SubHeadEntity;

    private SubHeadEntity1Model SubHeadEntity1;


    public MaterialSaveModel() {
    }

    public MaterialSaveModel(String fnumber, String fname, String fspecification, String f_WAUJ_PP, String kdMaterialGroupId, String kdCategoryCode) {
        this.FMATERIALID = 0;
        Fnumber = fnumber;
        Fname = fname;
        Fspecification = fspecification;
        F_WAUJ_PP = f_WAUJ_PP;
        this.FMaterialGroup = new FMaterialGroupModel(kdMaterialGroupId);
        SubHeadEntity = new SubHeadEntityModel(kdCategoryCode);
        SubHeadEntity1 = new SubHeadEntity1Model(true);
    }

    @Data
    private class SubHeadEntity1Model {
        boolean FIsBatchManage;

        public SubHeadEntity1Model(boolean FIsBatchManage) {
            this.FIsBatchManage = FIsBatchManage;
        }
    }

    @Data
    private class FMaterialGroupModel {
        private String FNumber;

        public FMaterialGroupModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Data
    private class SubHeadEntityModel {
        /**
         * 物料属性
         */
        private String FErpClsID = "1";


        /**
         * 特征子项
         */
        private String FFeatureItem = "1";


        /**
         * 存货类别
         */
        private FCategoryIDModel FCategoryID;

        /**
         * 基本单位
         */
        private FBaseUnitIdModel FBaseUnitId;

        /**
         * 是否允许采购
         */
        private boolean FIsPurchase;

        /**
         * 是否允许库存
         */
        private boolean FIsInventory;

        /**
         * 允许委外
         */
        private boolean FIsSubContract;

        /**
         * 允许销售
         */
        private boolean FIsSale;


        /**
         * 允许生产
         */
        private boolean FIsProduce;

        /**
         * 允许资产
         */
        private boolean FIsAsset;

        private SubHeadEntityModel(String kdCategoryCode) {
            this.FErpClsID = "1";
            this.FFeatureItem = "1";
            this.FCategoryID = new FCategoryIDModel(kdCategoryCode);
            this.FBaseUnitId = new FBaseUnitIdModel("Pcs");
            this.FIsPurchase = true;
            this.FIsInventory = true;
            this.FIsSubContract = true;
            this.FIsSale = true;
            this.FIsProduce = true;
            this.FIsAsset = true;
        }
    }


    @Data
    private class FCategoryIDModel {
        private String FNumber;

        public FCategoryIDModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }


    @Data
    private class FBaseUnitIdModel {
        private String FNumber;

        public FBaseUnitIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }

    @Override
    public List<String> buildModifyFields() {
        List<String> list = new ArrayList<>();
        list.add("Fnumber");
        list.add("FSpecification");
        list.add("Fname");
        return list;
    }

}
