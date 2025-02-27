package com.bio.drqi.seedtask;

import lombok.Data;

@Data
public class SeedTaskSeedNumRspDTO {
    private String seedNum;
    private String unit;
    private Integer id;
    private String number;

    public SeedTaskSeedNumRspDTO(String seedNum,String unit,Integer id,String number) {
        this.seedNum = seedNum;
        this.unit=unit;
        this.id=id;
        this.number=number;
    }
}
