package com.bio.drqi.es.support.global.project;

import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.common.enums.CheckResultEnum;
import com.bio.drqi.common.enums.TestResultEnum;
import com.bio.drqi.enums.ProjectStatusEnum;
import com.bio.drqi.enums.ProjectTypeEnum;
import com.bio.drqi.enums.QualityInspectionResultEnum;
import com.bio.drqi.enums.TransGeneFlagEnum;
import com.bio.drqi.enums.VectorTaskStatusEnum;
import com.bio.drqi.es.support.global.AbstractGlobalSearchDocumentBuilder;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class AbstractProjectGlobalSearchDocumentBuilder extends AbstractGlobalSearchDocumentBuilder {

    @Override
    public String systemCode() {
        return "project";
    }

    protected Map<String, Object> buildDoc(Map<String, Object> row,
                                           String title,
                                           String summary,
                                           String route,
                                           Map<String, Object> display,
                                           Object... searchValues) {
        String id = stringValue(row.get("id"));
        Map<String, Object> doc = new LinkedHashMap<>();
        doc.put("system_code", systemCode());
        doc.put("biz_type", table());
        doc.put("biz_id", id);
        doc.put("title", title);
        doc.put("summary", summary);
        doc.put("search_content", join(searchValues));
        doc.put("route", route + id);
        doc.put("display", display);
        doc.put("create_time", row.get("create_time"));
        return doc;
    }

    protected Map<String, Object> display(Object... labelValues) {
        Map<String, Object> display = new LinkedHashMap<>();
        if (labelValues == null) {
            return display;
        }
        for (int i = 0; i + 1 < labelValues.length; i += 2) {
            display.put(stringValue(labelValues[i]), labelValues[i + 1]);
        }
        return display;
    }

    protected String projectStatusName(Object value) {
        String status = stringValue(value);
        if (!isEnumName(ProjectStatusEnum.class, status)) {
            return status;
        }
        switch (ProjectStatusEnum.valueOf(status)) {
            case approve:
                return "审批中";
            case execute:
                return "执行中";
            case stop:
                return "暂停";
            case compete:
                return "完成";
            default:
                return status;
        }
    }

    protected String projectTypeName(Object value) {
        String code = stringValue(value);
        for (ProjectTypeEnum projectTypeEnum : ProjectTypeEnum.values()) {
            if (projectTypeEnum.code.equals(code)) {
                return projectTypeEnum.name;
            }
        }
        return code;
    }

    protected String taskStatusName(Object value) {
        String status = stringValue(value);
        String vectorTaskStatusName = VectorTaskStatusEnum.getNameByStatus(status);
        if (notEmpty(vectorTaskStatusName)) {
            return vectorTaskStatusName;
        }
        String bioTaskStatusName = BioTaskStatusEnum.getNameByStatus(status);
        if (notEmpty(bioTaskStatusName)) {
            return bioTaskStatusName;
        }
        return status;
    }

    protected String qualityInspectionResultName(Object value) {
        String result = stringValue(value);
        if (!isEnumName(QualityInspectionResultEnum.class, result)) {
            return result;
        }
        switch (QualityInspectionResultEnum.valueOf(result)) {
            case nocheck:
                return "未质检";
            case pass:
                return "合格";
            case refuse:
                return "不合格";
            case checking:
                return "质检中";
            default:
                return result;
        }
    }

    protected String testResultName(Object value) {
        String result = stringValue(value);
        if (!isEnumName(TestResultEnum.class, result)) {
            return result;
        }
        switch (TestResultEnum.valueOf(result)) {
            case noTest:
                return "未检测";
            case noResult:
                return "无结果";
            case haveResult:
                return "有结果";
            default:
                return result;
        }
    }

    protected String checkResultName(Object value) {
        String result = stringValue(value);
        if (!isEnumName(CheckResultEnum.class, result)) {
            return result;
        }
        switch (CheckResultEnum.valueOf(result)) {
            case stay:
                return "保留";
            case remove:
                return "去除";
            case noCheck:
                return "未审核";
            default:
                return result;
        }
    }

    protected String transGeneFlagName(Object value) {
        String flag = stringValue(value);
        if (!isEnumName(TransGeneFlagEnum.class, flag)) {
            return flag;
        }
        switch (TransGeneFlagEnum.valueOf(flag)) {
            case Y:
                return "是";
            case N:
                return "否";
            case O:
                return "N/A";
            default:
                return flag;
        }
    }

    protected String confirmStatusName(Object value) {
        String status = stringValue(value);
        switch (status) {
            case "checked":
                return "已确认";
            case "none":
                return "未确认";
            default:
                return status;
        }
    }

    protected String matchFlagName(Object value) {
        String flag = stringValue(value);
        switch (flag) {
            case "Y":
                return "匹配";
            case "N":
                return "不匹配";
            default:
                return flag;
        }
    }

    protected String synResultName(Object value) {
        String result = stringValue(value);
        switch (result) {
            case "Y":
            case "success":
            case "SUCCESS":
                return "同步成功";
            case "N":
            case "fail":
            case "FAIL":
            case "FAILED":
                return "同步失败";
            default:
                return result;
        }
    }

    private <E extends Enum<E>> boolean isEnumName(Class<E> enumClass, String value) {
        if (!notEmpty(value)) {
            return false;
        }
        try {
            Enum.valueOf(enumClass, value);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private boolean notEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
