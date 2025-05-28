package com.bio.drqi.manage.service.task.project;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.VectorTaskStatusEnum;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerInstantVerifyTaskTb;
import com.bio.drqi.domain.CerVectorTaskTb;
import com.bio.drqi.manage.dto.project.InstantVerifyTaskDTO;
import com.bio.drqi.mapper.CerInstantVerifyTaskTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 瞬时验证任务
 */
@Service("instant_verify_task")
public class InstantVerifyBaseTaskService extends AbstractProjectBaseTaskService {



    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;


    @Resource
    private CerInstantVerifyTaskTbMapper cerInstantVerifyTaskTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        //不需要实现
        InstantVerifyTaskDTO instantVerifyTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), InstantVerifyTaskDTO.class);
        ValidatorUtil.validator(instantVerifyTaskDTO);
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(instantVerifyTaskDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("未找到实施方案信息 vectorTaskCode=" + instantVerifyTaskDTO.getVectorTaskCode());
        }
        if (!VectorTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
            throw new BusinessException("实施方案不是进行中状态");
        }

        CerInstantVerifyTaskTb cerInstantVerifyTaskTb= cerInstantVerifyTaskTbMapper.selectOneByInstantVerifyCode(instantVerifyTaskDTO.getVerifyTaskCode());
        if(cerInstantVerifyTaskTb!=null){
            throw new BusinessException("瞬时测试任务编号存在");
        }
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(instantVerifyTaskDTO));
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            InstantVerifyTaskDTO instantVerifyTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), InstantVerifyTaskDTO.class);

            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(instantVerifyTaskDTO.getVectorTaskCode());

            CerInstantVerifyTaskTb cerInstantVerifyTaskTb=new CerInstantVerifyTaskTb();
            cerInstantVerifyTaskTb.setProjectId(cerVectorTaskTb.getProjectId());
            cerInstantVerifyTaskTb.setSubProjectId(cerVectorTaskTb.getSubProjectId());
            cerInstantVerifyTaskTb.setVectorTaskId(cerVectorTaskTb.getId());
            cerInstantVerifyTaskTb.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
            cerInstantVerifyTaskTb.setInstantVerifyCode(instantVerifyTaskDTO.getVerifyTaskCode());
            cerInstantVerifyTaskTb.setProjectCode(cerVectorTaskTb.getProjectCode());
            cerInstantVerifyTaskTb.setSubProjectCode(cerVectorTaskTb.getSubProjectCode());
            cerInstantVerifyTaskTb.setTextJson(JSONUtil.toJsonStr(instantVerifyTaskDTO));
            cerInstantVerifyTaskTb.setCreateTime(new Date());
            cerInstantVerifyTaskTbMapper.insert(cerInstantVerifyTaskTb);
        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        InstantVerifyTaskDTO instantVerifyTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), InstantVerifyTaskDTO.class);
        cerInstantVerifyTaskTbMapper.deleteByInstantVerifyCode(instantVerifyTaskDTO.getVerifyTaskCode());

    }
}
