package com.bio.drqi.manage.controller;

import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/test4")
public class TestPlantController {

    public ResponseResult<String> cleanSeedAndTc() {
        List<CleanSeedAndTcExcelDTO> cleanSeedAndTcExcelDTOList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\要清洗数据.xlsx", CleanSeedAndTcExcelDTO.class);

        for (CleanSeedAndTcExcelDTO cleanSeedAndTcExcelDTO:cleanSeedAndTcExcelDTOList){

        }
        return null;
    }

    @Data
    public static class CleanSeedAndTcExcelDTO {

        @ExcelProperty("种子编号")
        private String seedNum;

        @ExcelProperty("PD号")
        private String pdNum;

        @ExcelProperty("实施方案编号")
        private String vectorTaskCode;
    }
}
