package com.bio.drqi.applet.controller;

import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.applet.dto.req.SeedlingRemainReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingRemoveReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingReportReqDTO;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seedling")
public class SeedlingController {


    /**
     *保苗
     * @return
     */
    @PostMapping("remain")
    public ResponseResult<String> remain(@RequestBody SeedlingRemainReqDTO seedlingRemainReqDTO){

        return null;
    }


    /**
     * 剔苗
     * @return
     */
    @PostMapping("remove")
    public ResponseResult<String> remove(@RequestBody SeedlingRemoveReqDTO seedlingRemoveReqDTO){
        return null;
    }

    /**
     * 苗报备
     * @return
     */
    @PostMapping("report")
    public ResponseResult<String> report(@RequestBody SeedlingReportReqDTO seedlingReportReqDTO){
        return null;
    }

}
