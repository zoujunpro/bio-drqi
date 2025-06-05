package com.bio.drqi.tc.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.SampleGroupPergixEnum;
import com.bio.drqi.common.util.LetterUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.TcExperimentDesignTb;
import com.bio.drqi.domain.TcExperimentTb;
import com.bio.drqi.mapper.TcExperimentDesignTbMapper;
import com.bio.drqi.mapper.TcExperimentTbMapper;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.service.dto.ExperimentDesignExcelDTO;
import com.bio.drqi.tc.service.dto.TcExperimentTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("tc_experiment_task_apply")
@Slf4j
public class TcExperimentTaskService extends AbstractTcBaseTaskService {


    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;


    @Resource
    private OssService ossService;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
        ValidatorUtil.validator(tcExperimentTaskDTO);
        if (StringUtils.isNotEmpty(tcExperimentTaskDTO.getExperimentDesignUrl())) {
            validatorExcel(tcExperimentTaskDTO);
        }
    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcExperimentTaskDTO tcExperimentTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcExperimentTaskDTO.class);
        List<ExperimentDesignExcelDTO> experimentDesignExcelDTOList = null;
        if (StringUtils.isNotEmpty(tcExperimentTaskDTO.getExperimentDesignUrl())) {
            experimentDesignExcelDTOList = validatorExcel(tcExperimentTaskDTO);
        }

        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {

            if (CollectionUtil.isEmpty(experimentDesignExcelDTOList)) {
                throw new BusinessException("大田试验田间设计缺失");
            }

            TcExperimentTb tcExperimentTb = new TcExperimentTb();
            tcExperimentTb.setProjectCodes(JSONUtil.toJsonStr(tcExperimentTaskDTO.getProjectCodeList()));
            tcExperimentTb.setVectorTaskCodes(JSONUtil.toJsonStr(tcExperimentTaskDTO.getVectorTaskCodeList()));
            tcExperimentTb.setSpeciesCode(tcExperimentTaskDTO.getSpeciesCode());
            tcExperimentTb.setSpeciesName(tcExperimentTaskDTO.getSpeciesName());
            tcExperimentTb.setFileUrl(tcExperimentTaskDTO.getFileUrl());
            tcExperimentTb.setExperimentGoal(tcExperimentTaskDTO.getExperimentGoal());
            tcExperimentTb.setExperimentAddress(tcExperimentTaskDTO.getExperimentAddress());
            tcExperimentTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
            tcExperimentTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
            tcExperimentTb.setCreateTime(new Date());
            tcExperimentTb.setExperimentNum(bioTaskDtlTb.getTaskNum());
            tcExperimentTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            tcExperimentTb.setDesignUrl(tcExperimentTaskDTO.getExperimentDesignUrl());
            tcExperimentTb.setSampleCodePrefix(createSampleCode());
            tcExperimentTb.setNextSampleNumber(1);
            tcExperimentTb.setExperimentStatus(ExperimentStatusEnum.INIT.status);
            tcExperimentTbMapper.insert(tcExperimentTb);

            List<TcExperimentDesignTb> tcExperimentDesignTbList = new ArrayList<TcExperimentDesignTb>();
            for (ExperimentDesignExcelDTO experimentDesignExcelDTO : experimentDesignExcelDTOList) {
                TcExperimentDesignTb tcExperimentDesignTb = new TcExperimentDesignTb();
                tcExperimentDesignTb.setExperimentNum(tcExperimentTb.getExperimentNum());
                tcExperimentDesignTb.setRegionNum(experimentDesignExcelDTO.getRegionNum());
                tcExperimentDesignTb.setSeedNum(experimentDesignExcelDTO.getSeedNum());
                tcExperimentDesignTb.setProjectCode(experimentDesignExcelDTO.getProjectCode());
                tcExperimentDesignTb.setVectorTaskCode(experimentDesignExcelDTO.getVectorTaskCode());
                tcExperimentDesignTb.setSpeciesCode(tcExperimentTb.getSpeciesCode());
                tcExperimentDesignTb.setBreedName(experimentDesignExcelDTO.getBreedName());
                tcExperimentDesignTb.setTargetCharacter(experimentDesignExcelDTO.getTargetCharacter());
                tcExperimentDesignTb.setGenerationCode(experimentDesignExcelDTO.getGenerationCode());
                tcExperimentDesignTb.setTcGene(experimentDesignExcelDTO.getTcGene());
                tcExperimentDesignTb.setRegionArea(experimentDesignExcelDTO.getRegionArea());
                tcExperimentDesignTb.setAreaUnit(experimentDesignExcelDTO.getAreaUnit());
                tcExperimentDesignTb.setPlantSpace(experimentDesignExcelDTO.getPlantSpace());
                tcExperimentDesignTb.setRowsNumber(experimentDesignExcelDTO.getRowsNumber());
                tcExperimentDesignTb.setRowsLength(experimentDesignExcelDTO.getRowsLength());
                tcExperimentDesignTb.setRowsSpace(experimentDesignExcelDTO.getRowsSpace());
                tcExperimentDesignTb.setSeedingType(experimentDesignExcelDTO.getSeedingType());
                tcExperimentDesignTb.setSeedingNumber(experimentDesignExcelDTO.getSeedingNumber());
                tcExperimentDesignTb.setSeedingUnit(experimentDesignExcelDTO.getSeedingUnit());
                tcExperimentDesignTb.setSeedingTime(experimentDesignExcelDTO.getSeedingTime());
                tcExperimentDesignTb.setRemark(experimentDesignExcelDTO.getRemark());
                tcExperimentDesignTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                tcExperimentDesignTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
                tcExperimentDesignTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
                tcExperimentDesignTb.setCreateTime(new Date());
                tcExperimentDesignTb.setEmergenceRate(experimentDesignExcelDTO.getEmergenceRate());
                tcExperimentDesignTb.setTransplantTime(experimentDesignExcelDTO.getTransplantTime());
                tcExperimentDesignTbList.add(tcExperimentDesignTb);
            }
            tcExperimentDesignTbMapper.insertBatch(tcExperimentDesignTbList);
            tcExperimentTaskDTO.setSampleCodePrefix(tcExperimentTb.getSampleCodePrefix());
            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcExperimentTaskDTO));
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }


    private List<ExperimentDesignExcelDTO> validatorExcel(TcExperimentTaskDTO tcExperimentTaskDTO) {
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + tcExperimentTaskDTO.getExperimentDesignUrl();
        try {
            ossService.downloadPath(tempFilePath, tcExperimentTaskDTO.getExperimentDesignUrl());
        } catch (Exception e) {
            log.error("【大田试验田间设计】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<ExperimentDesignExcelDTO> experimentDesignExcelDTOList = ExcelUtil.readExcel(tempFilePath, ExperimentDesignExcelDTO.class);
        if (CollectionUtil.isEmpty(experimentDesignExcelDTOList)) {
            throw new BusinessException("大田试验田间设计没有具体内容");
        }
        for (ExperimentDesignExcelDTO experimentDesignExcelDTO : experimentDesignExcelDTOList) {
            ValidatorUtil.validator(experimentDesignExcelDTO);
            if (!tcExperimentTaskDTO.getProjectCodeList().contains(experimentDesignExcelDTO.getProjectCode())) {
                throw new BusinessException("excel大田设计文件中项目不是目标试验项目");
            }
            if (!tcExperimentTaskDTO.getVectorTaskCodeList().contains(experimentDesignExcelDTO.getVectorTaskCode())) {
                throw new BusinessException("excel大田设计文件中实现方案编号不正确，必须归属所选方案中");
            }
            if (!experimentDesignExcelDTO.getVectorTaskCode().startsWith(experimentDesignExcelDTO.getProjectCode())) {
                throw new BusinessException("实施方案:" + experimentDesignExcelDTO.getVectorTaskCode() + "不属于此项目:" + experimentDesignExcelDTO.getProjectCode());
            }
        }
        Map<String, List<ExperimentDesignExcelDTO>> listMap = experimentDesignExcelDTOList.stream().collect(Collectors.groupingBy(ExperimentDesignExcelDTO::getRegionNum));
        listMap.forEach((regionNun,list)->{
            if(list.size()!=list.stream().map(ExperimentDesignExcelDTO::getSeedNum).distinct().collect(Collectors.toList()).size()){
                throw new BusinessException("小区："+regionNun+" 有重复种子编号");
            }
        });



        return experimentDesignExcelDTOList;
    }


    private String createSampleCode() {
        String maxSampleCodePrefix = tcExperimentTbMapper.selectMaxSampleCodePerfix();
        if (StringUtils.isEmpty(maxSampleCodePrefix)) {
            return "AA";
        } else {
            return SampleGroupPergixEnum.T.name() + LetterUtil.nextLetterForInstantVerify(maxSampleCodePrefix.substring(1));
        }
    }
}
