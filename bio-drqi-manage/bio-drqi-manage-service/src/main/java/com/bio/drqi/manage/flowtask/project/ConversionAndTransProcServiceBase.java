package com.bio.drqi.manage.flowtask.project;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.ImplementationPlanTypeEnum;
import com.bio.drqi.manage.dto.project.ConversionAndTransDTO;
import com.bio.drqi.mapper.*;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service("conversion_and_trans")
@Slf4j
public class ConversionAndTransProcServiceBase extends AbstractProjectBaseTaskService {

    @Resource
    private CerConversionAndTransTbMapper cerConversionAndTransTbMapper;

    @Resource
    private CerConversionAndTransRefMapper cerConversionAndTransRefMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerTransformTbMapper cerTransformTbMapper;

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private PlantSingleStockTbMapper plantSingleStockTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    /**
     * 数据校验，暂时不做数据校验
     *
     * @param bioTaskDtlTb
     */
    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        ConversionAndTransDTO conversionAndTransDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ConversionAndTransDTO.class);
        if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getTransFormList())) {
            for (ConversionAndTransDTO.TransForm transForm : conversionAndTransDTO.getTransFormList()) {
                BeanUtils.trimFiledSpace(transForm);
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(transForm.getVectorTaskCode().trim());
                if (cerVectorTaskTb == null) {
                    throw new BusinessException("实施方案编号不存在：" + transForm.getVectorTaskCode());
                }
                CerTransformTb cerTransformTb = cerTransformTbMapper.selectOneByTransformCodeAndVectorTaskCode(transForm.getTransformCode(), cerVectorTaskTb.getVectorTaskCode());
                if (cerTransformTb == null) {
                    throw new BusinessException("此实施方案中无此转化编号：" + transForm.getTransformCode());
                }
                Long number = conversionAndTransDTO.getTransFormList().stream().filter(transForm1 -> StringUtils.equals(transForm1.getVectorTaskCode(), transForm.getVectorTaskCode()) && StringUtils.equals(transForm1.getTransformCode(), transForm.getTransformCode())).count();
                if (number > 1) {
                    throw new BusinessException("提交数据有重复");
                }
            }
        }
        if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getSampleCodeList())) {
            for (ConversionAndTransDTO.SampleCode sample : conversionAndTransDTO.getSampleCodeList()) {
                BeanUtils.trimFiledSpace(sample);
                if (StringUtils.isEmpty(sample.getEditPureUnion())) {
                    throw new BusinessException("是否编辑纯合不能为空");
                }
                CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(sample.getVectorTaskCode());
                if (cerVectorTaskTb == null) {
                    throw new BusinessException("实施方案编号不存在：" + sample.getVectorTaskCode());
                }
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByVectorTaskCodeAndSampleCode(sample.getVectorTaskCode(),sample.getSampleCode());
                if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
                    throw new BusinessException("此实施方案中无此取样编号：" + sample.getSampleCode());
                }

                //取样移苗时不校验是否是留种，如果把舍弃的苗移到CER,CER相关人员进行剔苗操作

 /*               if (CollectionUtil.isNotEmpty(cerSampleTestTbList.stream().filter(cerSampleTestTb -> "舍弃".equals(cerSampleTestTb.getCheckResult())).collect(Collectors.toList()))) {
                    throw new BusinessException("取样编号" + sample.getSampleCode() + "的检测结果为舍弃");
                }
                if (CollectionUtil.isEmpty(cerSampleTestTbList.stream().filter(cerSampleTestTb -> "传代".equals(cerSampleTestTb.getCheckResult()) || "留种".equals(cerSampleTestTb.getCheckResult())).collect(Collectors.toList()))) {
                    throw new BusinessException("取样编号" + sample.getSampleCode() + "还未检测完毕");
                }*/

                Long number = conversionAndTransDTO.getSampleCodeList().stream().filter(sample1 -> StringUtils.equals(sample1.getVectorTaskCode(), sample.getVectorTaskCode()) && StringUtils.equals(sample1.getSampleCode(), sample.getSampleCode())).count();
                if (number > 1) {
                    throw new BusinessException("提交数据有重复");
                }
            }
        }
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(conversionAndTransDTO));

    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            ConversionAndTransDTO conversionAndTransDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ConversionAndTransDTO.class);
            //取样移苗
            if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getSampleCodeList())) {
                for (ConversionAndTransDTO.SampleCode sampleCode : conversionAndTransDTO.getSampleCodeList()) {
                    if (StringUtils.isEmpty(sampleCode.getDealResult())) {
                        sampleCode.setDealResult(CerProjectContents.N);
                    }
                }
            }
            //转化移苗
            if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getTransFormList())) {
                for (ConversionAndTransDTO.TransForm transForm : conversionAndTransDTO.getTransFormList()) {
                    if (StringUtils.isEmpty(transForm.getDealResult())) {
                        transForm.setDealResult(CerProjectContents.N);
                    }
                }
            }


            //更新每一个实施方案的添加移苗步骤
            List<String> vectorTaskCodeList = null;
            if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getSampleCodeList())) {
                vectorTaskCodeList = conversionAndTransDTO.getSampleCodeList().stream().map(ConversionAndTransDTO.SampleCode::getVectorTaskCode).distinct().collect(Collectors.toList());
            }

            if (CollectionUtil.isNotEmpty(conversionAndTransDTO.getTransFormList())) {
                vectorTaskCodeList = conversionAndTransDTO.getTransFormList().stream().map(ConversionAndTransDTO.TransForm::getVectorTaskCode).distinct().collect(Collectors.toList());
            }
            if (CollectionUtil.isNotEmpty(vectorTaskCodeList)) {
                vectorTaskCodeList.forEach(vectorTaskCode -> {
                    CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
                    /**
                     * 更新当前执行步骤
                     */
                    logStep(cerVectorTaskTb.getId(), ImplementationPlanTypeEnum.cer_plant, bioTaskDtlTb.getTaskNum());
                });
            }

            bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(conversionAndTransDTO));
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        CerConversionAndTransTb cerConversionAndTransTb = cerConversionAndTransTbMapper.selectOneByTaskNum(bioTaskDtlTb.getTaskNum());
        if (cerConversionAndTransTb != null) {
            cerConversionAndTransTbMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
            cerConversionAndTransRefMapper.deleteByConversionAndTransId(cerConversionAndTransTb.getId());
            plantSingleStockTbMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
        }
        cerVectorStepLogMapper.deleteByTaskNumAndStepCode(bioTaskDtlTb.getTaskNum(), ImplementationPlanTypeEnum.cer_plant.name());

    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        ConversionAndTransDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), ConversionAndTransDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        boolean hasSample = CollectionUtil.isNotEmpty(dto.getSampleCodeList());
        boolean hasTransform = CollectionUtil.isNotEmpty(dto.getTransFormList());

        List<BioHtmlModelDTO.ModelField> fieldList = new ArrayList<>();
        fieldList.add(buildField("交接日期", dto.getHandoverDate()));
        fieldList.add(buildField("总数量", dto.getTotalNum() == null ? "" : String.valueOf(dto.getTotalNum())));
        if (hasSample) {
            fieldList.add(buildField("移苗类型", "取样移苗"));
            fieldList.add(buildField("取样移苗数量", String.valueOf(dto.getSampleCodeList().size())));
        }
        if (hasTransform) {
            fieldList.add(buildField("移苗类型", "转化移苗"));
            fieldList.add(buildField("转化移苗数量", String.valueOf(dto.getTransFormList().size())));
        }
        fieldList.add(buildField("备注", dto.getRemark()));
        sections.add(buildFieldSection("申请信息", fieldList));

        if (hasSample) {
            List<String> headers = Arrays.asList("实施方案编号", "取样编号", "是否编辑纯合", "受体材料", "是否转基因", "质粒名称", "是否接收", "备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (ConversionAndTransDTO.SampleCode item : dto.getSampleCodeList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("实施方案编号", item.getVectorTaskCode());
                row.put("取样编号", item.getSampleCode());
                row.put("是否编辑纯合", yesNoDesc(item.getEditPureUnion()));
                row.put("受体材料", item.getAcceptorMaterial());
                row.put("是否转基因", transGeneFlagName(item.getTransGeneFlag()));
                row.put("质粒名称", item.getPlasmidName());
                row.put("是否接收", receiveResultName(item.getDealResult()));
                row.put("备注", item.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("取样移苗明细", headers, rows));
        }

        if (hasTransform) {
            List<String> headers = Arrays.asList("实施方案编号", "转化编号", "受体材料", "移苗数量", "是否转基因", "质粒名称", "是否接收", "确认接收数量", "备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (ConversionAndTransDTO.TransForm item : dto.getTransFormList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("实施方案编号", item.getVectorTaskCode());
                row.put("转化编号", item.getTransformCode());
                row.put("受体材料", item.getAcceptorMaterial());
                row.put("移苗数量", item.getTransNum());
                row.put("是否转基因", transGeneFlagName(item.getTransGeneFlag()));
                row.put("质粒名称", item.getPlasmidName());
                row.put("是否接收", receiveResultName(item.getDealResult()));
                row.put("确认接收数量", item.getAcceptNum());
                row.put("备注", item.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("转化移苗明细", headers, rows));
        }

        return sections;
    }

    private String yesNoDesc(String value) {
        if (CerProjectContents.Y.equals(value)) {
            return "是";
        }
        if (CerProjectContents.N.equals(value)) {
            return "否";
        }
        return value;
    }

    private String transGeneFlagName(String value) {
        if (CerProjectContents.Y.equals(value)) {
            return "是";
        }
        if (CerProjectContents.N.equals(value)) {
            return "否";
        }
        if ("O".equals(value)) {
            return "N/A";
        }
        return value;
    }

    private String receiveResultName(String value) {
        if (CerProjectContents.Y.equals(value)) {
            return "接收";
        }
        if (CerProjectContents.N.equals(value)) {
            return "不接收";
        }
        return value;
    }
}
