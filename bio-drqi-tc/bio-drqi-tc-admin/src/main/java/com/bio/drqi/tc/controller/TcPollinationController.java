package com.bio.drqi.tc.controller;


import com.bio.common.core.dto.ResponseResult;
import com.bio.common.web.aspect.WebLog;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 授粉管理
 */
@RestController
@RequestMapping("/tcPollination")
public class TcPollinationController {

    /**
     * 授粉管理-分页查询
     * @return
     */
    @PostMapping("/listPage")
    @WebLog(desc = "授粉管理-分页查询")
    public ResponseResult listPage(){
        return null;
    }


    /**
     * 授粉管理-生成授粉excel
     */
    @PostMapping("/createPollinationExcel")
    @WebLog(desc = "授粉管理-生成授粉excel")
    public void createPollinationExcel(){

    }

}
