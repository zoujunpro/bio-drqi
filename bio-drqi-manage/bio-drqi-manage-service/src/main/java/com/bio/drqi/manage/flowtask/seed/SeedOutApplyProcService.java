package com.bio.drqi.manage.flowtask.seed;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.manage.dto.seed.SeedOutDTO;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 其他出库
 */
@Service("seed_out_apply")
@Slf4j
public class SeedOutApplyProcService extends AbstractSeedTaskService {

    private static final String USE_TO_DESC = "其他";

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        SeedOutDTO seedOutDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedOutDTO.class);
        List<SeedOutDTO.ExecuteFormContent> executeFormContentList = seedOutDTO.getExecuteForm().getExecuteFormContentList();
        Map<String, List<SeedOutDTO.ExecuteFormContent>> map = executeFormContentList.stream().collect(Collectors.groupingBy(SeedOutDTO.ExecuteFormContent::getSeedNum));
        if (CollectionUtil.isNotEmpty(map)) {
            map.forEach((seedNum, executeFormContents) -> {
                List<String> numList = executeFormContents.stream().map(SeedOutDTO.ExecuteFormContent::getNum).collect(Collectors.toList());
                BigDecimal numCount = new BigDecimal("0");
                for (String num:numList){
                    numCount=numCount.add(new BigDecimal(num));
                }
                checkSeedStock(seedNum, numCount);
            });
        }
        //不要做序列化，前端给的有自定义数据
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        log.info("种子库出库扣减库存开始：taskNum={} status={}", bioTaskDtlTb.getTaskNum(), bioTaskDtlTb.getTaskStatus());
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            SeedOutDTO seedOutDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedOutDTO.class);
            if (Objects.nonNull(seedOutDTO.getExecuteForm()) && seedOutDTO.getExecuteForm().getExecuteFormContentList().size() > 0) {
                for (int i = 0; i < seedOutDTO.getExecuteForm().getExecuteFormContentList().size(); i++) {
                    SeedOutDTO.ExecuteFormContent executeFormContent = seedOutDTO.getExecuteForm().getExecuteFormContentList().get(i);
                    //扣减库存，记录出库日志
                    reduceSeedStock(executeFormContent.getSeedNum(), bioTaskDtlTb, new BigDecimal(executeFormContent.getNum()), executeFormContent.getRemark(), i + 1, seedOutDTO.getApplyFrom().getUseToDesc());
                }
            }
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        //不做任何处理

    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        SeedOutDTO seedOutDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedOutDTO.class);
        if (seedOutDTO == null || seedOutDTO.getApplyFrom() == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        SeedOutDTO.ApplyFrom applyFrom = seedOutDTO.getApplyFrom();

        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("用途", applyFrom.getUseToDesc()));
        applyFields.add(buildField("出库类型", defaultString(applyFrom.getOutType())));
        applyFields.add(buildField("交付方式", translateDeliverMethod(applyFrom.getDeliverMethod())));
        applyFields.add(buildField("接收人", applyFrom.getReceiverName()));
        applyFields.add(buildField("联系电话", applyFrom.getReceiverTelephone()));
        applyFields.add(buildField("接收地址", applyFrom.getReceiverAddress()));
        applyFields.add(buildField("种子要求", applyFrom.getSeedDemandDesc()));
        applyFields.add(buildField("分装和标签要求", applyFrom.getLabelDemandDesc()));
        applyFields.add(buildField("备注", applyFrom.getApplyRemark()));
        sections.add(buildFieldSection("申请信息", applyFields));

        List<SeedOutDTO.ApplyFromContent> contentList = applyFrom.getApplyFromContentList();
        if (CollectionUtil.isNotEmpty(contentList)) {
            List<String> headers = Arrays.asList(
                    "种子编号", "项目编号", "项目名称", "子项目编号", "实施方案编号",
                    "基因型", "物种", "品种", "产地", "年份", "发芽率", "性状纯度",
                    "申请数量", "单位", "是否包衣", "备注"
            );
            List<Map<String, Object>> rows = new ArrayList<>();
            for (SeedOutDTO.ApplyFromContent content : contentList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("种子编号", content.getSeedNum());
                row.put("项目编号", content.getProjectCode());
                row.put("项目名称", content.getProjectName());
                row.put("子项目编号", content.getSubProjectCode());
                row.put("实施方案编号", content.getVectorTaskCode());
                row.put("基因型", content.getGeneType());
                row.put("物种", StringUtils.isNotEmpty(content.getSpeciesName()) ? content.getSpeciesName() : content.getSpeciesCode());
                row.put("品种", StringUtils.isNotEmpty(content.getBreedName()) ? content.getBreedName() : content.getBreedCode());
                row.put("产地", content.getProductAddress());
                row.put("年份", content.getYear());
                row.put("发芽率", content.getSgr());
                row.put("性状纯度", content.getTpur());
                row.put("申请数量", content.getNum());
                row.put("单位", content.getUnit());
                row.put("是否包衣", translateCoatingFlag(content.getCoatingFlag()));
                row.put("备注", content.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("出库明细", headers, rows));
        }

        return sections;
    }

    private String translateDeliverMethod(String deliverMethod) {
        if (StringUtils.isEmpty(deliverMethod)) {
            return "";
        }
        if ("1".equals(deliverMethod)) {
            return "邮寄";
        }
        if ("2".equals(deliverMethod)) {
            return "自提";
        }
        return deliverMethod;
    }

    private String translateCoatingFlag(String coatingFlag) {
        if (StringUtils.isEmpty(coatingFlag)) {
            return "";
        }
        if ("Y".equalsIgnoreCase(coatingFlag)) {
            return "是";
        }
        if ("N".equalsIgnoreCase(coatingFlag)) {
            return "否";
        }
        return coatingFlag;
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}
