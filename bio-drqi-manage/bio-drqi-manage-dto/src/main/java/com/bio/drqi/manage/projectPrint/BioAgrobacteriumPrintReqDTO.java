package com.bio.drqi.manage.projectPrint;

import lombok.Data;

import java.util.List;

@Data
public class BioAgrobacteriumPrintReqDTO {
    private List<String> plasmidNames;
    private String AgrobacteriumName;
    private String AgrobacteriumResistance;
    private String makingDate;
}
