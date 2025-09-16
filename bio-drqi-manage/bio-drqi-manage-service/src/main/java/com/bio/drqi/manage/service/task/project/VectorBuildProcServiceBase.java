package com.bio.drqi.manage.service.task.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.contents.CerProjectContents;

import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.drqi.enums.VectorTaskTypeEnum;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.manage.feign.PlasmidAPi;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("vector_build")
@Slf4j
public class VectorBuildProcServiceBase extends AbstractProjectBaseTaskService {

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private PlasmidAPi plasmidApi;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        log.info("【任务工单】载体构建开始");
        VectorTaskAddDTO vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);

        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskAddDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("无此实施方案信息");
        }
        if (CollectionUtil.isNotEmpty(cerVectorTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId()))) {
            throw new BusinessException("已经发起过载体构建，请不要重复发起");
        }
        CerVectorStepLog cerVectorStepLog = cerVectorStepLogMapper.selectOneByVectorTaskIdAndStepCode(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.vector_build.name());
        if (cerVectorStepLog != null) {
            BioTaskDtlTb vectorBuildBioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerVectorStepLog.getTaskNum());
            if (vectorBuildBioTaskDtlTb != null && BioTaskStatusEnum.TASK_STATUS_1.status.equals(vectorBuildBioTaskDtlTb.getTaskStatus())) {
                throw new BusinessException("已经有一个载体构建正在审批中：" + vectorBuildBioTaskDtlTb.getTaskNum());
            }
        }
        //判断任务类型，正常任务

        if (CollectionUtil.isEmpty(vectorTaskAddDTO.getVectorList())) {
            throw new BusinessException("质粒缺失");
        }
        List<String> plasmidNameList = vectorTaskAddDTO.getVectorList().stream().map(VectorTaskAddDTO.Vector::getPlasmidName).distinct().collect(Collectors.toList());
        if (plasmidNameList.size() != vectorTaskAddDTO.getVectorList().size()) {
            throw new BusinessException("载体构建中有重复质粒");
        }
        if(BioDrQiContents.Y.equals(cerVectorTaskTb.getNoPlasmidFlag())){
            for (VectorTaskAddDTO.Vector vector : vectorTaskAddDTO.getVectorList()) {
                ResponseResult responseResult = plasmidApi.detail(vector.getPlasmidName());
                if (responseResult.isError() || responseResult.getData() == null) {
                    throw new BusinessException("质粒库不存在质粒:" + vector.getPlasmidName());
                }
            }
        }
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            log.info("【任务工单】载体构建开始");
            VectorTaskAddDTO vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskAddDTO.getVectorTaskCode());
            if (cerVectorTaskTb == null) {
                throw new BusinessException("实施方案不存在");
            }
            CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(cerVectorTaskTb.getProjectId());
            if (cerProjectTb == null) {
                throw new BusinessException("数据异常，不存在所属项目");
            }
            if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
                throw new BusinessException("不是进行中项目");
            }
            if (CollectionUtil.isNotEmpty(cerVectorTbMapper.selectAllByVectorTaskId(cerVectorTaskTb.getId()))) {
                throw new BusinessException("已经发起过载体构建，请不要重复发起");
            }
            doNormalVectorTask(cerVectorTaskTb, vectorTaskAddDTO, bioTaskDtlTb.getTaskNum());

            /**
             * 更新当前执行步骤
             */
            logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.vector_build, bioTaskDtlTb.getTaskNum());

            //更新备注
            cerVectorTaskTb.setVectorBuildRemark(vectorTaskAddDTO.getRemark());
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);

            log.info("【任务工单】载体任务创建完毕");
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        cerVectorStepLogMapper.deleteByTaskNumAndStepCode(bioTaskDtlTb.getTaskNum(), ImplementationPlanTypeEnum.vector_build.name());
    }

    private void doNormalVectorTask(CerVectorTaskTb cerVectorTaskTb, VectorTaskAddDTO vectorTaskAddDTO, String taskNum) {
        //更新载体信息
        List<VectorTaskAddDTO.Vector> vectorList = vectorTaskAddDTO.getVectorList();
        List<CerVectorTb> cerVectorTbList = new ArrayList<>();
        for (VectorTaskAddDTO.Vector vector : vectorList) {
            CerVectorTb cerVectorTb = new CerVectorTb();
            cerVectorTb.setVectorTaskId(cerVectorTaskTb.getId());
            cerVectorTb.setPlasmidName(vector.getPlasmidName());
            cerVectorTb.setTargetSite(vector.getTargetSite());
            cerVectorTb.setBacterialResistance(vector.getBacterialResistance());
            cerVectorTb.setPlasmidSpecificPrimers(vector.getPlasmidSpecificPrimers());
            cerVectorTb.setBacterialReplicon(vector.getBacterialReplicon());
            cerVectorTb.setCopyNumber(vector.getCopyNumber());
            cerVectorTb.setAgrobacteriumInformation(vector.getAgrobacteriumInformation());
            cerVectorTb.setSelectionMarker(vector.getSelectionMarker());
            cerVectorTb.setGeneCharacter(vector.getGeneCharacter());
            cerVectorTb.setFileUrls(JSONUtil.toJsonStr(vector.getFileUrls()));
            cerVectorTb.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
            cerVectorTb.setTaskNum(taskNum);
            cerVectorTbList.add(cerVectorTb);
        }
        try {
            cerVectorTbMapper.insertBatch(cerVectorTbList);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("同一个实验方案中不能存在相同质粒：" + cerVectorTaskTb.getVectorTaskCode());
        }
    }

}
