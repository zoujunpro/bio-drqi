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
    private Object FMaterialGroup;


    private SubHeadEntityModel SubHeadEntity;


    @Data
    public static class SubHeadEntityModel {
        /**
         * 物料属性
         */
        private String FErpClsID="1";


        /**
         * 特征子项
         */
        private String FFeatureItem="1";


        /**
         * 存货类别
         */
        private FCategoryIDModel FCategoryID;

        /**
         * 基本单位
         */
        private FBaseUnitIdModel FBaseUnitId=new FBaseUnitIdModel();

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
    public static class FCategoryIDModel {
        private String FNumber;
    }


    @Data
    public static class FBaseUnitIdModel {
        private String FNumber="Pcs";
    }

    @Override
    public List<String> buildModifyFields() {
        List<String> list=new ArrayList<>();
        list.add("Fnumber");
        list.add("FSpecification");
        return list;
    }
}
