package com.bio.drqi.bsm.kd.dto;

import lombok.Data;

@Data
public class GroupSaveDTO {
    private String GroupFieldKey;
    private String GroupPkId;
    private String FParentId;
    private String FNumber;
    private String FName;
    private String FDescription;
}
