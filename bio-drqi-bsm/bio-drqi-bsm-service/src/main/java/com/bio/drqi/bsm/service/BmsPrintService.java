package com.bio.drqi.bsm.service;

import com.bio.drqi.bsm.req.BmsPrintProductLabelReqDTO;
import com.bio.drqi.manage.base.PrintRspDTO;

import java.util.List;

public interface BmsPrintService {

    List<PrintRspDTO> productLabel(BmsPrintProductLabelReqDTO bmsPrintProductLabelReqDTO);
}
