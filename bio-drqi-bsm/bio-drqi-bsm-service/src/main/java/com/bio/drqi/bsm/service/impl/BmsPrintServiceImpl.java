package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.drqi.bsm.req.BmsPrintProductLabelReqDTO;
import com.bio.drqi.bsm.service.BmsPrintService;
import com.bio.drqi.domain.BmsProductStockInLog;
import com.bio.drqi.domain.BmsProductStockTb;
import com.bio.drqi.enums.SeedMaterialTypeEnum;
import com.bio.drqi.manage.base.PrintRspDTO;
import com.bio.drqi.mapper.BmsProductStockInLogMapper;
import com.bio.drqi.mapper.BmsProductStockTbMapper;
import com.bio.print.api.PrintApi;
import com.bio.print.req.PrintDataReqDTO;
import com.bio.print.rsp.BmsLabelPrintDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class BmsPrintServiceImpl implements BmsPrintService {


    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Resource
    private PrintApi printApi;

    @Override
    public PrintRspDTO productLabel(BmsPrintProductLabelReqDTO bmsPrintProductLabelReqDTO) {
        List<BmsLabelPrintDTO> bmsLabelPrintDTOList = new ArrayList<>();
        for (BmsPrintProductLabelReqDTO.Content content : bmsPrintProductLabelReqDTO.getContentList()) {
            BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByProductInnerCodeAndUnitCodeAndBatchNo(content.getProductInnerCode(),content.getUnitCode(),content.getBatchNo());
            if (bmsProductStockTb == null) {
                log.error("本条要打印组装的数据content={}", JSONUtil.toJsonStr(content));
                throw new BusinessException("找不到打印数据");
            }
            BmsLabelPrintDTO bmsLabelPrintDTO = new BmsLabelPrintDTO();
            bmsLabelPrintDTO.setSupplierCode(bmsProductStockTb.getSupplierCode());
            bmsLabelPrintDTO.setProductInnerCode(bmsProductStockTb.getProductInnerCode());
            bmsLabelPrintDTO.setBatchNo(bmsProductStockTb.getBatchNo());
            bmsLabelPrintDTO.setPrintNum(content.getPrintNum());
            bmsLabelPrintDTO.setTaskNum(bmsPrintProductLabelReqDTO.getTaskNum());
            bmsLabelPrintDTO.setExpirationDate(bmsProductStockTb.getExpirationDate());
            bmsLabelPrintDTO.setUnitCode(content.getUnitCode());
            bmsLabelPrintDTO.setProduceDate(bmsProductStockTb.getProduceDate());
            bmsLabelPrintDTOList.add(bmsLabelPrintDTO);
        }
        if (CollectionUtil.isNotEmpty(bmsLabelPrintDTOList)) {
            PrintRspDTO printRspDTO = new PrintRspDTO();
            printRspDTO.setPrintName(SeedMaterialTypeEnum.TYPE_3.printName);
            printRspDTO.setPrintDataList(printDataSave("bms_label_print", bmsLabelPrintDTOList));
            return printRspDTO;
        } else {
            return null;
        }
    }

    private List<String> printDataSave(String printType, Object printData) {
        PrintDataReqDTO printDataReqDTO = new PrintDataReqDTO();
        printDataReqDTO.setPrintType(printType);
        printDataReqDTO.setPrintData(JSONUtil.toJsonStr(printData));
        ResponseResult<List<String>> responseResult = printApi.printDataSave(printDataReqDTO);
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        return responseResult.getData();
    }
}
