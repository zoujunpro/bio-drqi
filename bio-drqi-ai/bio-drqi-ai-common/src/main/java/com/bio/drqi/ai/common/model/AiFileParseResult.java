package com.bio.drqi.ai.common.model;

import java.util.List;

/**
 * AI 聊天附件解析结果。
 */
public class AiFileParseResult {

    private String filePath;

    private String fileName;

    private String fileType;

    private String parsedText;

    /**
     * 结构化解析块列表。
     * 这里用 List 是因为一个文件解析后通常不是单段内容：
     * PDF 会按页产生多个块，Word 会按段落和表格产生多个块，Excel 会按 Sheet 和行产生多个块。
     * RAG 入库时优先使用这些块做分片，可以保留页码、Sheet、章节、表格类型等上下文。
     */
    private List<AiFileParseBlock> parseBlocks;

    private String summary;

    private Boolean success;

    private String errorMessage;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getFileType() {
        return fileType;
    }

    public void setFileType(String fileType) {
        this.fileType = fileType;
    }

    public String getParsedText() {
        return parsedText;
    }

    public void setParsedText(String parsedText) {
        this.parsedText = parsedText;
    }

    public List<AiFileParseBlock> getParseBlocks() {
        return parseBlocks;
    }

    public void setParseBlocks(List<AiFileParseBlock> parseBlocks) {
        this.parseBlocks = parseBlocks;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
