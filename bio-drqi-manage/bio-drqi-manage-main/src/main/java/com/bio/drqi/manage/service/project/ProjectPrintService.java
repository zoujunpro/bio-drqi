package com.bio.drqi.manage.service.project;

import com.bio.drqi.base.PrintRspDTO;
import com.bio.drqi.projectPrint.SamplePrintReqDTO;
import com.bio.drqi.projectPrint.TransFormPrintReqDTO;
import com.bio.drqi.projectPrint.PlantPrintReqDTO;
import com.bio.drqi.projectPrint.VectorBuildPrintReqDTO;

public interface ProjectPrintService {

    PrintRspDTO vectorBuildPrint(VectorBuildPrintReqDTO vectorBuildPrintReqDTO);

    PrintRspDTO transFormPrint(TransFormPrintReqDTO transFormPrintReqDTO);


    PrintRspDTO samplePrint(SamplePrintReqDTO samplePrintReqDTO);

    PrintRspDTO layoutPrint( String layoutNumber);

    PrintRspDTO plantPrint(PlantPrintReqDTO plantPrintReqDTO);
}
