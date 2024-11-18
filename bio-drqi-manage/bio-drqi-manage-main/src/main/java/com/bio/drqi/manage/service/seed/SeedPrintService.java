package com.bio.drqi.manage.service.seed;


import com.bio.cer.base.PrintRspDTO;
import com.bio.cer.print.SeedInPrintReqDTO;
import com.bio.cer.print.SeedOutPrintReqDTO;

import java.util.List;

public interface SeedPrintService {

    List<PrintRspDTO> seedOutLabelPrint(SeedOutPrintReqDTO seedOutPrintReqDTO);

    List<PrintRspDTO> seedInLabelPrint(SeedInPrintReqDTO seedInPrintReqDTO);
}
