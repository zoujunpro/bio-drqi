package com.bio.drqi.tc.service.excel;

import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataValidation;
import org.apache.poi.ss.usermodel.DataValidationConstraint;
import org.apache.poi.ss.usermodel.DataValidationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.util.CellRangeAddressList;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExcelSelectedWriteHandler implements CellWriteHandler {

    private final Map<String, ExcelSelected> selectedMap = new HashMap<>();

    public ExcelSelectedWriteHandler(Class<?> headClass) {
        initSelectedMap(headClass);
    }

    @Override
    public void beforeCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Row row,
                                 Head head, Integer columnIndex, Integer relativeRowIndex, Boolean isHead) {
    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder, Cell cell,
                                Head head, Integer relativeRowIndex, Boolean isHead) {
    }

    @Override
    public void afterCellDataConverted(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                       CellData cellData, Cell cell, Head head, Integer relativeRowIndex,
                                       Boolean isHead) {
    }

    @Override
    public void afterCellDispose(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                 List<CellData> cellDataList, Cell cell, Head head, Integer relativeRowIndex,
                                 Boolean isHead) {
        if (!Boolean.TRUE.equals(isHead) || head == null || selectedMap.isEmpty()) {
            return;
        }
        ExcelSelected excelSelected = selectedMap.get(head.getFieldName());
        if (excelSelected == null) {
            return;
        }
        Sheet sheet = writeSheetHolder.getSheet();
        DataValidationHelper helper = sheet.getDataValidationHelper();
        CellRangeAddressList addressList = new CellRangeAddressList(excelSelected.firstRow(), excelSelected.lastRow(),
                cell.getColumnIndex(), cell.getColumnIndex());
        DataValidationConstraint constraint = helper.createExplicitListConstraint(excelSelected.value());
        DataValidation validation = helper.createValidation(constraint, addressList);
        validation.setShowErrorBox(true);
        sheet.addValidationData(validation);
    }

    private void initSelectedMap(Class<?> clazz) {
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            for (Field field : currentClass.getDeclaredFields()) {
                ExcelSelected excelSelected = field.getAnnotation(ExcelSelected.class);
                if (excelSelected != null) {
                    selectedMap.put(field.getName(), excelSelected);
                }
            }
            currentClass = currentClass.getSuperclass();
        }
    }
}
