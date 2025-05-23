package com.bio.drqi.tc.service.flowtask;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.TcHarvestSeedApplyTbMapper;
import com.bio.drqi.mapper.TcHarvestSeedTbMapper;
import com.bio.drqi.mapper.TcPollinationApplyTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.tc.service.dto.TcHarvestTaskDTO;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;


@Service("tc_harvest_task_apply")
@Slf4j
public class TcHarvestTaskService extends AbstractTcBaseTaskService {

    @Resource
    private TcHarvestSeedApplyTbMapper tcHarvestSeedApplyTbMapper;

    @Resource
    private TcHarvestSeedTbMapper tcHarvestSeedTbMapper;

    @Resource
    private OssService ossService;

    @Resource
    private TcPollinationApplyTbMapper tcPollinationApplyTbMapper;

    private TcPollinationTbMapper tcPollinationTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcHarvestTaskDTO tcHarvestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcHarvestTaskDTO.class);
        ValidatorUtil.validator(tcHarvestTaskDTO);
        BeanUtils.trimFiledSpace(tcHarvestTaskDTO);
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcHarvestTaskDTO tcHarvestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcHarvestTaskDTO.class);
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            TcPollinationApplyTb tcPollinationApplyTb = tcPollinationApplyTbMapper.selectOneByPollinationApplyNum(tcHarvestTaskDTO.getPollinationApplyNum());
            if (tcPollinationApplyTb == null) {
                throw new BusinessException("不存在此授粉批次");
            }
            if (StringUtils.isNotEmpty(tcPollinationApplyTb.getHarvestApplyNum())) {
                throw new BusinessException("该授粉批次已经收获");
            }
            TcHarvestSeedApplyTb tcHarvestSeedApplyTb = new TcHarvestSeedApplyTb();
            tcHarvestSeedApplyTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            tcHarvestSeedApplyTb.setPollinationApplyNum(tcHarvestTaskDTO.getPollinationApplyNum());
            tcHarvestSeedApplyTb.setHarvestApplyNum(bioTaskDtlTb.getTaskNum());
            tcHarvestSeedApplyTb.setHarvestTime(new String());
            tcHarvestSeedApplyTb.setCreateTime(bioTaskDtlTb.getCreateTime());
            tcHarvestSeedApplyTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
            tcHarvestSeedApplyTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
            tcHarvestSeedApplyTb.setExperimentNum(tcPollinationApplyTb.getExperimentNum());
            tcHarvestSeedApplyTb.setHarvestFileUrl(tcHarvestTaskDTO.getHarvestFileUrl());
            try {
                tcHarvestSeedApplyTbMapper.insert(tcHarvestSeedApplyTb);
            } catch (DuplicateKeyException e) {
                throw new BusinessException("重复收获操作");
            }

            //更新收获批次号
            tcPollinationApplyTb.setHarvestApplyNum(tcHarvestSeedApplyTb.getHarvestApplyNum());
            tcPollinationApplyTbMapper.updateById(tcPollinationApplyTb);

            //解析excel

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
            List<TcPollinationExcelDTO> tcPollinationExcelDTOList = ExcelUtil.readExcel(tempFilePath, TcPollinationExcelDTO.class);
            for (TcPollinationExcelDTO tcPollinationExcelDTO:tcPollinationExcelDTOList){

            }



        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
