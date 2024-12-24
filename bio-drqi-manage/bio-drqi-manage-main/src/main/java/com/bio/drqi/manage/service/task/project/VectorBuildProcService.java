package com.bio.drqi.manage.service.task.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.contents.CerProjectContents;

import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.drqi.enums.VectorTaskTypeEnum;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.mapper.CerProjectTbMapper;
import com.bio.drqi.mapper.CerVectorGroupTbMapper;
import com.bio.drqi.mapper.CerVectorTaskTbMapper;
import com.bio.drqi.mapper.CerVectorTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service("vector_build")
@Slf4j
public class VectorBuildProcService extends AbstractBaseProjectTaskService {

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerVectorGroupTbMapper cerVectorGroupTbMapper;


    @Override
    public void taskCheck(BioTaskDtlTb bioTaskDtlTb) {
        log.info("【任务工单】载体构建开始");
        VectorTaskAddDTO vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskAddDTO.getVectorTaskCode());
        if (CerProjectContents.Y.equals(cerVectorTaskTb.getVectorBuildFlag())) {
            throw new BusinessException("已经发起过载体构建");
        }
        //判断任务类型，正常任务
        if (VectorTaskTypeEnum.type_1.code.equals(cerVectorTaskTb.getVectorTaskType())) {
            if (CollectionUtil.isEmpty(vectorTaskAddDTO.getVectorList())) {
                throw new BusinessException("质粒缺失");
            }
            List<String> plasmidNameList = vectorTaskAddDTO.getVectorList().stream().map(VectorTaskAddDTO.Vector::getPlasmidName).distinct().collect(Collectors.toList());
            if (plasmidNameList.size() != vectorTaskAddDTO.getVectorList().size()) {
                throw new BusinessException("载体构建中有重复质粒");
            }
//            for (VectorTaskAddDTO.Vector vector : vectorTaskAddDTO.getVectorList()) {
//                ResponseResult responseResult = plasmidApi.detail(vector.getPlasmidName());
//                if (responseResult.isError() || responseResult.getData() == null) {
//                    throw new BusinessException("质粒库不存在质粒:" + vector.getPlasmidName());
//                }
//            }
        } else {
            List<VectorTaskAddDTO.ExcelVector> excelVectorList = vectorTaskAddDTO.getExcelVectorList();
            if (CollectionUtil.isEmpty(excelVectorList)) {
                throw new BusinessException("测试质粒缺失");
            }
            List<String> plasmidNameList = excelVectorList.stream().map(VectorTaskAddDTO.ExcelVector::getPlasmidName).distinct().collect(Collectors.toList());
            if (plasmidNameList.size() != vectorTaskAddDTO.getExcelVectorList().size()) {
                throw new BusinessException("载体构建中有重复质粒");
            }
        }

        List<String> vectorGroupList = vectorTaskAddDTO.getVectorGroupList().stream().map(VectorTaskAddDTO.VectorGroup::getGroupName).distinct().collect(Collectors.toList());
        if (vectorGroupList.size() != vectorTaskAddDTO.getVectorGroupList().size()) {
            throw new BusinessException("载体构建中转化方案名称重复");
        }

        cerVectorTaskTb.setVectorBuildFlag(CerProjectContents.Y);
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);

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
            //判断任务类型，正常任务
            if (VectorTaskTypeEnum.type_1.code.equals(cerVectorTaskTb.getVectorTaskType())) {
                doNormalVectorTask(cerVectorTaskTb, vectorTaskAddDTO);
            } else {
                //其他任务需要解析excel
                doOtherVectorTask(vectorTaskAddDTO, cerVectorTaskTb);
            }
            //更新共转组信息
            List<CerVectorGroupTb> cerVectorGroupTbList = new ArrayList<>();
            vectorTaskAddDTO.getVectorGroupList().forEach(plasmidGroup -> {
                CerVectorGroupTb cerVectorGroupTb = new CerVectorGroupTb();
                cerVectorGroupTb.setVectorTaskId(cerVectorTaskTb.getId());
                cerVectorGroupTb.setGroupName(plasmidGroup.getGroupName());
                cerVectorGroupTb.setProjectId(cerVectorTaskTb.getProjectId());
                cerVectorGroupTb.setPlasmidNames(plasmidGroup.getPlasmidNames());
                cerVectorGroupTb.setRemark(plasmidGroup.getRemark());
                cerVectorGroupTb.setRepeatNum(plasmidGroup.getRepeatNum());
                cerVectorGroupTbList.add(cerVectorGroupTb);
            });
            try {
                cerVectorGroupTbMapper.insertBatch(cerVectorGroupTbList);
            } catch (DuplicateKeyException e) {
                throw new BusinessException("同一个项目中不能存在相同共转名称");
            }

            /**
             * 更新当前执行步骤
             */
            logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.vector_build, bioTaskDtlTb.getTaskNum());


            updateVectorTaskTimePlan(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.vector_build);
            log.info("【任务工单】载体任务创建完毕");
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        VectorTaskAddDTO vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskAddDTO.getVectorTaskCode());
        cerVectorTaskTb.setVectorBuildFlag(CerProjectContents.N);
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
    }

    private void doOtherVectorTask(VectorTaskAddDTO vectorTaskAddDTO, CerVectorTaskTb cerVectorTaskTb) {
        List<VectorTaskAddDTO.ExcelVector> excelVectorList = vectorTaskAddDTO.getExcelVectorList();
        List<CerVectorTb> cerVectorTbList = new ArrayList<>();
        for (VectorTaskAddDTO.ExcelVector excelVector : excelVectorList) {
            CerVectorTb cerVectorTb = new CerVectorTb();
            cerVectorTb.setVectorTaskId(cerVectorTaskTb.getId());
            cerVectorTb.setPlasmidName(excelVector.getPlasmidName());
            cerVectorTb.setBacterialResistance(excelVector.getBacterialResistance());
            cerVectorTb.setPlasmidSpecificPrimers(excelVector.getPlasmidSpecificPrimers());
            cerVectorTb.setCopyNumber(excelVector.getCopyNumber());
            cerVectorTb.setSelectionMarker(excelVector.getSelectionMarker());
            cerVectorTb.setRemark(excelVector.getRemark());
            cerVectorTb.setDestinationStripeSize(excelVector.getDestinationStripeSize());
            cerVectorTb.setVectorSize(excelVector.getVectorSize());
            cerVectorTbList.add(cerVectorTb);
        }
        try {
            cerVectorTbMapper.insertBatch(cerVectorTbList);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("同一个 实验方案中不能存在相同质粒：" + cerVectorTaskTb.getVectorTaskCode());
        }
    }

    private void doNormalVectorTask(CerVectorTaskTb cerVectorTaskTb, VectorTaskAddDTO vectorTaskAddDTO) {
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
            cerVectorTb.setForeignGene(vector.getForeignGene());
            cerVectorTb.setGeneCharacter(vector.getGeneCharacter());
            cerVectorTb.setTargetGene(vector.getTargetGene());
            cerVectorTb.setPam(vector.getPam());
            cerVectorTb.setRemark(vector.getRemark());
            cerVectorTb.setExpectedPositiveVaccine(vector.getExpectedPositiveVaccine());
            cerVectorTb.setFileUrls(JSONUtil.toJsonStr(vector.getFileUrls()));
            cerVectorTbList.add(cerVectorTb);
        }
        try {
            cerVectorTbMapper.insertBatch(cerVectorTbList);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("同一个实验方案中不能存在相同质粒：" + cerVectorTaskTb.getVectorTaskCode());
        }
    }

}
