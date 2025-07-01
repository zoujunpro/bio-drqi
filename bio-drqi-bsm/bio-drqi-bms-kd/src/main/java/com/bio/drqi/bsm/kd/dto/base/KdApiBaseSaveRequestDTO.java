package com.bio.drqi.bsm.kd.dto.base;

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


}
