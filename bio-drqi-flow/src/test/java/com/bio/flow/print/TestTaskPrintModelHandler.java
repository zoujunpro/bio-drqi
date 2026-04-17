package com.bio.flow.print;

import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.flow.dto.BioHtmlModelDTO;
import com.bio.flow.hander.DefaultBuildHtmlModelHandler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TestTaskPrintModelHandler extends DefaultBuildHtmlModelHandler {

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();

        List<BioHtmlModelDTO.ModelField> projectFields = new ArrayList<>();
        projectFields.add(buildField("项目名称", "玉米高产自交系筛选项目"));
        projectFields.add(buildField("项目编号", "PRJ-2026-001"));
        projectFields.add(buildField("项目类型", "常规项目"));
        projectFields.add(buildField("编辑类型", "育种"));
        projectFields.add(buildField("预计开始日期", "2026-05-01"));
        projectFields.add(buildField("项目分类", "大田作物"));
        sections.add(buildFieldSection("项目基本信息", projectFields));

        List<String> headers = new ArrayList<>();
        headers.add("序号");
        headers.add("子项目编号");
        headers.add("子项目名称");
        headers.add("负责人");
        headers.add("状态");
        headers.add("计划完成时间");
        List<Map<String, Object>> rows = new ArrayList<>();
        for (int i = 1; i <= 50; i++) {
            LinkedHashMap<String, Object> row = new LinkedHashMap<>();
            row.put("序号", i);
            row.put("子项目编号", "SUB-" + String.format("%03d", i));
            row.put("子项目名称", "育种阶段任务-" + i);
            row.put("负责人", i % 2 == 0 ? "邹军" : "谢孟");
            row.put("状态", i % 3 == 0 ? "进行中" : "未开始");
            row.put("计划完成时间", "2026-06-" + String.format("%02d", (i % 28) + 1));
            rows.add(row);
        }
        sections.add(buildTableSection("子项目计划明细", headers, rows));
        return sections;
    }
}
