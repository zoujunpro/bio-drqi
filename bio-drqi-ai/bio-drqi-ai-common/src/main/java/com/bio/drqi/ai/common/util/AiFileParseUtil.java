package com.bio.drqi.ai.common.util;

import com.bio.drqi.ai.common.enums.AiFileTypeEnum;
import com.bio.drqi.ai.common.model.AiFileParseResult;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * AI 聊天附件解析工具。
 *
 * <p>只负责根据文件地址和类型提取文本或结构化摘要，不包含业务逻辑。
 * fileType 可为空；为空时根据文件扩展名自动判断。</p>
 */
public final class AiFileParseUtil {

    private static final int MAX_EXCEL_ROWS_PER_SHEET = 200;

    private static final int MAX_EXCEL_COLUMNS_PER_ROW = 50;

    private AiFileParseUtil() {
    }

    public static AiFileParseResult parse(String filePath) {
        return parse(filePath, null);
    }

    public static AiFileParseResult parse(String filePath, String fileType) {
        AiFileParseResult result = initResult(filePath, fileType);
        try {
            if (!hasText(filePath)) {
                return fail(result, "文件地址不能为空");
            }

            File file = new File(filePath);
            if (!file.exists() || !file.isFile()) {
                return fail(result, "文件不存在或不是普通文件");
            }

            AiFileTypeEnum typeEnum = AiFileTypeEnum.resolve(fileType, file.getName());
            result.setFileType(typeEnum.getCode());

            if (AiFileTypeEnum.PDF == typeEnum) {
                result.setParsedText(parsePdf(file));
            } else if (AiFileTypeEnum.WORD == typeEnum) {
                result.setParsedText(parseWord(file));
            } else if (AiFileTypeEnum.EXCEL == typeEnum) {
                result.setParsedText(parseExcel(file));
            } else if (AiFileTypeEnum.TEXT == typeEnum) {
                result.setParsedText(parseText(file));
            } else if (AiFileTypeEnum.IMAGE == typeEnum) {
                result.setParsedText("");
                result.setSummary("图片文件已识别，OCR或视觉理解需要交给视觉模型处理。");
            } else {
                return fail(result, "暂不支持的文件类型");
            }

            result.setSuccess(Boolean.TRUE);
            if (!hasText(result.getSummary())) {
                result.setSummary(buildSummary(typeEnum, result.getParsedText()));
            }
            return result;
        } catch (Exception e) {
            return fail(result, e.getMessage());
        }
    }

    private static AiFileParseResult initResult(String filePath, String fileType) {
        AiFileParseResult result = new AiFileParseResult();
        result.setFilePath(filePath);
        result.setFileType(fileType);
        if (hasText(filePath)) {
            result.setFileName(new File(filePath).getName());
        }
        result.setSuccess(Boolean.FALSE);
        return result;
    }

    private static String parsePdf(File file) throws IOException {
        try (PDDocument document = PDDocument.load(file)) {
            PDFTextStripper stripper = new PDFTextStripper();
            return stripper.getText(document);
        }
    }

    private static String parseWord(File file) throws IOException {
        String lowerName = file.getName().toLowerCase();
        if (lowerName.endsWith(".docx")) {
            return parseDocx(file);
        }
        if (lowerName.endsWith(".doc")) {
            return parseDoc(file);
        }
        return "";
    }

    private static String parseDocx(File file) throws IOException {
        StringBuilder builder = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream(file);
             XWPFDocument document = new XWPFDocument(inputStream)) {
            for (XWPFParagraph paragraph : document.getParagraphs()) {
                appendLine(builder, paragraph.getText());
            }
            for (XWPFTable table : document.getTables()) {
                for (XWPFTableRow row : table.getRows()) {
                    StringBuilder rowBuilder = new StringBuilder();
                    for (XWPFTableCell cell : row.getTableCells()) {
                        if (rowBuilder.length() > 0) {
                            rowBuilder.append('\t');
                        }
                        rowBuilder.append(cell.getText());
                    }
                    appendLine(builder, rowBuilder.toString());
                }
            }
        }
        return builder.toString();
    }

    private static String parseDoc(File file) throws IOException {
        try (FileInputStream inputStream = new FileInputStream(file);
             HWPFDocument document = new HWPFDocument(inputStream);
             WordExtractor extractor = new WordExtractor(document)) {
            return extractor.getText();
        }
    }

    private static String parseExcel(File file) throws Exception {
        StringBuilder builder = new StringBuilder();
        try (FileInputStream inputStream = new FileInputStream(file);
             Workbook workbook = WorkbookFactory.create(inputStream)) {
            DataFormatter formatter = new DataFormatter();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                appendLine(builder, "Sheet: " + sheet.getSheetName());
                int rowCount = 0;
                for (Row row : sheet) {
                    if (rowCount >= MAX_EXCEL_ROWS_PER_SHEET) {
                        appendLine(builder, "... 当前Sheet超过" + MAX_EXCEL_ROWS_PER_SHEET + "行，已截断");
                        break;
                    }
                    appendLine(builder, formatExcelRow(row, formatter, evaluator));
                    rowCount++;
                }
            }
        }
        return builder.toString();
    }

    private static String formatExcelRow(Row row, DataFormatter formatter, FormulaEvaluator evaluator) {
        StringBuilder builder = new StringBuilder();
        short lastCellNum = row.getLastCellNum();
        int maxCellNum = Math.min(lastCellNum < 0 ? 0 : lastCellNum, MAX_EXCEL_COLUMNS_PER_ROW);
        for (int cellIndex = 0; cellIndex < maxCellNum; cellIndex++) {
            if (cellIndex > 0) {
                builder.append('\t');
            }
            Cell cell = row.getCell(cellIndex);
            if (cell != null) {
                builder.append(formatter.formatCellValue(cell, evaluator));
            }
        }
        return builder.toString();
    }

    private static String parseText(File file) throws IOException {
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    private static String buildSummary(AiFileTypeEnum fileTypeEnum, String parsedText) {
        int length = parsedText == null ? 0 : parsedText.length();
        return fileTypeEnum.getDesc() + "文件解析完成，提取文本长度：" + length;
    }

    private static AiFileParseResult fail(AiFileParseResult result, String errorMessage) {
        result.setSuccess(Boolean.FALSE);
        result.setErrorMessage(errorMessage);
        return result;
    }

    private static void appendLine(StringBuilder builder, String value) {
        if (hasText(value)) {
            builder.append(value).append('\n');
        }
    }

    private static boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
