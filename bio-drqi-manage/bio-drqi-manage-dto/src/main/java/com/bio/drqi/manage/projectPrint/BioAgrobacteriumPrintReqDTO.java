package com.bio.drqi.manage.projectPrint;

import lombok.Data;

import java.util.List;

@Data
public class BioAgrobacteriumPrintReqDTO {
    private List<String> plasmidNames;
    private String agrobacteriumName;
    private String agrobacteriumResistance;
    private String makingDate;
}
