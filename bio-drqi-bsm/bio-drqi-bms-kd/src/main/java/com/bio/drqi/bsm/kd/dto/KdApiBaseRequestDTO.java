package com.bio.drqi.bsm.kd.dto;

import com.bio.drqi.bsm.kd.dto.model.KdModel;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class KdApiBaseRequestDTO<T extends KdModel> {
    private List<String> NeedUpDateFields;
    private List<String> NeedReturnFields;
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


    public static KdApiBaseRequestDTO buildOfSave(KdModel kdModel, String FNumber ) {
        KdApiBaseRequestDTO kdApiBaseRequestDTO = new KdApiBaseRequestDTO();
        kdApiBaseRequestDTO.setNeedUpDateFields(Arrays.asList("Id"));
        kdApiBaseRequestDTO.setNeedReturnFields(null);
        kdApiBaseRequestDTO.setIsDeleteEntry("true");
        kdApiBaseRequestDTO.setSubSystemId(null);
        kdApiBaseRequestDTO.setIsVerifyBaseDataFiel("false");
        kdApiBaseRequestDTO.setIsEntryBatchFil("true");
        kdApiBaseRequestDTO.setValidateFlag("true");
        kdApiBaseRequestDTO.setNumberSearch("true");
        kdApiBaseRequestDTO.setIsAutoAdjustField("false");
        kdApiBaseRequestDTO.setIsAutoSubmitAndAudit("true");
        kdApiBaseRequestDTO.setInterationFlags(null);
        kdApiBaseRequestDTO.setIgnoreInterationFlag(null);
        kdApiBaseRequestDTO.setModel(kdModel.build(FNumber));
        return kdApiBaseRequestDTO;
    }
    public static KdApiBaseRequestDTO buildOfModify(KdModel kdModel ) {
        KdApiBaseRequestDTO kdApiBaseRequestDTO = new KdApiBaseRequestDTO();
        kdApiBaseRequestDTO.setModel(kdModel);
        kdApiBaseRequestDTO.setNeedUpDateFields(kdModel.buildModifyFields());
        return kdApiBaseRequestDTO;
    }


}
