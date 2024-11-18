package com.bio.cer.seedtask;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

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
