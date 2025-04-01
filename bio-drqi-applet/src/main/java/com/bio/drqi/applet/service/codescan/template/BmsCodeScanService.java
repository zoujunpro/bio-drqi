package com.bio.drqi.applet.service.codescan.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.applet.dto.rsp.ScanCodeBmsRspDTO;
import com.bio.drqi.applet.dto.rsp.ScanCodePlasmidRspDTO;
import com.bio.drqi.applet.service.codescan.AbstractBaseCodeScanService;
import com.bio.drqi.applet.service.codescan.dto.BmsUniqueCodeDTO;
import com.bio.drqi.applet.service.codescan.dto.PlasmidUniqueCodeDTO;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 质粒扫码（载体构建）
 */
@Service
@Slf4j
public class BmsCodeScanService extends AbstractBaseCodeScanService<BmsUniqueCodeDTO, ScanCodeBmsRspDTO> {

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Resource
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Override
    public BmsUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        BmsUniqueCodeDTO bmsUniqueCodeDTO = new BmsUniqueCodeDTO();
        bmsUniqueCodeDTO.setTaskNum(uniqueCodeArr[0]);
        bmsUniqueCodeDTO.setProductInnerCode(uniqueCodeArr[1]);
        bmsUniqueCodeDTO.setBatchNo(uniqueCodeArr[2]);
        bmsUniqueCodeDTO.setProduceDate(uniqueCodeArr[3]);

        return bmsUniqueCodeDTO;
    }


    @Override
    public ScanCodeBmsRspDTO dealCodeContent(BmsUniqueCodeDTO bmsUniqueCodeDTO) {
        BmsProductStockInLog bmsProductStockInLog = bmsProductStockInLogMapper.selectOneByTaskNumAndProductInnerCodeAndBatchNoAndProduceDate(bmsUniqueCodeDTO.getTaskNum(), bmsUniqueCodeDTO.getProductInnerCode(), bmsUniqueCodeDTO.getBatchNo(),bmsUniqueCodeDTO.getProduceDate());
        if (bmsProductStockInLog == null) {
            log.error("扫码失败，找不到数据，{}", JSONUtil.toJsonStr(bmsUniqueCodeDTO));
            throw new BusinessException("无此入库记录,任务订单号:" + bmsUniqueCodeDTO.getTaskNum() + " 商品编号:" + bmsUniqueCodeDTO.getProductInnerCode());
        }
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByProductInnerCodeAndUnitCodeAndBatchNoAndProduceDate(bmsProductStockInLog.getProductInnerCode(), bmsProductStockInLog.getUnitCode(), bmsProductStockInLog.getBatchNo(),bmsProductStockInLog.getProduceDate());
        if (bmsProductStockTb == null) {
            log.error("扫码数据异常，找不到数据，{}", JSONUtil.toJsonStr(bmsUniqueCodeDTO));
            throw new BusinessException("数据异常，库存中找不到数据,商品编号：" + bmsProductStockInLog.getProductInnerCode() + " 批次号：" + bmsProductStockInLog.getProductInnerCode());
        }
        ScanCodeBmsRspDTO scanCodeBmsRspDTO= BeanUtils.copyProperties(bmsProductStockTb, ScanCodeBmsRspDTO.class);
        return scanCodeBmsRspDTO;
    }


}


