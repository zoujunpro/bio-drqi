package com.bio.flow.dto;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class BioHtmlModelDTO {
    private ModelHeader modelHeader;

    private List<ModelSection> sections;

    private List<ModelBottom> modelBottomList;

    @Data
    public static class ModelHeader {
        private String taskNum;
        private String taskTypeCode;
        private String taskTypeName;
        private String taskDesc;
        private String applyUserName;
        private String applyTime;
        private String taskStatusName;
        private String refTaskNum;
        private String printUser;
        private String printTime;
        private String qrCodeUrl;
        private String qrCodeText;
    }

    @Data
    public static class ModelSection {
        private String title;
        private String type;
        private Object data;
    }

    @Data
    public static class ModelField {
        private String label;
        private String value;
    }

    @Data
    public static class ModelTable {
        private List<String> headers;
        private List<Map<String, Object>> rows;
    }

    @Data
    public static class ModelBottom {
        private String nodeName;
        private String username;
        private String approveResult;
        private String approveRemark;
        private String approveTime;
    }
}
