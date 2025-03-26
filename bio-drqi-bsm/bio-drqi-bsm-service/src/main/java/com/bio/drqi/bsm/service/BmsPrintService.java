package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsPrintProductLabelReqDTO;
import com.bio.drqi.manage.base.PrintRspDTO;

public interface BmsPrintService {

    PrintRspDTO productLabel( BmsPrintProductLabelReqDTO bmsPrintProductLabelReqDTO);
}
