package com.bio.drqi.tc.service.flowtask;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.TcHarvestSeedApplyTb;
import com.bio.drqi.domain.TcHarvestSeedTb;
import com.bio.drqi.domain.TcPollinationApplyTb;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.TcHarvestSeedApplyTbMapper;
import com.bio.drqi.mapper.TcHarvestSeedTbMapper;
import com.bio.drqi.mapper.TcPollinationApplyTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.tc.service.dto.TcHarvestTaskDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;


@Service("tc_harvest_task_apply")
@Slf4j
public class TcHarvestTaskService extends AbstractTcBaseTaskService {

    @Resource
    private TcHarvestSeedApplyTbMapper tcHarvestSeedApplyTbMapper;

    @Resource
    private TcHarvestSeedTbMapper tcHarvestSeedTbMapper;

    @Resource
    private TcPollinationApplyTbMapper tcPollinationApplyTbMapper;

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



        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }
}
