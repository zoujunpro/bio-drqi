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
    private Integer FMATERIALID = 0;

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
     * 物料分组
     */
    private FMaterialGroupModel FMaterialGroup;


    private SubHeadEntityModel SubHeadEntity;


    @Data
    private class FMaterialGroupModel {
        private String FNumber;
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
        private FBaseUnitIdModel FBaseUnitId = new FBaseUnitIdModel();

        /**
         * 是否允许采购
         */
        private boolean FIsPurchase = true;

        /**
         * 是否允许库存
         */
        private boolean FIsInventory = true;

        /**
         * 允许委外
         */
        private boolean FIsSubContract = true;

        /**
         * 允许销售
         */
        private boolean FIsSale = true;


        /**
         * 允许生产
         */
        private boolean FIsProduce = true;

        /**
         * 允许资产
         */
        private boolean FIsAsset = true;


        private boolean FIsBatchManage = true;


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
        private String FNumber = "Pcs";
    }

    @Override
    public List<String> buildModifyFields() {
        List<String> list = new ArrayList<>();
        list.add("Fnumber");
        list.add("FSpecification");
        return list;
    }

    public MaterialSaveModel buildFMaterialGroup(String fMaterialGroup) {
        FMaterialGroupModel fMaterialGroupModel = new FMaterialGroupModel();
        fMaterialGroupModel.setFNumber(fMaterialGroup);
        this.FMaterialGroup = fMaterialGroupModel;
        return this;
    }

    public MaterialSaveModel buildSubHeadEntity(String fCategoryID) {
        SubHeadEntityModel subHeadEntityModel = new SubHeadEntityModel();
        subHeadEntityModel.setFCategoryID(new FCategoryIDModel(fCategoryID));
        this.SubHeadEntity = subHeadEntityModel;
        return this;

    }

    public MaterialSaveModel(Integer FMATERIALID, String fnumber, String fname, String fspecification) {
        this.FMATERIALID = FMATERIALID;
        Fnumber = fnumber;
        Fname = fname;
        Fspecification = fspecification;
    }
}
