package com.bio.drqi.manage.plant.rsp;


import lombok.Data;

@Data
public class PlantDtlCountRspDTO {
    private Integer totalCountNum;
    private Integer normalCountNum;
    private Integer abnormalCountNum;
    private Integer deleteCountNum;
    private Integer harvestCountNum;

    public PlantDtlCountRspDTO buildTotalCountNum() {
        if (normalCountNum == null) {
            normalCountNum = 0;
        }
        if (abnormalCountNum == null) {
            abnormalCountNum = 0;
        }
        if (deleteCountNum == null) {
            deleteCountNum = 0;
        }
        if (harvestCountNum == null) {
            harvestCountNum = 0;
        }
        this.totalCountNum = normalCountNum + abnormalCountNum + deleteCountNum + harvestCountNum;
        return this;
    }
}
