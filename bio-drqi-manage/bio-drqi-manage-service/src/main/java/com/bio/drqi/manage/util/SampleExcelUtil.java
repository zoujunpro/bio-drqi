package com.bio.drqi.manage.util;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.drqi.manage.base.SampleUnitDTO;
import com.bio.common.core.util.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFCell;
import org.apache.poi.xssf.streaming.SXSSFRow;
import org.apache.poi.xssf.streaming.SXSSFSheet;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.List;

public class SampleExcelUtil {

    public static void createExcel(String applyNo, List<List<List<SampleUnitDTO>>> layoutList, List<SampleUnitDTO> singleList, HttpServletResponse response, String fileName) {
        SXSSFWorkbook workbook = createExcel(applyNo, layoutList, singleList);

        ServletOutputStream out = null;
        try {
            out = response.getOutputStream();
            response.setContentType("application/vnd.ms-excel");
            response.setCharacterEncoding("utf-8");
            response.setHeader("Content-disposition", "attachment;filename=" + URLEncoder.encode(fileName, "UTF-8"));
            workbook.write(out);
        } catch (IOException var14) {
            throw new RuntimeException(var14);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException var13) {
                    throw new RuntimeException(var13);
                }
            }

        }


    }

    private static SXSSFWorkbook createExcel(String applyNo, List<List<List<SampleUnitDTO>>> layoutList, List<SampleUnitDTO> singleList) {
        SXSSFWorkbook workbook = new SXSSFWorkbook();
        if (CollectionUtil.isNotEmpty(layoutList)) {
            initNinetySixByLayoutSheet(applyNo, layoutList, workbook);
            initNinetySixByRowSheet(applyNo, layoutList, workbook);
        }
        if (CollectionUtil.isNotEmpty(singleList)) {
            initSingleSheet(singleList, workbook);
        }
        return workbook;

    }

    private static void createExcel(String applyNo, List<List<List<SampleUnitDTO>>> layoutList, List<SampleUnitDTO> singleList, String path) {
        SXSSFWorkbook workbook = createExcel(applyNo, layoutList, singleList);
        FileOutputStream output = null;
        try {
            File target = new File(path);
            output = new FileOutputStream(target);
            workbook.write(output);
            output.flush();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        }
    }

    private static void initSingleSheet(List<SampleUnitDTO> singleList, SXSSFWorkbook workbook) {
        SXSSFSheet sheet = workbook.createSheet("单管数据");
        CellStyle headCellStyle = getHeadHSSFCellStyle(workbook);
        CellStyle cellStyle = getBodyHSSFCellStyle(workbook);

        SXSSFRow headRow = sheet.createRow(0);
        //实施方案编号
        SXSSFCell headCell1 = headRow.createCell(0);
        headCell1.setCellStyle(headCellStyle);
        headCell1.setCellValue("实施方案编号");
        //取样编号
        SXSSFCell headCell2 = headRow.createCell(1);
        headCell2.setCellStyle(headCellStyle);
        headCell2.setCellValue("取样编号");
        for (int i = 0; i < singleList.size(); i++) {
            SampleUnitDTO sampleUnitDTO = singleList.get(i);
            if (sampleUnitDTO != null && !sampleUnitDTO.ifNull()) {
                SXSSFRow row = sheet.createRow(i + 1);

                //实施方案编号
                SXSSFCell cell1 = row.createCell(0);
                cell1.setCellValue(sampleUnitDTO.getTransFormCode());
                cell1.setCellStyle(cellStyle);

                //取样编号
                SXSSFCell cell2 = row.createCell(1);
                cell2.setCellValue(sampleUnitDTO.getSampleCode());
                cell2.setCellStyle(cellStyle);
            }
        }
    }

    private static void initNinetySixByRowSheet(String applyNo, List<List<List<SampleUnitDTO>>> layoutList, SXSSFWorkbook workbook) {
        CellStyle cellStyle = getBodyHSSFCellStyle(workbook);
        SXSSFSheet sheet = workbook.createSheet("96孔板（列表排布）");
        CellStyle headCellStyle = getHeadHSSFCellStyle(workbook);
        //按列更新数据
        int index=0;
        //行表头-96表头
        SXSSFRow headRow = sheet.createRow(index);
        SXSSFCell headCell1 = headRow.createCell(0);
        headCell1.setCellValue("96孔板号");
        headCell1.setCellStyle(headCellStyle);
        SXSSFCell headCell2 = headRow.createCell(1);
        headCell2.setCellValue("实施方案编号");
        headCell2.setCellStyle(headCellStyle);
        SXSSFCell headCell3 = headRow.createCell(2);
        headCell3.setCellValue("取样编号");
        headCell3.setCellStyle(headCellStyle);
        for (int i = 0; i < layoutList.size(); i++) {

            List<List<SampleUnitDTO>> layout = layoutList.get(i);
            for (List<SampleUnitDTO> rowDaTa : layout) {
                for (SampleUnitDTO cellData : rowDaTa) {
                    if (cellData != null && !cellData.ifNull()) {
                        SXSSFRow row = sheet.createRow(++index);
                        SXSSFCell cell1 = row.createCell(0);
                        cell1.setCellValue(applyNo + "-" + StringUtils.padl(String.valueOf(i+1), 2, '0'));
                        cell1.setCellStyle(cellStyle);

                        SXSSFCell cell2 = row.createCell(1);
                        cell2.setCellValue(cellData.getVectorTaskCode());
                        cell2.setCellStyle(cellStyle);

                        SXSSFCell cell3 = row.createCell(2);
                        cell3.setCellValue(cellData.getSampleCode());
                        cell3.setCellStyle(cellStyle);
                    }

                }
            }
        }
    }

    private static void initNinetySixByLayoutSheet(String applyNo, List<List<List<SampleUnitDTO>>> layoutList, SXSSFWorkbook workbook) {
        SXSSFSheet sheet = workbook.createSheet("96孔板（孔板排布）");
        CellStyle cellStyle = getBodyHSSFCellStyle(workbook);
        for (int layoutIndex = 0; layoutIndex < layoutList.size(); layoutIndex++) {
            creatNinetySixHead(applyNo, sheet, workbook, layoutIndex * 10);
            List<List<SampleUnitDTO>> layout = layoutList.get(layoutIndex);
            //遍历孔板每一行
            for (int rowIndex = 0; rowIndex < layout.size(); rowIndex++) {
                SXSSFRow row = sheet.createRow(layoutIndex * 10 + rowIndex + 2);
                List<SampleUnitDTO> layoutRow = layout.get(rowIndex);
                //遍历孔板每一行的单元格
                for (int cellIndex = 0; cellIndex < layoutRow.size(); cellIndex++) {
                    SampleUnitDTO cellValue = layoutRow.get(cellIndex);
                    SXSSFCell cell = row.createCell(cellIndex);
                    if (cellValue != null && StringUtils.isNotEmpty(cellValue.getSampleCode())) {
                        cell.setCellValue(cellValue.getSampleCode());
                    }
                    cell.setCellStyle(cellStyle);
                }
            }
        }
    }


    private static void creatNinetySixHead(String applyNo, SXSSFSheet sheet, SXSSFWorkbook workbook, Integer rowNum) {
        //设置样式
        CellStyle cellStyle = getHeadHSSFCellStyle(workbook);
        SXSSFRow titleRow = sheet.createRow(rowNum);
        SXSSFCell titleCell1 = titleRow.createCell(0);
        SXSSFCell titleCell2 = titleRow.createCell(1);
        SXSSFCell titleCell3 = titleRow.createCell(2);
        SXSSFCell titleCell4 = titleRow.createCell(3);
        SXSSFCell titleCell5 = titleRow.createCell(4);
        SXSSFCell titleCell6 = titleRow.createCell(5);
        SXSSFCell titleCell7 = titleRow.createCell(6);
        SXSSFCell titleCell8 = titleRow.createCell(7);
        SXSSFCell titleCell9 = titleRow.createCell(8);
        SXSSFCell titleCell10 = titleRow.createCell(9);
        SXSSFCell titleCell11 = titleRow.createCell(10);
        SXSSFCell titleCell12 = titleRow.createCell(11);
        titleCell1.setCellValue(applyNo + "-" + StringUtils.padl(String.valueOf((rowNum / 10 + 1)), 2, '0'));
        titleCell1.setCellStyle(cellStyle);
        titleCell2.setCellStyle(cellStyle);
        titleCell3.setCellStyle(cellStyle);
        titleCell4.setCellStyle(cellStyle);
        titleCell5.setCellStyle(cellStyle);
        titleCell6.setCellStyle(cellStyle);
        titleCell7.setCellStyle(cellStyle);
        titleCell8.setCellStyle(cellStyle);
        titleCell9.setCellStyle(cellStyle);
        titleCell10.setCellStyle(cellStyle);
        titleCell11.setCellStyle(cellStyle);
        titleCell12.setCellStyle(cellStyle);

        //生成列
        sheet.addMergedRegion(new CellRangeAddress(rowNum, rowNum, 0, 11));


        SXSSFRow headRow = sheet.createRow(rowNum + 1);
        //生成列
        SXSSFCell cell1 = headRow.createCell(0);
        SXSSFCell cell2 = headRow.createCell(1);
        SXSSFCell cell3 = headRow.createCell(2);
        SXSSFCell cell4 = headRow.createCell(3);
        SXSSFCell cell5 = headRow.createCell(4);
        SXSSFCell cell6 = headRow.createCell(5);
        SXSSFCell cell7 = headRow.createCell(6);
        SXSSFCell cell8 = headRow.createCell(7);
        SXSSFCell cell9 = headRow.createCell(8);
        SXSSFCell cell10 = headRow.createCell(9);
        SXSSFCell cell11 = headRow.createCell(10);
        SXSSFCell cell12 = headRow.createCell(11);

        cell1.setCellStyle(cellStyle);
        cell2.setCellStyle(cellStyle);
        cell3.setCellStyle(cellStyle);
        cell4.setCellStyle(cellStyle);
        cell5.setCellStyle(cellStyle);
        cell6.setCellStyle(cellStyle);
        cell7.setCellStyle(cellStyle);
        cell8.setCellStyle(cellStyle);
        cell9.setCellStyle(cellStyle);
        cell10.setCellStyle(cellStyle);
        cell11.setCellStyle(cellStyle);
        cell12.setCellStyle(cellStyle);

        //赋值
        cell1.setCellValue("1");
        cell2.setCellValue("2");
        cell3.setCellValue("3");
        cell4.setCellValue("4");
        cell5.setCellValue("5");
        cell6.setCellValue("6");
        cell7.setCellValue("7");
        cell8.setCellValue("7");
        cell9.setCellValue("8");
        cell10.setCellValue("10");
        cell11.setCellValue("11");
        cell12.setCellValue("12");
    }

    public static CellStyle getHeadHSSFCellStyle(SXSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle(); // 单元格样式
        //字体样式
        Font fontStyle = workbook.createFont();
        fontStyle.setBold(false); // 加粗
        fontStyle.setFontName("黑体"); // 字体
        fontStyle.setFontHeightInPoints((short) 11); // 大小
        fontStyle.setColor(Font.COLOR_NORMAL);//颜色
        //字体样式添加到单元格样式中
        cellStyle.setFont(fontStyle);


        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);

        //单元格风格
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//左右居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中

        //单元格背景色
        cellStyle.setFillForegroundColor(IndexedColors.GREEN.getIndex());
        cellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        return cellStyle;
    }

    public static CellStyle getBodyHSSFCellStyle(SXSSFWorkbook workbook) {
        CellStyle cellStyle = workbook.createCellStyle(); // 单元格样式
        //字体样式
        Font fontStyle = workbook.createFont();
        fontStyle.setBold(false); // 加粗
        fontStyle.setFontName("黑体"); // 字体
        fontStyle.setFontHeightInPoints((short) 11); // 大小
        fontStyle.setColor(Font.COLOR_NORMAL);//颜色
        //字体样式添加到单元格样式中
        cellStyle.setFont(fontStyle);


        cellStyle.setBorderBottom(BorderStyle.THIN);
        cellStyle.setBorderLeft(BorderStyle.THIN);
        cellStyle.setBorderRight(BorderStyle.THIN);
        cellStyle.setBorderBottom(BorderStyle.THIN);

        //单元格风格
        cellStyle.setAlignment(HorizontalAlignment.CENTER);//左右居中
        cellStyle.setVerticalAlignment(VerticalAlignment.CENTER);//垂直居中
        return cellStyle;
    }

}
