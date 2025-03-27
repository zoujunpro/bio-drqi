package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.bean.BeanUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodeBmsRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodePlasmidRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.BmsUniqueCodeDTO;
import com.bio.drqi.applet.service.codescan.dto.PlasmidUniqueCodeDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 质粒扫码（载体构建）
 */
@Service
public class BmsCodeScanService extends AbstractBaseCodeScanService<BmsUniqueCodeDTO, ScanCodeBmsRspDTO> {

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Override
    public BmsUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        BmsUniqueCodeDTO bmsUniqueCodeDTO = new BmsUniqueCodeDTO();
        bmsUniqueCodeDTO.setProductInnerCode(uniqueCodeArr[0]);
        bmsUniqueCodeDTO.setBatchNo(uniqueCodeArr[1]);
        bmsUniqueCodeDTO.setTaskNum(uniqueCodeArr[2]);
        return bmsUniqueCodeDTO;
    }


    @Override
    public ScanCodeBmsRspDTO dealCodeContent(BmsUniqueCodeDTO bmsUniqueCodeDTO) {
        BmsProductStockInLog bmsProductStockInLog = bmsProductStockInLogMapper.selectOneByTaskNumAndProductInnerCodeAndBatchNo(bmsUniqueCodeDTO.getTaskNum(), bmsUniqueCodeDTO.getProductInnerCode(), bmsUniqueCodeDTO.getBatchNo());
        if (bmsProductStockInLog == null) {
            throw new BusinessException("无此入库记录,任务订单号:" + bmsUniqueCodeDTO.getTaskNum() + " 商品编号:" + bmsUniqueCodeDTO.getProductInnerCode());
        }
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByProductInnerCodeAndUnitCodeAndBatchNo(bmsProductStockInLog.getProductInnerCode(), bmsProductStockInLog.getUnitCode(), bmsProductStockInLog.getBatchNo());
        if(bmsProductStockTb==null){
        throw new BusinessException("数据异常，库存中找不到数据,商品编号："+bmsProductStockInLog.getProductInnerCode()+" 批次号："+bmsProductStockInLog.getProductInnerCode());
        }
        return BeanUtils.copyProperties(bmsProductStockInLog, ScanCodeBmsRspDTO.class);
    }


}


