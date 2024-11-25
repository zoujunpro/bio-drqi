package com.bio.drqi.applet.service;


import com.bio.drqi.applet.dto.rsp.ScanCodeRspDTO;

public interface ScanCodeService {

    ScanCodeRspDTO scanCode(String code);

}
