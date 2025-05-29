package com.bio.drqi.tc.service.flowtask;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.TcPollinationApplyTb;
import com.bio.drqi.domain.TcPollinationTb;
import com.bio.drqi.mapper.TcPollinationApplyTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.tc.service.dto.TcHarvestExcelDTO;
import com.bio.drqi.tc.service.dto.TcHarvestTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;


@Service("tc_harvest_task_apply")
@Slf4j
public class TcHarvestTaskService extends AbstractTcBaseTaskService {

    @Resource
    private OssService ossService;

    @Resource
    private TcPollinationApplyTbMapper tcPollinationApplyTbMapper;


    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcHarvestTaskDTO tcHarvestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcHarvestTaskDTO.class);
        ValidatorUtil.validator(tcHarvestTaskDTO);
        BeanUtils.trimFiledSpace(tcHarvestTaskDTO);

        TcPollinationApplyTb tcPollinationApplyTb = tcPollinationApplyTbMapper.selectOneByPollinationApplyNum(tcHarvestTaskDTO.getPollinationApplyNum());
        if (tcPollinationApplyTb == null) {
            throw new BusinessException("不存在此授粉批次");
        }
        if (StringUtils.isNotEmpty(tcPollinationApplyTb.getHarvestApplyNum())) {
            throw new BusinessException("该授粉批次已经收获");
        }
        if (!tcHarvestTaskDTO.getHarvestFileUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + tcHarvestTaskDTO.getHarvestFileUrl();
        try {
            ossService.downloadPath(tempFilePath, tcHarvestTaskDTO.getHarvestFileUrl());
        } catch (Exception e) {
            log.error("【任务工单】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<TcHarvestExcelDTO> tcHarvestExcelDTOList = ExcelUtil.readExcel(tempFilePath, TcHarvestExcelDTO.class);
        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectAllByPollinationApplyNum(tcPollinationApplyTb.getHarvestApplyNum());
        if (tcPollinationTbList.size() != tcHarvestExcelDTOList.size()) {
            throw new BusinessException("收获总数据和授粉数不匹配，请核实收获内容");
        }
        for (TcHarvestExcelDTO tcHarvestExcelDTO : tcHarvestExcelDTOList) {
            TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSampleCodeAndMSampleCode
                    (tcPollinationApplyTb.getExperimentNum(),
                            tcHarvestExcelDTO.getFatherRegionNum(),
                            tcHarvestExcelDTO.getMotherRegionNum(),
                            tcHarvestExcelDTO.getFatherSeedNum(),
                            tcHarvestExcelDTO.getMotherSeedNum(),
                            tcHarvestExcelDTO.getFatherSampleCode(),
                            tcHarvestExcelDTO.getMotherSampleCode());
            if (tcPollinationTb == null) {
                throw new BusinessException("无此授粉记录：" + JSONUtil.toJsonStr(tcHarvestExcelDTO));
            }
        }
        tcHarvestTaskDTO.setTcHarvestExcelDTOList(tcHarvestExcelDTOList);
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcHarvestTaskDTO));
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcHarvestTaskDTO tcHarvestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcHarvestTaskDTO.class);
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            TcPollinationApplyTb tcPollinationApplyTb = tcPollinationApplyTbMapper.selectOneByPollinationApplyNum(tcHarvestTaskDTO.getPollinationApplyNum());
            tcPollinationApplyTb.setHarvestApplyNum(bioTaskDtlTb.getTaskNum());
            tcPollinationApplyTb.setHarvestExcelUrl(tcHarvestTaskDTO.getHarvestFileUrl());
            tcPollinationApplyTb.setHarvestApplyNum(bioTaskDtlTb.getTaskNum());
            tcPollinationApplyTbMapper.updateById(tcPollinationApplyTb);


            for (TcHarvestExcelDTO tcHarvestExcelDTO : tcHarvestTaskDTO.getTcHarvestExcelDTOList()) {
                TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSampleCodeAndMSampleCode
                        (tcPollinationApplyTb.getExperimentNum(),
                                tcHarvestExcelDTO.getFatherRegionNum(),
                                tcHarvestExcelDTO.getMotherRegionNum(),
                                tcHarvestExcelDTO.getFatherSeedNum(),
                                tcHarvestExcelDTO.getMotherSeedNum(),
                                tcHarvestExcelDTO.getFatherSampleCode(),
                                tcHarvestExcelDTO.getMotherSampleCode());
                if (tcPollinationTb == null) {
                    throw new BusinessException("无此授粉记录：" + JSONUtil.toJsonStr(tcHarvestExcelDTO));
                }
                tcPollinationTb.setUnit(tcHarvestExcelDTO.getUnit());
                tcPollinationTb.setSeedNumber(new BigDecimal(StringUtils.isEmpty(tcHarvestExcelDTO.getSeedNumber()) ? "0" : tcHarvestExcelDTO.getSeedNumber()));
                tcPollinationTbMapper.updateById(tcPollinationTb);
            }


        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
