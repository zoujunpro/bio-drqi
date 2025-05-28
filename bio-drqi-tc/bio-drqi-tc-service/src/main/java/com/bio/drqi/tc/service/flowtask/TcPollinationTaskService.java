package com.bio.drqi.tc.service.flowtask;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
import com.bio.drqi.tc.service.dto.TcPollinationTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("tc_pollination_task_apply")
@Slf4j
public class TcPollinationTaskService extends AbstractTcBaseTaskService {

    @Resource
    private OssService ossService;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;


    @Resource
    private TcPollinationApplyTbMapper tcPollinationApplyTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private BioDictMapper bioDictMapper;


    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
        ValidatorUtil.validator(tcPollinationTaskDTO);
        BeanUtils.trimFiledSpace(tcPollinationTaskDTO);


        TcPollinationApplyTb tcPollinationApplyTb = tcPollinationApplyTbMapper.selectOneByExperimentNum(tcPollinationTaskDTO.getExperimentNum());
        if (tcPollinationApplyTb != null) {
            throw new BusinessException("该试验已经授粉");
        }

        BioDict bioDict = bioDictMapper.selectOneByDictTypeAndDictValueCode(BioDictTypeEnum.POLLINATE_TYPE.name(), tcPollinationTaskDTO.getPollinationType());
        if (bioDict == null) {
            throw new BusinessException("授粉方式错误");
        }

        if (!tcPollinationTaskDTO.getPollinationExcelUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + tcPollinationTaskDTO.getPollinationExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, tcPollinationTaskDTO.getPollinationExcelUrl());
        } catch (Exception e) {
            log.error("【任务工单】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<TcPollinationExcelDTO> tcPollinationExcelDTOList = ExcelUtil.readExcel(tempFilePath, TcPollinationExcelDTO.class);
        for (TcPollinationExcelDTO tcPollinationExcelDTO : tcPollinationExcelDTOList) {
            ValidatorUtil.validator(tcPollinationTaskDTO);

            TcExperimentDesignTb father = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationTaskDTO.getExperimentNum(), tcPollinationExcelDTO.getFatherRegionNum(), tcPollinationExcelDTO.getFatherSeedNum());
            if (father == null) {
                throw new BusinessException("实验中无此区域为：" + tcPollinationExcelDTO.getFatherRegionNum() + "的种子编号:" + tcPollinationExcelDTO.getFatherSeedNum());
            }
            TcExperimentDesignTb mather = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationTaskDTO.getExperimentNum(), tcPollinationExcelDTO.getMotherRegionNum(), tcPollinationExcelDTO.getMotherSeedNum());
            if (mather == null) {
                throw new BusinessException("实验中无此区域为：" + tcPollinationExcelDTO.getMotherRegionNum() + "的种子编号:" + tcPollinationExcelDTO.getMotherSeedNum());
            }

            BioDict harvestTypeDict = bioDictMapper.selectOneByDictTypeAndDictValueName(BioDictTypeEnum.HARVEST_TYPE.name(), tcPollinationExcelDTO.getHarvestTypeName());
            if (harvestTypeDict == null) {
                throw new BusinessException("收获方式填写错误：" + tcPollinationExcelDTO.getHarvestTypeName());
            }
            tcPollinationExcelDTO.setHarvestTypeCode(harvestTypeDict.getDictValueCode());
        }

        tcPollinationTaskDTO.setTcPollinationExcelDTOList(tcPollinationExcelDTOList);
        tcPollinationTaskDTO.setPollinationTypeName(bioDict.getDictValueName());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcPollinationTaskDTO));
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            TcPollinationApplyTb tcPollinationApplyTb = new TcPollinationApplyTb();
            tcPollinationApplyTb.setExperimentNum(tcPollinationTaskDTO.getExperimentNum());
            tcPollinationApplyTb.setSampleApplyNum(tcPollinationTaskDTO.getSampleApplyNum());
            tcPollinationApplyTb.setPollinationType(tcPollinationTaskDTO.getPollinationType());
            tcPollinationApplyTb.setPollinationApplyNum(bioTaskDtlTb.getTaskNum());
            tcPollinationApplyTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            tcPollinationApplyTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
            tcPollinationApplyTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
            tcPollinationApplyTb.setCreateTime(new Date());
            tcPollinationApplyTb.setHarvestApplyNum(null);
            tcPollinationApplyTbMapper.insert(tcPollinationApplyTb);

            TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcPollinationApplyTb.getExperimentNum());
            tcExperimentTb.setPollinationNum(tcPollinationApplyTb.getPollinationApplyNum());
            tcExperimentTbMapper.updateById(tcExperimentTb);

            List<TcPollinationTb> tcPollinationTbList = new ArrayList<>();
            for (TcPollinationExcelDTO tcPollinationExcelDTO : tcPollinationTaskDTO.getTcPollinationExcelDTOList()) {
                ValidatorUtil.validator(tcPollinationTaskDTO);
                TcPollinationTb tcPollinationTb = new TcPollinationTb();
                tcPollinationTb.setExperimentNum(tcPollinationTaskDTO.getExperimentNum());
                tcPollinationTb.setSampleApplyNum(tcPollinationTaskDTO.getSampleApplyNum());
                tcPollinationTb.setPollinationApplyNum(bioTaskDtlTb.getTaskNum());
                tcPollinationTb.setMRegionNum(tcPollinationExcelDTO.getMotherRegionNum());
                tcPollinationTb.setFRegionNum(tcPollinationExcelDTO.getFatherRegionNum());
                tcPollinationTb.setMSampleCode(tcPollinationExcelDTO.getMotherSampleCode());
                tcPollinationTb.setFSampleCode(tcPollinationExcelDTO.getFatherSampleCode());
                tcPollinationTb.setMSeedNum(tcPollinationExcelDTO.getMotherSeedNum());
                tcPollinationTb.setFSeedNum(tcPollinationExcelDTO.getFatherSeedNum());
                tcPollinationTb.setFBreedName(tcPollinationExcelDTO.getFatherBreedName());
                tcPollinationTb.setMBreedName(tcPollinationExcelDTO.getMotherBreedName());
                tcPollinationTb.setMVectorTaskCode(tcPollinationExcelDTO.getMotherVectorTaskCode());
                tcPollinationTb.setFVectorTaskCode(tcPollinationExcelDTO.getFatherVectorTaskCode());
                tcPollinationTb.setMGenerationCode(tcPollinationExcelDTO.getMotherGenerationName());
                tcPollinationTb.setFGenerationCode(tcPollinationExcelDTO.getFatherGenerationName());
                tcPollinationTb.setMTcGene(tcPollinationExcelDTO.getMotherTcGene());
                tcPollinationTb.setFTcGene(tcPollinationExcelDTO.getFatherTcGene());
                tcPollinationTb.setPollinationDate(tcPollinationExcelDTO.getPollinationDate());
                tcPollinationTb.setPollinationMethodCode(tcPollinationTaskDTO.getPollinationType());
                tcPollinationTb.setPollinationMethodName(tcPollinationTaskDTO.getPollinationTypeName());
                tcPollinationTb.setHarvestTypeName(tcPollinationExcelDTO.getHarvestTypeName());
                tcPollinationTb.setHarvestTypeCode(tcPollinationExcelDTO.getHarvestTypeCode());
                tcPollinationTb.setRemark(tcPollinationExcelDTO.getRemark());
                tcPollinationTbList.add(tcPollinationTb);
            }
            tcPollinationTbMapper.insertBatch(tcPollinationTbList);
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
