package com.bio.drqi.bsm.kd.service;

import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.bsm.kd.dto.KdApiBaseDisableRequestDTO;
import com.bio.drqi.bsm.kd.dto.model.*;
import com.bio.drqi.bsm.kd.dto.KdApiBaseSaveRequestDTO;
import com.bio.drqi.bsm.kd.dto.model.KdModel;
import com.bio.drqi.bsm.kd.enums.FormIdEnum;
import com.bio.drqi.bsm.kd.enums.OperateEnum;
import com.bio.drqi.bsm.kd.enums.OrgEnum;
import com.bio.drqi.bsm.kd.util.KdRequestUtil;
import com.bio.drqi.domain.BmsBrandTb;
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
                return executeBmsModify(obj, unitCode);
            case bmsDisable:
                return executeBmsDisable(obj);
            case materialSave:
                return executeMaterialSave(obj, unitCode);
            case materialModify:
                return executeMaterialModify(obj, unitCode);
            case materialDisable:
                return executeMaterialDisable(obj, unitCode);
            default:
                throw new BusinessException("数据异常，请检查金蝶配置");
        }
    }


    private String executeBmsSave(Object obj, String unitCode){
        BmsBrandTb bmsBrandTb = (BmsBrandTb) obj;
        BrandSaveKdModel brandSaveKdModel=new BrandSaveKdModel();
        brandSaveKdModel.setFID(0);
        brandSaveKdModel.setFnumber(bmsBrandTb.getBrandCode());
        brandSaveKdModel.setFname(bmsBrandTb.getBrandName());
        return KdRequestUtil.save(FormIdEnum.CMK_BD_Brand, KdApiBaseSaveRequestDTO.buildOfSave(brandSaveKdModel, OrgEnum.getOrgByActiveAndUnitCode(active,unitCode)));
    }
    private String executeBmsModify(Object obj, String unitCode){
        BmsBrandTb bmsBrandTb = (BmsBrandTb) obj;
        BrandSaveKdModel brandSaveKdModel=new BrandSaveKdModel();
        brandSaveKdModel.setFID(bmsBrandTb.getKdNumber());
        brandSaveKdModel.setFnumber(bmsBrandTb.getBrandCode());
        brandSaveKdModel.setFname(bmsBrandTb.getBrandName());
        return KdRequestUtil.save(FormIdEnum.CMK_BD_Brand, KdApiBaseSaveRequestDTO.buildOfSave(brandSaveKdModel, OrgEnum.getOrgByActiveAndUnitCode(active,unitCode)));
    }
    private String executeBmsDisable(Object obj){
        BmsBrandTb bmsBrandTb = (BmsBrandTb) obj;
        KdApiBaseDisableRequestDTO kdApiBaseDisableRequestDTO=new KdApiBaseDisableRequestDTO();
        kdApiBaseDisableRequestDTO.setIds(bmsBrandTb.getId());
        return KdRequestUtil.disable(FormIdEnum.CMK_BD_Brand,kdApiBaseDisableRequestDTO);
    }
    private String executeMaterialSave(Object obj, String unitCode){
        return null;
    }
    private String executeMaterialModify(Object obj, String unitCode){
        return null;
    }
    private String executeMaterialDisable(Object obj, String unitCode){
        return null;
    }


}
