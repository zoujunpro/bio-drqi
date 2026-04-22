package com.bio.drqi.tc.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.flow.dto.BioHtmlModelDTO;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service("tc_test_task_apply")
public class TcTestTaskService  extends AbstractTcBaseTaskService{
    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {


    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        if (StringUtils.isEmpty(bioTaskDtlTb.getTaskForm()) || !JSONUtil.isTypeJSON(bioTaskDtlTb.getTaskForm())) {
            return Collections.emptyList();
        }

        Map<String, Object> formMap = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), LinkedHashMap.class);
        if (CollectionUtil.isEmpty(formMap)) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        formMap.forEach((key, value) -> {
            if (value instanceof String || value instanceof Number || value instanceof Boolean) {
                applyFields.add(buildField(key, String.valueOf(value)));
            }
        });
        if (applyFields.isEmpty()) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        sections.add(buildFieldSection("申请信息", applyFields));
        return sections;
    }
}
