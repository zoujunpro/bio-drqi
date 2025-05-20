package com.bio.drqi.tc.controller;

import com.alibaba.excel.EasyExcel;
import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.protocol.HTTP;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("test")
@Slf4j
public class TcTestController {


    @GetMapping("/createExcel")
    public void createExcel(HttpServletResponse httpServletResponse){

    }

}
