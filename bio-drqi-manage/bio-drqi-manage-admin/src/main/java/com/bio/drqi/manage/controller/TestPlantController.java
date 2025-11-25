package com.bio.drqi.manage.controller;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.excel.annotation.ExcelProperty;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.Jar;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

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


    @GetMapping("cleanSeedAndTc")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanSeedAndTc() {
        List<CleanSeedAndTcExcelDTO> cleanSeedAndTcExcelDTOList = ExcelUtil.readExcel("C:\\Users\\zou'jun\\Desktop\\上线\\要清洗数据.xlsx", CleanSeedAndTcExcelDTO.class);

        for (CleanSeedAndTcExcelDTO cleanSeedAndTcExcelDTO : cleanSeedAndTcExcelDTOList) {
            log.info("cleanSeedAndTcExcelDTO=" + JSONUtil.toJsonStr(cleanSeedAndTcExcelDTO));
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(cleanSeedAndTcExcelDTO.seedNum);
            if (seedStockTb == null) {
                throw new BusinessException("种子库找不到种子编号");
            }
            List<TcExperimentDesignTb> tcExperimentDesignTbList = tcExperimentDesignTbMapper.selectAllBySeedNum(seedStockTb.getSeedNum());
            if (CollectionUtil.isEmpty(tcExperimentDesignTbList)) {
                throw new BusinessException("大田找不到种子信息");
            }
            if (StringUtils.isEmpty(cleanSeedAndTcExcelDTO.getVectorTaskCode())) {
                seedStockTbMapper.updatePdNumAndVectorTaskCodeAndProjectCodeById(cleanSeedAndTcExcelDTO.pdNum, cleanSeedAndTcExcelDTO.vectorTaskCode, null, seedStockTb.getId());
            } else {
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(cleanSeedAndTcExcelDTO.vectorTaskCode);
                if (cerVectorTaskTb == null) {
                    throw new BusinessException("找不到此实施方案编号");
                }
                seedStockTbMapper.updatePdNumAndVectorTaskCodeAndProjectCodeById(cleanSeedAndTcExcelDTO.pdNum, cleanSeedAndTcExcelDTO.vectorTaskCode, cerVectorTaskTb.getProjectCode(), seedStockTb.getId());
            }

            tcExperimentDesignTbList.forEach(tcExperimentDesignTb -> {
                tcExperimentDesignTbMapper.updatePdNumAndVectorTaskCodeById(cleanSeedAndTcExcelDTO.pdNum, cleanSeedAndTcExcelDTO.vectorTaskCode, tcExperimentDesignTb.getId());
            });


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
