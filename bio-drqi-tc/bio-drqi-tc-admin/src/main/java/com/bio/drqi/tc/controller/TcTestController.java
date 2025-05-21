package com.bio.drqi.tc.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("test")
@Slf4j
public class TcTestController {


    @GetMapping("/createExcel")
    public void createExcel(HttpServletResponse httpServletResponse) {

    }

}
