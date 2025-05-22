package com.bio.drqi.tc.service.flowtask;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
import com.bio.drqi.tc.service.dto.TcPollinationTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.List;


@Service("tc_pollination_task_apply")
@Slf4j
public class TcPollinationTaskService extends AbstractTcBaseTaskService {

    @Resource
    private OssService ossService;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;




    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
        ValidatorUtil.validator(tcPollinationTaskDTO);
        BeanUtils.trimFiledSpace(tcPollinationTaskDTO);
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

            List<TcPollinationTb> tcPollinationTbList=new ArrayList<>();
            List<TcPollinationExcelDTO> tcPollinationExcelDTOList = ExcelUtil.readExcel(tempFilePath, TcPollinationExcelDTO.class);
            for (TcPollinationExcelDTO tcPollinationExcelDTO : tcPollinationExcelDTOList){
                TcPollinationTb tcPollinationTb=new TcPollinationTb();
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
                tcPollinationTb.setPollinationMethodCode(null);
                tcPollinationTb.setPollinationMethodName(tcPollinationTaskDTO.getPollinationType());
                tcPollinationTb.setHarvestTypeCode(null);
                tcPollinationTb.setHarvestTypeName(tcPollinationExcelDTO.getHarvestTypeName());
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
