package com.bio.drqi.tc.controller;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.domain.TcSampleTestTb;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("test")
@Slf4j
public class TcTestController {

    @Resource
    private TcSampleTestTbMapper tcSampleTestTbMapper;


    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private TcPollinationApplyTbMapper tcPollinationApplyTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private OssService ossService;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;


    /**
     *
     * @param httpServletResponse
     */
    @GetMapping("excelExcel")
    public void excelExcel(HttpServletResponse httpServletResponse) {
        List<SeedAndTcExcel> seedAndTcExcelList = new ArrayList<>();
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectList(null);
        tcSampleTestTbList = tcSampleTestTbList.stream().filter(tcSampleTestTb -> tcSampleTestTb.getSampleCode().contains("TAR") || tcSampleTestTb.getSampleCode().contains("TAQ")).collect(Collectors.toList());
        for (TcSampleTestTb tcSampleTestTb : tcSampleTestTbList) {
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(tcSampleTestTb.getSeedNum());
            SeedAndTcExcel seedAndTcExcel = new SeedAndTcExcel();
            seedAndTcExcel.setExperimentNum(tcSampleTestTb.getExperimentNum());
            seedAndTcExcel.setSeedNum(tcSampleTestTb.getSeedNum());
            seedAndTcExcel.setSampleCode(tcSampleTestTb.getSampleCode());
            seedAndTcExcel.setPlantCode(seedStockTb.getPlantCode());
            seedAndTcExcel.setAliasName(seedStockTb.getAliasName());
            seedAndTcExcel.setRemark(seedStockTb.getRemarks());
            if (StringUtils.isNotEmpty(seedStockTb.getMatherSeedNum())) {
                SeedStockTb matherSeedStockTb = seedStockTbMapper.selectOneBySeedNum(seedStockTb.getMatherSeedNum());
                seedAndTcExcel.setMatherSeedNum(matherSeedStockTb.getSeedNum());
                seedAndTcExcel.setMatherPlantCode(matherSeedStockTb.getPlantCode());
                seedAndTcExcel.setMatherRemark(matherSeedStockTb.getRemarks());
                seedAndTcExcel.setMatherAliasName(matherSeedStockTb.getAliasName());
            }

            if (StringUtils.isNotEmpty(seedStockTb.getFatherSingleNum())) {
                SeedStockTb fatherSeedStockTb = seedStockTbMapper.selectOneBySeedNum(seedStockTb.getFatherSeedNum());
                seedAndTcExcel.setFatherSeedNum(fatherSeedStockTb.getSeedNum());
                seedAndTcExcel.setFatherPlantCode(fatherSeedStockTb.getPlantCode());
                seedAndTcExcel.setFatherRemark(fatherSeedStockTb.getRemarks());
                seedAndTcExcel.setFatherAliasName(fatherSeedStockTb.getAliasName());
            }
            seedAndTcExcelList.add(seedAndTcExcel);
        }
        ExcelUtil.writeExcel("种植编号", "sheet1", seedAndTcExcelList, SeedAndTcExcel.class, httpServletResponse);
    }

    @Data
    public static class SeedAndTcExcel {

        @ExcelProperty("试验编号")
        private String experimentNum;

        @ExcelProperty("种子编号")
        private String seedNum;

        @ExcelProperty("取样编号")
        private String sampleCode;

        @ExcelProperty("种植编号")
        private String plantCode;

        @ExcelProperty("别名")
        private String aliasName;

        @ExcelProperty("备注")
        private String remark;

        @ExcelProperty("母本种子编号")
        private String matherSeedNum;

        @ExcelProperty("母本种植编号")
        private String matherPlantCode;

        @ExcelProperty("母本备注")
        private String matherRemark;

        @ExcelProperty("母本别名")
        private String matherAliasName;


        @ExcelProperty("父本种子编号")
        private String fatherSeedNum;

        @ExcelProperty("父本种植编号")
        private String fatherPlantCode;

        @ExcelProperty("父本备注")
        private String fatherRemark;

        @ExcelProperty("父本别名")
        private String fatherAliasName;


    }

    @GetMapping("/cleanExperimentType")
    public ResponseResult<String> cleanExperimentType() {
        tcExperimentTbMapper.selectList(null).forEach(tcExperimentTb -> {
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcExperimentTb.getTaskNum());
            TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
            tcExperimentTaskDTO.setExperimentType(JSONUtil.toList(tcExperimentTb.getExperimentType(), String.class));
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcExperimentTaskDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        });
        return ResponseResult.getSuccess("ok");
    }


    @GetMapping("/cleanSample")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSample() {
        List<TcSampleTestTb> tcSampleTestTbList = tcSampleTestTbMapper.selectAllByExperimentNum("C0002636");
        Map<String, List<TcSampleTestTb>> map = tcSampleTestTbList.stream().collect(Collectors.groupingBy(TcSampleTestTb::getRegionNum));
        map.forEach((reginCode, list) -> {
            list = list.stream().sorted(Comparator.comparing(TcSampleTestTb::getId)).collect(Collectors.toList());
            for (int i = 0; i < list.size(); i++) {
                TcSampleTestTb tcSampleTestTb = list.get(i);
                tcSampleTestTb.setTcSampleCode(reginCode + StringUtils.padl(String.valueOf(i + 1), 3, '0'));
                tcSampleTestTbMapper.updateById(tcSampleTestTb);
            }
        });
        return ResponseResult.getSuccess("OK");

    }

}
