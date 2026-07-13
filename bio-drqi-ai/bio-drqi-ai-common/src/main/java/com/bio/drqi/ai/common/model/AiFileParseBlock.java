package com.bio.drqi.ai.common.model;

/**
 * 文件解析后的结构化文本块。
 * 一个 block 表示解析层识别到的一段相对完整内容，例如 PDF 某一页、Word 段落、Word 表格行、Excel 某一行。
 */
public class AiFileParseBlock {

    /**
     * 文本内容。后续会作为 RAG 分块和 embedding 的基础文本。
     */
    private String text;

    /**
     * 文本块类型。取值来自 AiDocumentChunkTypeEnum，例如 TEXT、TABLE、MIXED。
     */
    private String blockType;

    /**
     * 所属章节标题。Word 标题、Markdown 标题、Excel Sheet 名称都可以映射到这里。
     */
    private String sectionTitle;

    /**
     * PDF 页码。从 1 开始；非分页文件为空。
     */
    private Integer pageNo;

    /**
     * Excel Sheet 名称。非 Excel 文件为空。
     */
    private String sheetName;

    /**
     * Excel 行号。从 1 开始；非 Excel 行数据为空。
     */
    private Integer rowNo;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBlockType() {
        return blockType;
    }

    public void setBlockType(String blockType) {
        this.blockType = blockType;
    }

    public String getSectionTitle() {
        return sectionTitle;
    }

    public void setSectionTitle(String sectionTitle) {
        this.sectionTitle = sectionTitle;
    }

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public String getSheetName() {
        return sheetName;
    }

    public void setSheetName(String sheetName) {
        this.sheetName = sheetName;
    }

    public Integer getRowNo() {
        return rowNo;
    }

    public void setRowNo(Integer rowNo) {
        this.rowNo = rowNo;
    }
}
