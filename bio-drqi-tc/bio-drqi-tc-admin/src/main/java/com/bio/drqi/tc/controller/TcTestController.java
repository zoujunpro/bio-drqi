package com.bio.drqi.tc.controller;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.TcPollinationApplyTb;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.domain.TcSampleTestTb;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
import com.bio.drqi.tc.service.dto.TcPollinationTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
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

    @GetMapping("/cleanExperimentType")
    public ResponseResult<String> cleanExperimentType() {
        tcExperimentTbMapper.selectList(null).forEach(tcExperimentTb -> {
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcExperimentTb.getTaskNum());
            TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
            tcExperimentTaskDTO.setExperimentType(JSONUtil.toList(tcExperimentTb.getExperimentType(),String.class));
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcExperimentTaskDTO));
            bioTaskDtlTbMapper.updateById(bioTaskDtlTb);
        });
        return ResponseResult.getSuccess("ok");
    }

    @GetMapping("/cleanTcPollinationTb")
    @Transactional(rollbackFor = Exception.class)
    public ResponseResult<String> cleanTcPollinationTb() {
        List<TcPollinationApplyTb> tcPollinationApplyTbList = tcPollinationApplyTbMapper.selectSelective(null);
        for (TcPollinationApplyTb tcPollinationApplyTb : tcPollinationApplyTbList) {
            if ("C0004400".equals(tcPollinationApplyTb.getPollinationApplyNum())) {
                continue;
            }
            if ("C0004401".equals(tcPollinationApplyTb.getPollinationApplyNum())) {
                continue;
            }
            String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + tcPollinationApplyTb.getPollinationExcelUrl();
            try {
                ossService.downloadPath(tempFilePath, tcPollinationApplyTb.getPollinationExcelUrl());
            } catch (Exception e) {
                log.error("【任务工单】文件从oss下载失败", e);
                throw new BusinessException("文件处理异常");
            }
            BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(tcPollinationApplyTb.getTaskNum());
            TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
            tcPollinationTbMapper.deleteByPollinationApplyNum(tcPollinationApplyTb.getPollinationApplyNum());
            List<TcPollinationExcelDTO> tcPollinationExcelDTOList = ExcelUtil.readExcel(tempFilePath, TcPollinationExcelDTO.class);
            for (TcPollinationExcelDTO tcPollinationExcelDTO : tcPollinationExcelDTOList) {
                log.info("清洗数据：tcPollinationExcelDTO" + JSONUtil.toJsonStr(tcPollinationExcelDTO));
                TcPollinationTb tcPollinationTb = new TcPollinationTb();
                tcPollinationTb.setExperimentNum(tcPollinationApplyTb.getExperimentNum());
                tcPollinationTb.setSampleApplyNum(tcPollinationApplyTb.getSampleApplyNum());
                tcPollinationTb.setPollinationApplyNum(tcPollinationApplyTb.getTaskNum());
                tcPollinationTb.setMRegionNum(tcPollinationExcelDTO.getMotherRegionNum());
                tcPollinationTb.setFRegionNum(tcPollinationExcelDTO.getFatherRegionNum());
                tcPollinationTb.setMSampleCode(tcPollinationExcelDTO.getMotherSampleCode());
                tcPollinationTb.setFSampleCode(tcPollinationExcelDTO.getFatherSampleCode());
                tcPollinationTb.setMTcSampleCode(tcPollinationExcelDTO.getMotherTcSampleCode());
                tcPollinationTb.setFTcSampleCode(tcPollinationExcelDTO.getFatherTcSampleCode());
                tcPollinationTb.setFSingleNumber(tcPollinationExcelDTO.getFatherSingleNumber());
                tcPollinationTb.setMSingleNumber(tcPollinationExcelDTO.getMotherSingleNumber());
                tcPollinationTb.setMSeedNum(tcPollinationExcelDTO.getMotherSeedNum());
                tcPollinationTb.setFSeedNum(tcPollinationExcelDTO.getFatherSeedNum());
                tcPollinationTb.setFBreedCode(tcPollinationExcelDTO.getFatherBreedCode());
                tcPollinationTb.setMBreedCode(tcPollinationExcelDTO.getMotherBreedCode());
                tcPollinationTb.setMVectorTaskCode(tcPollinationExcelDTO.getMotherVectorTaskCode());
                tcPollinationTb.setFVectorTaskCode(tcPollinationExcelDTO.getFatherVectorTaskCode());
                tcPollinationTb.setMGenerationCode(tcPollinationExcelDTO.getMotherGenerationName());
                tcPollinationTb.setFGenerationCode(tcPollinationExcelDTO.getFatherGenerationName());
                tcPollinationTb.setMTcGene(tcPollinationExcelDTO.getMotherTcGene());
                tcPollinationTb.setFTcGene(tcPollinationExcelDTO.getFatherTcGene());
                tcPollinationTb.setPollinationDate(tcPollinationExcelDTO.getPollinationDate());
                tcPollinationTb.setPollinationMethodCode(tcPollinationApplyTb.getPollinationType());
                tcPollinationTb.setPollinationMethodName(tcPollinationTaskDTO.getPollinationTypeName());
                tcPollinationTb.setHarvestTypeName(tcPollinationExcelDTO.getHarvestTypeName());
                tcPollinationTb.setHarvestTypeCode(tcPollinationExcelDTO.getHarvestTypeCode());
                tcPollinationTb.setRemark(tcPollinationExcelDTO.getRemark());
                tcPollinationTbMapper.insert(tcPollinationTb);
            }


        }
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
