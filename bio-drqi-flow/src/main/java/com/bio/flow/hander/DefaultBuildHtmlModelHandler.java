package com.bio.flow.hander;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.util.QrCodeUtil;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.flow.dto.ApproveDetailRspDTO;
import com.bio.flow.dto.BioHtmlModelDTO;
import com.bio.flow.service.FlowService;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public abstract class DefaultBuildHtmlModelHandler {

    @Resource
    private FlowService flowService;

    public BioHtmlModelDTO handler(BioTaskDtlTb bioTaskDtlTb) {
        BioHtmlModelDTO bioHtmlModelDTO = new BioHtmlModelDTO();
        bioHtmlModelDTO.setModelHeader(getModelHeader(bioTaskDtlTb));
        bioHtmlModelDTO.setSections(getSections(bioTaskDtlTb));
        bioHtmlModelDTO.setModelBottomList(getModelBottomList(bioTaskDtlTb));
        return bioHtmlModelDTO;
    }

    abstract public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb);

    protected BioHtmlModelDTO.ModelHeader getModelHeader(BioTaskDtlTb bioTaskDtlTb) {
        BioHtmlModelDTO.ModelHeader modelHeader = new BioHtmlModelDTO.ModelHeader();
        modelHeader.setTaskNum(bioTaskDtlTb.getTaskNum());
        modelHeader.setTaskTypeCode(bioTaskDtlTb.getTaskTypeCode());
        modelHeader.setTaskTypeName(bioTaskDtlTb.getTaskTypeName());
        modelHeader.setTaskDesc(bioTaskDtlTb.getTaskDesc());
        modelHeader.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        modelHeader.setApplyTime(bioTaskDtlTb.getApplyTime() == null ? "" : DateUtil.format(bioTaskDtlTb.getApplyTime(), DatePattern.NORM_DATE_PATTERN));
        modelHeader.setTaskStatusName(BioTaskStatusEnum.getNameByStatus(bioTaskDtlTb.getTaskStatus()));
        modelHeader.setRefTaskNum(bioTaskDtlTb.getRefTaskNum());
        modelHeader.setPrintUser(SecurityContextHolder.getNickName());
        modelHeader.setPrintTime(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
        modelHeader.setQrCodeUrl(QrCodeUtil.toBase64DataUri(buildQrCodeContent(bioTaskDtlTb)));
        modelHeader.setQrCodeText("工单二维码");
        return modelHeader;
    }

    protected List<BioHtmlModelDTO.ModelBottom> getModelBottomList(BioTaskDtlTb bioTaskDtlTb) {
        List<BioHtmlModelDTO.ModelBottom> resultList = new ArrayList<>();
        if (bioTaskDtlTb.getInstanceId() == null) {
            return resultList;
        }
        ApproveDetailRspDTO approveDetailRspDTO = flowService.approveDetail(String.valueOf(bioTaskDtlTb.getInstanceId()));
        if (approveDetailRspDTO == null || CollectionUtil.isEmpty(approveDetailRspDTO.getModelList())) {
            return resultList;
        }
        for (ApproveDetailRspDTO.Model model : approveDetailRspDTO.getModelList()) {
            if (CollectionUtil.isEmpty(model.getNodeUserList())) {
                BioHtmlModelDTO.ModelBottom modelBottom = new BioHtmlModelDTO.ModelBottom();
                modelBottom.setNodeName(model.getNodeName());
                resultList.add(modelBottom);
                continue;
            }
            List<ApproveDetailRspDTO.NodeUser> nodeUserList = model.getNodeUserList().stream()
                    .sorted(Comparator.comparing(ApproveDetailRspDTO.NodeUser::getApproveTime, Comparator.nullsLast(Date::compareTo)))
                    .collect(Collectors.toList());
            for (ApproveDetailRspDTO.NodeUser nodeUser : nodeUserList) {
                BioHtmlModelDTO.ModelBottom modelBottom = new BioHtmlModelDTO.ModelBottom();
                modelBottom.setNodeName(model.getNodeName());
                modelBottom.setUsername(nodeUser.getUsername());
                modelBottom.setApproveResult(nodeUser.getApproveResult());
                modelBottom.setApproveRemark(nodeUser.getApproveRemark());
                modelBottom.setApproveTime(nodeUser.getApproveTime() == null ? "" : DateUtil.format(nodeUser.getApproveTime(), DatePattern.NORM_DATETIME_PATTERN));
                resultList.add(modelBottom);
            }
        }
        return resultList;
    }

    protected BioHtmlModelDTO.ModelSection buildFieldSection(String title, List<BioHtmlModelDTO.ModelField> fieldList) {
        BioHtmlModelDTO.ModelSection section = new BioHtmlModelDTO.ModelSection();
        section.setTitle(title);
        section.setType("field");
        section.setData(fieldList);
        return section;
    }

    protected BioHtmlModelDTO.ModelSection buildTableSection(String title, List<String> headers, List<java.util.Map<String, Object>> rows) {
        BioHtmlModelDTO.ModelTable table = new BioHtmlModelDTO.ModelTable();
        table.setHeaders(headers);
        table.setRows(rows);
        BioHtmlModelDTO.ModelSection section = new BioHtmlModelDTO.ModelSection();
        section.setTitle(title);
        section.setType("table");
        section.setData(table);
        return section;
    }

    protected BioHtmlModelDTO.ModelField buildField(String label, String value) {
        BioHtmlModelDTO.ModelField field = new BioHtmlModelDTO.ModelField();
        field.setLabel(label);
        field.setValue(value);
        return field;
    }

    private String buildQrCodeContent(BioTaskDtlTb bioTaskDtlTb) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("taskId=").append(bioTaskDtlTb.getId());
        if (StringUtils.isNotBlank(bioTaskDtlTb.getTaskNum())) {
            stringBuilder.append("&taskNum=").append(bioTaskDtlTb.getTaskNum());
        }
        if (StringUtils.isNotBlank(bioTaskDtlTb.getTaskTypeCode())) {
            stringBuilder.append("&taskType=").append(bioTaskDtlTb.getTaskTypeCode());
        }
        return stringBuilder.toString();
    }
}
