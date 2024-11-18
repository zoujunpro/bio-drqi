package com.bio.drqi.manage.service.project;

import com.bio.cer.base.PrintRspDTO;
import com.bio.cer.projectPrint.SamplePrintReqDTO;
import com.bio.cer.projectPrint.TransFormPrintReqDTO;
import com.bio.cer.projectPrint.VectorBuildPrintReqDTO;

public interface ProjectPrintService {

    PrintRspDTO vectorBuildPrint(VectorBuildPrintReqDTO vectorBuildPrintReqDTO);

    PrintRspDTO transFormPrint(TransFormPrintReqDTO transFormPrintReqDTO);


    PrintRspDTO samplePrint(SamplePrintReqDTO samplePrintReqDTO);

    PrintRspDTO layoutPrint( String layoutNumber);
}
