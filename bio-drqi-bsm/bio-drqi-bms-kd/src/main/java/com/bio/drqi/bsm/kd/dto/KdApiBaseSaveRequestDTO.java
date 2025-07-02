package com.bio.drqi.bsm.kd.dto;

import com.bio.drqi.bsm.kd.dto.model.KdModel;
import lombok.Data;

@Data
public class KdApiBaseSaveRequestDTO<T extends KdModel> {
    private Object[] NeedUpDateFields;
    private Object[] NeedReturnFields;
    private String IsDeleteEntry = "true";
    private String SubSystemId;
    private String IsVerifyBaseDataFiel = "false";
    private String IsEntryBatchFil = "true";
    private String ValidateFlag = "true";
    private String NumberSearch = "true";
    private String IsAutoAdjustField = "false";
    private String IsAutoSubmitAndAudit = "true";
    private String InterationFlags;
    private String IgnoreInterationFlag;
    private T Model;


    public static KdApiBaseSaveRequestDTO buildOfSave( KdModel kdModel,String FNumber ) {
        KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO = new KdApiBaseSaveRequestDTO();
        kdApiBaseSaveRequestDTO.setNeedUpDateFields(null);
        kdApiBaseSaveRequestDTO.setNeedReturnFields(null);
        kdApiBaseSaveRequestDTO.setIsDeleteEntry("true");
        kdApiBaseSaveRequestDTO.setSubSystemId(null);
        kdApiBaseSaveRequestDTO.setIsVerifyBaseDataFiel("false");
        kdApiBaseSaveRequestDTO.setIsEntryBatchFil("true");
        kdApiBaseSaveRequestDTO.setValidateFlag("true");
        kdApiBaseSaveRequestDTO.setNumberSearch("true");
        kdApiBaseSaveRequestDTO.setIsAutoAdjustField("false");
        kdApiBaseSaveRequestDTO.setIsAutoSubmitAndAudit("true");
        kdApiBaseSaveRequestDTO.setInterationFlags(null);
        kdApiBaseSaveRequestDTO.setIgnoreInterationFlag(null);
        kdApiBaseSaveRequestDTO.setModel(kdModel.build(FNumber));
        return kdApiBaseSaveRequestDTO;
    }


}
