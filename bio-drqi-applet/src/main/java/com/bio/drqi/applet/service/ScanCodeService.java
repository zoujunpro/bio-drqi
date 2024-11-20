package com.bio.drqi.applet.service;


import com.bio.drqi.applet.service.parse.dto.PlasmidUniqueCodeDTO;
import com.bio.drqi.applet.service.parse.dto.SampleTestUniqueReqDTO;
import com.bio.drqi.applet.service.parse.dto.TransformUniqueCodeDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodePlasmidRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeSampleTestRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodeTransformRspDTO;

public interface ScanCodeService {

    Object scanCode(String code);

}
