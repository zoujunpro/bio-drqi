package com.bio.drqi.bsm.kd.service;

import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.bsm.kd.OutStockSaveModel;
import com.bio.drqi.bsm.kd.dto.*;
import com.bio.drqi.bsm.kd.dto.base.KdApiBaseSaveRequestDTO;
import com.bio.drqi.bsm.kd.dto.base.KdModel;
import com.bio.drqi.bsm.kd.dto.ProjectSaveModel;
import com.bio.drqi.bsm.kd.enums.FormIdEnum;
import com.bio.drqi.bsm.kd.util.KdRequestUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KdApiServiceImpl implements KdApiService {


    @Override
    public String executeSave(FormIdEnum formIdEnum, KdModel kdModel, String FNumber) {
        switch (formIdEnum) {
            case CMK_BD_Brand:
                return bmsSave(kdModel, FNumber);
            case BD_MATERIAL:
                return materialSave(kdModel, FNumber);
            case BD_STOCK:
                return stockSave(kdModel, FNumber);
            case BD_FLEXVALUES:
                return fixValueSave(kdModel, FNumber);
            case BD_Supplier:
                return supplierSave(kdModel, FNumber);
            case k62a1e2f33daa4a738462728197b95678:
                return projectSave(kdModel, FNumber);
            case STK_InStock:
                return inStockSave(kdModel, FNumber);
            case STK_MisDelivery:
                return outStockSave(kdModel,FNumber);
            default:
                throw new BusinessException("数据异常，请检查金蝶配置");
        }

    }


    /**
     * 品牌保存
     *
     * @param kdModel
     * @param FNumber
     * @return
     */
    private String bmsSave(KdModel kdModel, String FNumber) {
        BmsSaveKdModel bmsSaveKdModel = (BmsSaveKdModel) kdModel;
        KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO = new KdApiBaseSaveRequestDTO();
        kdApiBaseSaveRequestDTO.setModel(bmsSaveKdModel.build(FNumber));
        return KdRequestUtil.save(FormIdEnum.CMK_BD_Brand, kdApiBaseSaveRequestDTO);
    }

    /**
     * 材料保存
     *
     * @param kdModel
     * @param FNumber
     * @return
     */
    private String materialSave(KdModel kdModel, String FNumber) {
        MaterialSaveModel materialSaveModel = (MaterialSaveModel) kdModel;
        KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO = new KdApiBaseSaveRequestDTO();
        kdApiBaseSaveRequestDTO.setModel(materialSaveModel.build(FNumber));
        return KdRequestUtil.save(FormIdEnum.BD_MATERIAL, kdApiBaseSaveRequestDTO);
    }

    /**
     * 库存保存
     *
     * @param kdModel
     * @param FNumber
     * @return
     */
    private String stockSave(KdModel kdModel, String FNumber) {
        StockSaveModel stockSaveModel = (StockSaveModel) kdModel;
        KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO = new KdApiBaseSaveRequestDTO();
        kdApiBaseSaveRequestDTO.setModel(stockSaveModel.build(FNumber));
        return KdRequestUtil.save(FormIdEnum.BD_STOCK, kdApiBaseSaveRequestDTO);
    }

    /**
     * 仓位保存
     *
     * @param kdModel
     * @param FNumber
     * @return
     */
    private String fixValueSave(KdModel kdModel, String FNumber) {
        FixValueSaveModel fixValueSaveModel = (FixValueSaveModel) kdModel;
        KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO = new KdApiBaseSaveRequestDTO();
        kdApiBaseSaveRequestDTO.setModel(fixValueSaveModel.build(FNumber));
        return KdRequestUtil.save(FormIdEnum.BD_FLEXVALUES, kdApiBaseSaveRequestDTO);
    }

    /**
     * 供应商保存
     *
     * @param kdModel
     * @param FNumber
     * @return
     */
    private String supplierSave(KdModel kdModel, String FNumber) {
        SupplierSaveModel supplierSaveModel = (SupplierSaveModel) kdModel;
        KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO = new KdApiBaseSaveRequestDTO();
        kdApiBaseSaveRequestDTO.setModel(supplierSaveModel.build(FNumber));
        return KdRequestUtil.save(FormIdEnum.BD_Supplier, kdApiBaseSaveRequestDTO);
    }

    /**
     * 项目保存
     *
     * @param kdModel
     * @param FNumber
     * @return
     */
    private String projectSave(KdModel kdModel, String FNumber) {
        ProjectSaveModel projectSaveModel = (ProjectSaveModel) kdModel;
        KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO = new KdApiBaseSaveRequestDTO();
        kdApiBaseSaveRequestDTO.setModel(projectSaveModel.build(FNumber));
        return KdRequestUtil.save(FormIdEnum.k62a1e2f33daa4a738462728197b95678, kdApiBaseSaveRequestDTO);
    }

    /**
     * 入库
     *
     * @param kdModel
     * @param FNumber
     * @return
     */
    private String inStockSave(KdModel kdModel, String FNumber) {
        InStockSaveModel inStockSaveModel = (InStockSaveModel) kdModel;
        KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO = new KdApiBaseSaveRequestDTO();
        kdApiBaseSaveRequestDTO.setModel(inStockSaveModel.build(FNumber));
        return KdRequestUtil.save(FormIdEnum.STK_InStock, kdApiBaseSaveRequestDTO);
    }
    /**
     * 出库
     *
     * @param kdModel
     * @param FNumber
     * @return
     */
    private String outStockSave(KdModel kdModel, String FNumber) {
        OutStockSaveModel outStockSaveModel = (OutStockSaveModel) kdModel;
        KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO = new KdApiBaseSaveRequestDTO();
        kdApiBaseSaveRequestDTO.setModel(outStockSaveModel.build(FNumber));
        return KdRequestUtil.save(FormIdEnum.STK_MisDelivery, kdApiBaseSaveRequestDTO);
    }
}
