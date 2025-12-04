package com.bio.drqi.applet.service;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.applet.dto.req.FindPlantFieldReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingRemainReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingRemoveReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingReportReqDTO;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;
import java.util.Map;

public interface SeedlingService {



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

    List<Map<String, String>> findPlantField(FindPlantFieldReqDTO findPlantFieldReqDTO);
}
