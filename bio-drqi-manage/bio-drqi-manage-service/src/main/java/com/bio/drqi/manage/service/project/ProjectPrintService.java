package com.bio.drqi.manage.service.project;

import com.bio.drqi.manage.base.PrintRspDTO;
import com.bio.drqi.manage.projectPrint.*;

import java.util.List;

public interface ProjectPrintService {

    List<PrintRspDTO> vectorBuildPrint(VectorBuildPrintReqDTO vectorBuildPrintReqDTO);

    List<PrintRspDTO> transFormPrint(TransFormPrintReqDTO transFormPrintReqDTO);


    List<PrintRspDTO> samplePrint(SamplePrintReqDTO samplePrintReqDTO);

    List<PrintRspDTO> layoutPrint(String layoutNumber);

    List<PrintRspDTO> plantPrint(PlantPrintReqDTO plantPrintReqDTO);

    List<PrintRspDTO> transPrint(TransPrintReqDTO transPrintReqDTO);

    List<PrintRspDTO> tissueEmbryoPrint(TissueEmbryoPrintReqDTO transPrintReqDTO);

    List<PrintRspDTO> plantApplyPrint(BioPrintPlantApplyReqDTO bioPrintPlantApplyReqDTO);

    List<PrintRspDTO> tcExperimentPrint(BioPrintTcExperimentReqDTO bioPrintTcExperimentReqDTO);
}
