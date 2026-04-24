package com.bio.drqi.manage.flowtask.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.contents.BioDrQiContents;

import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.enums.GeneEditTypeEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.VectorTaskStatusEnum;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.manage.feign.PlasmidAPi;
import com.bio.drqi.mapper.*;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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
        ValidatorUtil.validator(vectorTaskAddDTO);
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskAddDTO.getVectorTaskCode());
        if (cerVectorTaskTb == null) {
            throw new BusinessException("无此实施方案信息");
        }
        if(!VectorTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())){
            throw new BusinessException("不是进行中实施方案");
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
        if(!BioDrQiContents.Y.equals(cerVectorTaskTb.getNoPlasmidFlag())){
            for (VectorTaskAddDTO.Vector vector : vectorTaskAddDTO.getVectorList()) {
                ResponseResult responseResult = plasmidApi.detail(vector.getPlasmidName());
                if (responseResult.isError() || responseResult.getData() == null) {
                    throw new BusinessException("质粒库不存在质粒:" + vector.getPlasmidName());
                }
            }
        }
        /**
         * 更新当前执行步骤
         */
        logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.vector_build, bioTaskDtlTb.getTaskNum());
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
            if(!VectorTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())){
                throw new BusinessException("不是进行中实施方案");
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

            //更新备注
            cerVectorTaskTb.setVectorBuildRemark(vectorTaskAddDTO.getRemark());
            cerVectorTaskTbMapper.updateById(cerVectorTaskTb);

            /**
             * 更新当前执行步骤,有则不更新
             */
            logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.vector_build, bioTaskDtlTb.getTaskNum());
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
            cerVectorTb.setBacterialReplicon(vector.getBacterialReplicon());
            cerVectorTb.setCopyNumber(vector.getCopyNumber());
            cerVectorTb.setAgrobacteriumInformation(vector.getAgrobacteriumInformation());
            cerVectorTb.setSelectionMarker(vector.getSelectionMarker());
            cerVectorTb.setGeneCharacter(vector.getGeneCharacter());
            cerVectorTb.setFileUrls(JSONUtil.toJsonStr(vector.getFileUrls()));
            cerVectorTb.setVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
            cerVectorTb.setTransFlag(vectorTaskAddDTO.getTransFlag());
            cerVectorTb.setTaskNum(taskNum);
            cerVectorTbList.add(cerVectorTb);
        }
        try {
            cerVectorTbMapper.insertBatch(cerVectorTbList);
        } catch (DuplicateKeyException e) {
            throw new BusinessException("同一个实验方案中不能存在相同质粒：" + cerVectorTaskTb.getVectorTaskCode());
        }
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        VectorTaskAddDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        CerVectorTaskTb vectorTask = cerVectorTaskTbMapper.selectOneByVectorTaskCode(dto.getVectorTaskCode());

        List<BioHtmlModelDTO.ModelField> applyInfoFields = new ArrayList<>();
        applyInfoFields.add(buildField("项目名称", dto.getProjectName()));
        applyInfoFields.add(buildField("项目编号", dto.getProjectCode()));
        applyInfoFields.add(buildField("子项目编号", dto.getSubProjectCode()));
        applyInfoFields.add(buildField("实施方案编号", dto.getVectorTaskCode()));
        applyInfoFields.add(buildField("材料类型", transFlagName(dto.getTransFlag())));
        applyInfoFields.add(buildField("载体数量", String.valueOf(CollectionUtil.isEmpty(dto.getVectorList()) ? 0 : dto.getVectorList().size())));
        if (vectorTask != null) {
            applyInfoFields.add(buildField("递送方式", deliveryMethodName(vectorTask.getDeliveryMethod())));
            applyInfoFields.add(buildField("受体材料", vectorTask.getAcceptorMaterial()));
            applyInfoFields.add(buildField("编辑类型", editTypeName(vectorTask.getEditType())));
            applyInfoFields.add(buildField("期望阳性苗", vectorTask.getExpectedPositiveSeed()));
        }
        applyInfoFields.add(buildField("备注", dto.getRemark()));
        sections.add(buildFieldSection("申请信息", applyInfoFields));

        if (dto.getTransportStart() != null) {
            List<BioHtmlModelDTO.ModelField> transportStartFields = new ArrayList<>();
            transportStartFields.add(buildField("发货地", dto.getTransportStart().getDeliveryLocation()));
            transportStartFields.add(buildField("发货日期", dto.getTransportStart().getDeliveryDate()));
            transportStartFields.add(buildField("快递公司", dto.getTransportStart().getExpressName()));
            transportStartFields.add(buildField("运单号", dto.getTransportStart().getExpressNumber()));
            transportStartFields.add(buildField("样品信息", dto.getTransportStart().getSampleInfo()));
            transportStartFields.add(buildField("外层包装类型及数量", dto.getTransportStart().getNumDesc()));
            transportStartFields.add(buildField("备注", dto.getTransportStart().getRemark()));
            if (hasFieldValue(transportStartFields)) {
                sections.add(buildFieldSection("运输发出信息", transportStartFields));
            }
        }

        if (dto.getTransportEnd() != null) {
            List<BioHtmlModelDTO.ModelField> transportEndFields = new ArrayList<>();
            transportEndFields.add(buildField("收货地", dto.getTransportEnd().getReceiptLocation()));
            transportEndFields.add(buildField("收货日期", dto.getTransportEnd().getReceiptDate()));
            transportEndFields.add(buildField("包装是否完好", yesNoDesc(dto.getTransportEnd().getCheck1())));
            transportEndFields.add(buildField("数量是否匹配", yesNoDesc(dto.getTransportEnd().getCheck2())));
            transportEndFields.add(buildField("备注", dto.getTransportEnd().getRemark()));
            if (hasFieldValue(transportEndFields)) {
                sections.add(buildFieldSection("运输接收信息", transportEndFields));
            }
        }

        if (CollectionUtil.isNotEmpty(dto.getVectorList())) {
            List<String> headers = Arrays.asList("质粒名称", "靶位点", "细菌抗性", "细菌复制子", "拷贝数", "农杆菌信息", "植物筛选标记", "目标特性", "浓度", "体积");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (VectorTaskAddDTO.Vector item : dto.getVectorList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("质粒名称", item.getPlasmidName());
                row.put("靶位点", item.getTargetSite());
                row.put("细菌抗性", item.getBacterialResistance());
                row.put("细菌复制子", item.getBacterialReplicon());
                row.put("拷贝数", item.getCopyNumber());
                row.put("农杆菌信息", item.getAgrobacteriumInformation());
                row.put("植物筛选标记", item.getSelectionMarker());
                row.put("目标特性", item.getGeneCharacter());
                row.put("浓度", item.getConcentration());
                row.put("体积", item.getCapacity());
                rows.add(row);
            }
            sections.add(buildTableSection("载体构建明细", headers, rows));
        }

        return sections;
    }

    private boolean hasFieldValue(List<BioHtmlModelDTO.ModelField> fieldList) {
        return fieldList.stream().anyMatch(field -> cn.hutool.core.util.StrUtil.isNotBlank(field.getValue()));
    }

    private String deliveryMethodName(String code) {
        if ("A".equals(code)) {
            return "农杆菌转化";
        }
        if ("B".equals(code)) {
            return "基因枪";
        }
        if ("P".equals(code)) {
            return "原生质体转化";
        }
        if ("V".equals(code)) {
            return "病毒载体";
        }
        return code;
    }

    private String editTypeName(String code) {
        if ("1".equals(code)) {
            return "KO";
        }
        if ("2".equals(code)) {
            return "点突变";
        }
        if ("3".equals(code)) {
            return "精准小";
        }
        if ("4".equals(code)) {
            return "精准大";
        }
        if ("5".equals(code)) {
            return "随机转基因";
        }
        return code;
    }

    private String transFlagName(String code) {
        for (GeneEditTypeEnum value : GeneEditTypeEnum.values()) {
            if (value.code.equals(code)) {
                return value.name;
            }
        }
        return code;
    }

    private String yesNoDesc(String value) {
        if (BioDrQiContents.Y.equals(value)) {
            return "是";
        }
        if (BioDrQiContents.N.equals(value)) {
            return "否";
        }
        return value;
    }
}
