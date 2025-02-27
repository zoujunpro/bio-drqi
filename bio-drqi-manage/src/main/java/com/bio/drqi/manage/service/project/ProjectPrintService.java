package com.bio.drqi.manage.service.project;

import com.bio.drqi.base.PrintRspDTO;
import com.bio.drqi.projectPrint.*;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;

public interface ProjectPrintService {

    PrintRspDTO vectorBuildPrint(VectorBuildPrintReqDTO vectorBuildPrintReqDTO);

    PrintRspDTO transFormPrint(TransFormPrintReqDTO transFormPrintReqDTO);


    PrintRspDTO samplePrint(SamplePrintReqDTO samplePrintReqDTO);

    PrintRspDTO layoutPrint( String layoutNumber);

    PrintRspDTO plantPrint(PlantPrintReqDTO plantPrintReqDTO);

    PrintRspDTO transPrint( TransPrintReqDTO transPrintReqDTO);

    PrintRspDTO tissueEmbryoPrint(TissueEmbryoPrintReqDTO transPrintReqDTO);
}
