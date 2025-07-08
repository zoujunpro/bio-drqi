package com.bio.drqi.bsm.kd.dto;

import com.bio.drqi.bsm.kd.dto.model.KdModel;
import lombok.Data;

import java.util.Arrays;
import java.util.List;

@Data
public class KdApiBaseSaveRequestDTO<T extends KdModel> {
    private List<String> NeedUpDateFields;
    private List<String> NeedReturnFields;
    private String IsDeleteEntry ;
    private String SubSystemId;
    private String IsVerifyBaseDataFiel ;
    private String IsEntryBatchFil ;
    private String ValidateFlag ;
    private String NumberSearch ;
    private String IsAutoAdjustField ;
    private String IsAutoSubmitAndAudit ;
    private String InterationFlags;
    private String IgnoreInterationFlag;
    private T Model;


    public static KdApiBaseSaveRequestDTO buildOfSave(KdModel kdModel, String FNumber ) {
        KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO = new KdApiBaseSaveRequestDTO();
        kdApiBaseSaveRequestDTO.setNeedUpDateFields(null);
        kdApiBaseSaveRequestDTO.setNeedReturnFields(Arrays.asList("ID"));
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
    public static KdApiBaseSaveRequestDTO buildOfModify(KdModel kdModel ) {
        KdApiBaseSaveRequestDTO kdApiBaseSaveRequestDTO = new KdApiBaseSaveRequestDTO();
        kdApiBaseSaveRequestDTO.setModel(kdModel);
        kdApiBaseSaveRequestDTO.setNeedUpDateFields(kdModel.buildModifyFields());
        return kdApiBaseSaveRequestDTO;
    }


}
