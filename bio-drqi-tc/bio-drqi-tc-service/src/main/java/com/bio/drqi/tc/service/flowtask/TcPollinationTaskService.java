package com.bio.drqi.tc.service.flowtask;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.BioDict;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.TcPollinationApplyTb;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.enums.BioDictTypeEnum;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.BioDictMapper;
import com.bio.drqi.mapper.TcPollinationApplyTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
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
    private BioDictMapper bioDictMapper;


    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
        ValidatorUtil.validator(tcPollinationTaskDTO);
        BeanUtils.trimFiledSpace(tcPollinationTaskDTO);

        BioDict bioDict = bioDictMapper.selectOneByDictTypeAndDictValueCode(BioDictTypeEnum.POLLINATE_TYPE.name(), tcPollinationTaskDTO.getPollinationType());
        if (bioDict == null) {
            throw new BusinessException("授粉方式错误");
        }
        tcPollinationTaskDTO.setPollinationName(bioDict.getDictValueName());
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
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

            List<TcPollinationTb> tcPollinationTbList = new ArrayList<>();
            List<TcPollinationExcelDTO> tcPollinationExcelDTOList = ExcelUtil.readExcel(tempFilePath, TcPollinationExcelDTO.class);
            for (TcPollinationExcelDTO tcPollinationExcelDTO : tcPollinationExcelDTOList) {
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
                tcPollinationTb.setPollinationMethodName(tcPollinationTaskDTO.getPollinationName());
                tcPollinationTb.setHarvestTypeName(tcPollinationExcelDTO.getHarvestTypeName());
                BioDict bioDict = bioDictMapper.selectOneByDictTypeAndDictValueName(BioDictTypeEnum.HARVEST_TYPE.name(), tcPollinationExcelDTO.getHarvestTypeName());
                if(bioDict==null){
                    throw new BusinessException("收获方式填写错误："+tcPollinationExcelDTO.getHarvestTypeName());
                }
                tcPollinationTb.setHarvestTypeCode(bioDict.getDictValueCode());
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
