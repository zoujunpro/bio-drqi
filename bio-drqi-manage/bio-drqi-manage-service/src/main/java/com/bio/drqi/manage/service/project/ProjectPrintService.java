package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.base.PrintRspDTO;
import com.bio.drqi.manage.projectPrint.*;

public interface ProjectPrintService {

    PrintRspDTO vectorBuildPrint(VectorBuildPrintReqDTO vectorBuildPrintReqDTO);

    PrintRspDTO transFormPrint(TransFormPrintReqDTO transFormPrintReqDTO);


    PrintRspDTO samplePrint(SamplePrintReqDTO samplePrintReqDTO);

    PrintRspDTO layoutPrint( String layoutNumber);

    PrintRspDTO plantPrint(PlantPrintReqDTO plantPrintReqDTO);

    PrintRspDTO transPrint( TransPrintReqDTO transPrintReqDTO);

    PrintRspDTO tissueEmbryoPrint(TissueEmbryoPrintReqDTO transPrintReqDTO);
}
