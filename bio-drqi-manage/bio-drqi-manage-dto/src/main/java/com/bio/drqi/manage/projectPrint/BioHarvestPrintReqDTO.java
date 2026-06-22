package com.bio.drqi.manage.projectPrint;

import lombok.Data;

import java.util.List;

@Data
public class BioHarvestPrintReqDTO {

    private List<Integer> idList;

    private String batchNo;
}
