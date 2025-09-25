package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.bsm.dto.BmsOrderDetailExcelDTO;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailQueryByOrderNumRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDtlDetailRspDTO;
import com.bio.drqi.bsm.service.BmsOrderDetailService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
public class BmsOrderDetailServiceImpl implements BmsOrderDetailService {

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;

    @Resource
    private BmsReturnOrderDetailTbMapper bmsReturnOrderDetailTbMapper;

    @Resource
    private BmsProductStockInLogMapper bmsProductStockInLogMapper;

    @Override
    public PageInfo<BmsOrderDetailListPageRspDTO> listPage(BmsOrderDetailListPageReqDTO bmsOrderDetailListPageReqDTO) {
        PageHelper.startPage(bmsOrderDetailListPageReqDTO.getPageNum(), bmsOrderDetailListPageReqDTO.getPageSize());
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectSelective(BeanUtils.copyProperties(bmsOrderDetailListPageReqDTO, BmsOrderDetailTb.class));
        PageInfo<BmsOrderDetailTb> srcPageInfo = new PageInfo<>(bmsOrderDetailTbList);

        return BeanUtils.copyPageInfoProperties(srcPageInfo, BmsOrderDetailListPageRspDTO.class);
    }

    @Override
    public BmsOrderDtlDetailRspDTO detail(Integer id) {
        BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectById(id);
        return BeanUtils.copyProperties(bmsOrderDetailTb, BmsOrderDtlDetailRspDTO.class);
    }

    @Override
    public List<BmsOrderDetailQueryByOrderNumRspDTO> queryByOrderNum(String orderNum) {
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectAllByOrderNum(orderNum);
        return BeanUtils.copyListProperties(bmsOrderDetailTbList, BmsOrderDetailQueryByOrderNumRspDTO.class);
    }

    @Override
    public void uploadContract(BmsOrderDetailUploadContractReqDTO bmsOrderDetailUploadContractReqDTO) {
        BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectById(bmsOrderDetailUploadContractReqDTO.getId());
        if (bmsOrderDetailTb == null) {
            log.error("订单不存在，orderDetailId={}", bmsOrderDetailUploadContractReqDTO.getId());
            throw new BusinessException("订单不存在");
        }
        bmsOrderDetailTb.setContractNumber(bmsOrderDetailUploadContractReqDTO.getContractNumber());
        bmsOrderDetailTb.setContractUrls(bmsOrderDetailUploadContractReqDTO.getContractUrls());
        bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
    }

    @Override
    public void uploadInvoice(BmsOrderDetailUploadInvoiceReqDTO bmsOrderDetailUploadInvoiceReqDTO) {
        BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectById(bmsOrderDetailUploadInvoiceReqDTO.getId());
        if (bmsOrderDetailTb == null) {
            log.error("订单不存在，orderDetailId={}", bmsOrderDetailUploadInvoiceReqDTO.getId());
            throw new BusinessException("订单不存在");
        }
        bmsOrderDetailTb.setInvoiceUrls(bmsOrderDetailUploadInvoiceReqDTO.getInvoiceUrls());
        bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
    }

    @Override
    public void reportAccount(BmsOrderDetailReportAccountReqDTO bmsOrderDetailReportAccountReqDTO) {
        BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectById(bmsOrderDetailReportAccountReqDTO.getId());

        if (bmsOrderDetailTb == null) {
            log.error("订单不存在，orderDetailId={}", bmsOrderDetailReportAccountReqDTO.getId());
            throw new BusinessException("订单不存在");
        }
        bmsOrderDetailTb.setReportAccountTime(bmsOrderDetailReportAccountReqDTO.getAccountTime());

        bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
    }

    @Override
    public void uploadPaymentVoucher(BmsOrderDetailUploadPaymentVoucherReqDTO bmsOrderDetailUploadPaymentVoucherReqDTO) {
        BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectById(bmsOrderDetailUploadPaymentVoucherReqDTO.getId());
        if (bmsOrderDetailTb == null) {
            log.error("订单不存在，orderDetailId={}", bmsOrderDetailUploadPaymentVoucherReqDTO.getId());
            throw new BusinessException("订单不存在");
        }
        bmsOrderDetailTb.setPaymentVoucherUrls(bmsOrderDetailUploadPaymentVoucherReqDTO.getPaymentVoucherUrls());

        bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
    }

