package com.bio.drqi.manage.service.seed;


import com.bio.drqi.base.PrintRspDTO;
import com.bio.drqi.print.SeedInPrintReqDTO;
import com.bio.drqi.print.SeedOutPrintReqDTO;

import java.util.List;

public interface SeedPrintService {

    List<PrintRspDTO> seedOutLabelPrint(SeedOutPrintReqDTO seedOutPrintReqDTO);

    List<PrintRspDTO> seedInLabelPrint(SeedInPrintReqDTO seedInPrintReqDTO);
}
