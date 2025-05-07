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
    private BmsProductStockTbMapper bmsProductStockTbMapper;

    @Override
    public BmsUniqueCodeDTO parseUniqueCode(String uniqueCode) {
        String[] uniqueCodeArr = uniqueCode.split("\\|");
        BmsUniqueCodeDTO bmsUniqueCodeDTO = new BmsUniqueCodeDTO();
        bmsUniqueCodeDTO.setProductInnerCode(uniqueCodeArr[0]);
        bmsUniqueCodeDTO.setBatchNo(uniqueCodeArr[1]);
        bmsUniqueCodeDTO.setUnitCode(uniqueCodeArr[2]);
        return bmsUniqueCodeDTO;
    }


    @Override
    public ScanCodeBmsRspDTO dealCodeContent(BmsUniqueCodeDTO bmsUniqueCodeDTO) {
        BmsProductStockTb bmsProductStockTb = bmsProductStockTbMapper.selectOneByProductInnerCodeAndUnitCodeAndBatchNo(bmsUniqueCodeDTO.getProductInnerCode(), bmsUniqueCodeDTO.getBatchNo(), bmsUniqueCodeDTO.getUnitCode());
        if (bmsProductStockTb == null) {
            log.error("扫码失败，找不到库存数据，{}", JSONUtil.toJsonStr(bmsUniqueCodeDTO));
            throw new BusinessException("扫码失败，找不到库存数据:" + " 商品编号:" + bmsUniqueCodeDTO.getProductInnerCode()+"批次号："+bmsUniqueCodeDTO.getBatchNo()+"单位："+bmsUniqueCodeDTO.getUnitCode());
        }
        ScanCodeBmsRspDTO scanCodeBmsRspDTO= BeanUtils.copyProperties(bmsProductStockTb, ScanCodeBmsRspDTO.class);
        return scanCodeBmsRspDTO;
    }


}


