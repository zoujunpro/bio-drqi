package com.bio.drqi.applet.service;

import com.bio.drqi.applet.dto.req.SeedlingRemainReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingRemoveReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingReportReqDTO;

public interface SeedlingService {

    /**
     * 保苗
     *
     * @return
     */
    void remain(SeedlingRemainReqDTO seedlingRemainReqDTO);


    /**
     * 剔苗
     *
     * @return
     */
    void remove(SeedlingRemoveReqDTO seedlingRemoveReqDTO);

    /**
     * 苗报备
     *
     * @return
     */
    void report(SeedlingReportReqDTO seedlingReportReqDTO);
}
