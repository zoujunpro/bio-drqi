package com.bio.drqi.bsm.kd.service;

import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.bsm.kd.dto.KdApiBaseDisableRequestDTO;
import com.bio.drqi.bsm.kd.dto.model.*;
import com.bio.drqi.bsm.kd.dto.KdApiBaseSaveRequestDTO;
import com.bio.drqi.bsm.kd.enums.FormIdEnum;
import com.bio.drqi.bsm.kd.enums.OperateEnum;
import com.bio.drqi.bsm.kd.enums.OrgEnum;
import com.bio.drqi.bsm.kd.util.KdRequestUtil;
import com.bio.drqi.domain.BmsBrandTb;
import com.bio.drqi.domain.BmsProjectDict;
import com.bio.drqi.domain.BmsStockLocationDict;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KdApiServiceImpl implements KdApiService {

    @Value("${spring.profiles.active}")
    private String active;

    @Override
    public String execute(OperateEnum operateEnum, Object obj, String unitCode) {
        switch (operateEnum) {
            case bmsSave:
                return executeBmsSave(obj, unitCode);
            case bmsModify:
                return executeBmsModify(obj);
            case bmsDisable:
                return executeBmsDisable(obj);
            case projectSave:
                return executeProjectSave(obj, unitCode);
            case projectModify:
                return executeProjectModify(obj);
            case projectDisable:
                return executeProjectDisable(obj);
            case fixValueSave:
                return executeStockSave(obj,unitCode);
            case fixValueModify:
                return  executeStockModify(obj);
            case fixValueDisable:
                return  executeStockDisable(obj);
            case materialSave:
                return executeMaterialSave(obj, unitCode);
            case materialModify:
                return executeMaterialModify(obj);
            case materialDisable:
                return executeMaterialDisable(obj, unitCode);
            default:
                throw new BusinessException("数据异常，请检查金蝶配置");
        }
    }


    /**
     * 保存品牌
     *
     * @param obj
     * @param unitCode
     * @return
     */
    private String executeBmsSave(Object obj, String unitCode) {
        BmsBrandTb bmsBrandTb = (BmsBrandTb) obj;
        BrandKdModel brandKdModel = new BrandKdModel();
        brandKdModel.setFID(0);
        brandKdModel.setFnumber(bmsBrandTb.getBrandCode());
        brandKdModel.setFname(bmsBrandTb.getBrandName());
        return KdRequestUtil.save(FormIdEnum.CMK_BD_Brand, KdApiBaseSaveRequestDTO.buildOfSave(brandKdModel, OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));
    }

    /**
     * 修改品牌
     *
     * @param obj
     * @return
     */
    private String executeBmsModify(Object obj) {
        BmsBrandTb bmsBrandTb = (BmsBrandTb) obj;
        BrandKdModel brandKdModel = new BrandKdModel();
        brandKdModel.setFID(bmsBrandTb.getKdNumber());
        brandKdModel.setFnumber(bmsBrandTb.getBrandCode());
        brandKdModel.setFname(bmsBrandTb.getBrandName());
        return KdRequestUtil.save(FormIdEnum.CMK_BD_Brand, KdApiBaseSaveRequestDTO.buildOfModify(brandKdModel));
    }

    /**
     * 禁用品牌
     *
     * @param obj
     * @return
     */
    private String executeBmsDisable(Object obj) {
        BmsBrandTb bmsBrandTb = (BmsBrandTb) obj;
        KdApiBaseDisableRequestDTO kdApiBaseDisableRequestDTO = new KdApiBaseDisableRequestDTO();
        kdApiBaseDisableRequestDTO.setIds(bmsBrandTb.getKdNumber());
        return KdRequestUtil.disable(FormIdEnum.CMK_BD_Brand, kdApiBaseDisableRequestDTO);
    }

    /**
     * 执行项目保存
     *
     * @param obj
     * @param unitCode
     * @return
     */
    private String executeProjectSave(Object obj, String unitCode) {
        BmsProjectDict bmsProjectDict = (BmsProjectDict) obj;
        ProjectModel projectModel = new ProjectModel();
        projectModel.setFMATERIALID(0);
        projectModel.setFnumber(bmsProjectDict.getProjectCode());
        projectModel.setFname(bmsProjectDict.getProjectName());
        return KdRequestUtil.save(FormIdEnum.k62a1e2f33daa4a738462728197b95678, KdApiBaseSaveRequestDTO.buildOfSave(projectModel, OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));
    }


    /**
     * 项目修改
     *
     * @param obj
     * @return
     */
    private String executeProjectModify(Object obj) {
        BmsProjectDict bmsProjectDict = (BmsProjectDict) obj;
        ProjectModel projectModel = new ProjectModel();
        projectModel.setFMATERIALID(bmsProjectDict.getKdNumber());
        projectModel.setFnumber(bmsProjectDict.getProjectCode());
        projectModel.setFname(bmsProjectDict.getProjectName());
        return KdRequestUtil.save(FormIdEnum.k62a1e2f33daa4a738462728197b95678, KdApiBaseSaveRequestDTO.buildOfModify(projectModel));
    }

    /**
     * 项目禁用
     *
     * @param obj
     * @return
     */
    private String executeProjectDisable(Object obj) {
        BmsProjectDict bmsProjectDict = (BmsProjectDict) obj;
        KdApiBaseDisableRequestDTO kdApiBaseDisableRequestDTO = new KdApiBaseDisableRequestDTO();
        kdApiBaseDisableRequestDTO.setIds(bmsProjectDict.getKdNumber());
        return KdRequestUtil.disable(FormIdEnum.k62a1e2f33daa4a738462728197b95678, kdApiBaseDisableRequestDTO);
    }


    /**
     * 仓库保存
     * @param obj
     * @param unitCode
     * @return
     */
    private String executeStockSave(Object obj, String unitCode) {
        BmsStockLocationDict bmsStockLocationDict= (BmsStockLocationDict) obj;
        StockModel stockModel =new StockModel();
        stockModel.setFStockId(0);
        stockModel.setFnumber(bmsStockLocationDict.getLocationNumber());
        stockModel.setFname(bmsStockLocationDict.getStockName());
        stockModel.setFStockProperty("1");
        stockModel.setFStockStatusType("0,1,2,3,4,5,6,7,8");
        return KdRequestUtil.save(FormIdEnum.BD_STOCK, KdApiBaseSaveRequestDTO.buildOfSave(stockModel, OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));
    }

    /**
     * 仓库修改
     * @param obj
     * @return
     */
    private String executeStockModify(Object obj){
        BmsStockLocationDict bmsStockLocationDict= (BmsStockLocationDict) obj;
        StockModel stockModel =new StockModel();
        stockModel.setFStockId(bmsStockLocationDict.getKdNumber());
        stockModel.setFnumber(bmsStockLocationDict.getLocationNumber());
        stockModel.setFname(bmsStockLocationDict.getStockName());
        return KdRequestUtil.save(FormIdEnum.BD_STOCK, KdApiBaseSaveRequestDTO.buildOfModify(stockModel));

    }

    /**
     * 仓库禁用
     * @param obj
     * @return
     */
    private String executeStockDisable(Object obj){
        BmsStockLocationDict bmsStockLocationDict= (BmsStockLocationDict) obj;
        KdApiBaseDisableRequestDTO kdApiBaseDisableRequestDTO = new KdApiBaseDisableRequestDTO();
        kdApiBaseDisableRequestDTO.setIds(bmsStockLocationDict.getKdNumber());
        return KdRequestUtil.disable(FormIdEnum.BD_STOCK, kdApiBaseDisableRequestDTO);

    }

    private String executeMaterialSave(Object obj, String unitCode) {
        return null;
    }

    private String executeMaterialModify(Object obj) {
        return null;
    }

    private String executeMaterialDisable(Object obj, String unitCode) {
        return null;
    }


}