    @Override
    public void taxRate(BmsOrderDetailTaxRateReqDTO bmsOrderDetailTaxRateReqDTO) {
        try {
            Double.valueOf(bmsOrderDetailTaxRateReqDTO.getTaxRate());
        } catch (NumberFormatException e) {
            throw new BusinessException("税率格式异常");
        }
        BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectById(bmsOrderDetailTaxRateReqDTO.getId());
        if (bmsOrderDetailTb == null) {
            throw new BusinessException("找不到此订单");
        }
        if (bmsOrderDetailTb.getReceiveNumber() != bmsOrderDetailTb.getPurchaseNumber().intValue()) {
            throw new BusinessException("耗材未全部到货");
        }

        bmsOrderDetailTb.setTaxRate(bmsOrderDetailTaxRateReqDTO.getTaxRate());
        bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);

        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectAllByOrderDetailNum(bmsOrderDetailTb.getOrderDetailNum());
        if (CollectionUtil.isNotEmpty(bmsReturnOrderDetailTbList)) {
            bmsReturnOrderDetailTbList.forEach(bmsReturnOrderDetailTb -> {
                bmsReturnOrderDetailTb.setTaxRate(bmsOrderDetailTaxRateReqDTO.getTaxRate());
                bmsReturnOrderDetailTbMapper.updateById(bmsReturnOrderDetailTb);
            });
        }

    }

    @Override
    public void exportExcel(BmsOrderDetailExportExcelReqDTO bmsOrderDetailExportExcelReqDTO, HttpServletResponse httpServletResponse) {
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectBatchIds(bmsOrderDetailExportExcelReqDTO.getIdList());
        List<BmsOrderDetailExcelDTO> bmsOrderDetailExcelDTOList = BeanUtils.copyListProperties(bmsOrderDetailTbList, BmsOrderDetailExcelDTO.class);
        ExcelUtil.writeExcel("订单明细" + DateUtil.format(new Date(), "yyyyMMddHHmmss") + ".xlsx", "sheet", bmsOrderDetailExcelDTOList, BmsOrderDetailExcelDTO.class, httpServletResponse);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void modifyPrice(BmsOrderDetailModifyPriceReqDTO bmsOrderDetailModifyPriceReqDTO) {
        BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectById(bmsOrderDetailModifyPriceReqDTO.getId());
        if (bmsOrderDetailTb == null) {
            throw new BusinessException("找不到此订单信息");
        }
        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectAllByOrderDetailNum(bmsOrderDetailTb.getOrderDetailNum());
        if (CollectionUtil.isNotEmpty(bmsProductStockInLogList) && bmsProductStockInLogList.stream().filter(bmsProductStockInLog -> bmsProductStockInLog.getKdNumber()!=null).collect(Collectors.toList()).size()>0) {
            throw new BusinessException("此订单已经入库且已经和金蝶进行过账务同步，无法更改");
        }

        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectAllByOrderDetailNum(bmsOrderDetailTb.getOrderDetailNum());
        if (CollectionUtil.isNotEmpty(bmsReturnOrderDetailTbList) && bmsReturnOrderDetailTbList.stream().filter(bmsReturnOrderDetailTb -> bmsReturnOrderDetailTb.getKdNumber()!=null).collect(Collectors.toList()).size()>0) {
            throw new BusinessException("此订单的退货订单已经同步到金蝶系统，无法更改");
        }

        //修改订单金额和总金额
        bmsOrderDetailTb.setPurchasePrice(bmsOrderDetailModifyPriceReqDTO.getPurchasePrice());
        bmsOrderDetailTb.setPayAmount(bmsOrderDetailTb.getPurchasePrice().multiply(new BigDecimal(bmsOrderDetailTb.getPurchaseNumber())));
        bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);


        //更新入库记录金额信息
        if (CollectionUtil.isNotEmpty(bmsProductStockInLogList)) {
            bmsProductStockInLogList.forEach(bmsProductStockInLog -> {
                bmsProductStockInLog.setProductPrice(bmsOrderDetailModifyPriceReqDTO.getPurchasePrice());
                bmsProductStockInLog.setStoreAmount(new BigDecimal(bmsProductStockInLog.getStoreNumber()).multiply(bmsProductStockInLog.getProductPrice()));
                bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
            });
        }

        //更新退回信息
        if(CollectionUtil.isNotEmpty(bmsReturnOrderDetailTbList)){
            bmsReturnOrderDetailTbList.forEach(bmsReturnOrderDetailTb -> {
                bmsReturnOrderDetailTb.setProductPrice(bmsOrderDetailModifyPriceReqDTO.getPurchasePrice());
                bmsReturnOrderDetailTb.setReturnAmount(bmsReturnOrderDetailTb.getProductPrice().multiply(new BigDecimal(bmsReturnOrderDetailTb.getReturnNumber())));
                bmsReturnOrderDetailTbMapper.updateById(bmsReturnOrderDetailTb);
            });
        }


    }
}
