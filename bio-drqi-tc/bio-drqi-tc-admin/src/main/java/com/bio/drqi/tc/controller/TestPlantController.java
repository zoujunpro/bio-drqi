package com.bio.drqi.tc.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.Jar;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/test4")
public class TestPlantController {

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;


    @GetMapping("cleanSeedAndTc")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSeedAndTc() {


        List<TcExperimentTb> tcExperimentTbList = tcExperimentTbMapper.selectList(null);
        for (TcExperimentTb tcExperimentTb : tcExperimentTbList) {
            log.info("处理试验工单tcExperimentTb=" + JSONUtil.toJsonStr(tcExperimentTb));
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcExperimentTb.getTaskNum());
            if (bioTaskDtlTb == null) {
                throw new BusinessException("找不到申请工单");
            }
            TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
            tcExperimentTaskDTO.setPdImplementCodeList(JSONUtil.toList(tcExperimentTb.getPdImplementCodes(),String.class));
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcExperimentTaskDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        }


        return ResponseResult.getSuccess("pok");
    }

    public static class CleanSeedAndTcExcelDTO {

        @ExcelProperty("种子编号")
        private String seedNum;

        @ExcelProperty("PD号")
        private String pdNum;

        @ExcelProperty("实施方案编号")
        private String vectorTaskCode;

        public String getSeedNum() {
            return seedNum;
        }

        public void setSeedNum(String seedNum) {
            this.seedNum = seedNum;
        }

        public String getPdNum() {
            return StringUtils.isEmpty(pdNum) ? null : pdNum;
        }

        public void setPdNum(String pdNum) {
            this.pdNum = pdNum;
        }

        public String getVectorTaskCode() {
            return StringUtils.isEmpty(vectorTaskCode) ? null : vectorTaskCode;
        }

        public void setVectorTaskCode(String vectorTaskCode) {
            this.vectorTaskCode = vectorTaskCode;
        }
    }
}
