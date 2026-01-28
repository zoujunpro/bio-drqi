package com.bio.drqi.bsm.kd.dto.model;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * 物料
 */
@Data
public class ProjectModel extends KdModel {
    /**
     * 实体主键
     */
    private String FEntryID = "0";

    /**
     * 编码
     */
    private String Fnumber;

    /**
     * 名称
     */
    private String FDataValue;

    private FIdModel FId;

    public ProjectModel(String FEntryID,String KdProjectCode, String kdProjectName, String kdProjectType) {
        this.FEntryID = FEntryID;
        Fnumber = KdProjectCode;
        this.FDataValue = kdProjectName;
        this.FId = new FIdModel(kdProjectType);
    }

    @Override
    public List<String> buildModifyFields() {
        List<String> list = new ArrayList<>();
        list.add("FDataValue");
        list.add("Fnumber");
        return list;
    }

    @Data
    private class FIdModel {
        private String FNumber;

        public FIdModel(String FNumber) {
            this.FNumber = FNumber;
        }
    }


}
