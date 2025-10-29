package com.bio.drqi.bsm.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.dto.BmsOrderDetailExcelDTO;
import com.bio.drqi.bsm.req.*;
import com.bio.drqi.bsm.rsp.BmsOrderDetailListPageRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDetailQueryByOrderNumRspDTO;
import com.bio.drqi.bsm.rsp.BmsOrderDtlDetailRspDTO;
import com.bio.drqi.bsm.service.BmsOrderDetailService;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.easyflow.engine.FlowEngineService;
import com.easyflow.engine.entity.FlowEntity;
import com.easyflow.engine.entity.FlowHisCommitTb;
import com.easyflow.mybatis.mapper.FlowHisCommitTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Resource
    private FlowEngineService flowEngineService;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

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
    @Transactional(rollbackFor = Exception.class)
    public void uploadContract(BmsOrderDetailUploadContractReqDTO bmsOrderDetailUploadContractReqDTO) {
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectBatchIds(bmsOrderDetailUploadContractReqDTO.getIdList());
        bmsOrderDetailTbList.forEach(bmsOrderDetailTb -> {
            List<String> contractUrlList = new ArrayList<>();
            if (StringUtils.isNotEmpty(bmsOrderDetailTb.getOrderDetailNum())) {
                contractUrlList = JSONUtil.toList(bmsOrderDetailTb.getContractUrls(), String.class);
            }
            contractUrlList.add(bmsOrderDetailUploadContractReqDTO.getContractUrl());
            bmsOrderDetailTb.setContractNumber(bmsOrderDetailUploadContractReqDTO.getContractNumber());
            bmsOrderDetailTb.setContractUrls(JSONUtil.toJsonStr(contractUrlList));
            bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
        });
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteContract(BmsOrderDetailDeleteContractReqDTO bmsOrderDetailDeleteContractReqDTO) {
        BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectById(bmsOrderDetailDeleteContractReqDTO.getId());
        if (bmsOrderDetailTb == null) {
            throw new BusinessException("找不到订单信息");
        }
        List<String> contractUrlList = JSONUtil.toList(bmsOrderDetailTb.getContractUrls(), String.class);
        if (CollectionUtil.isNotEmpty(contractUrlList) && contractUrlList.contains(bmsOrderDetailDeleteContractReqDTO.getContractUrl().trim())) {
            contractUrlList = contractUrlList.stream().map(contractUrl -> contractUrl.trim()).collect(Collectors.toList());
            contractUrlList.remove(bmsOrderDetailDeleteContractReqDTO.getContractUrl());
        }
        bmsOrderDetailTb.setContractUrls(JSONUtil.toJsonStr(contractUrlList));
        bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadInvoice(BmsOrderDetailUploadInvoiceReqDTO bmsOrderDetailUploadInvoiceReqDTO) {
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectBatchIds(bmsOrderDetailUploadInvoiceReqDTO.getIdList());
        if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
            if (bmsOrderDetailTbList.stream().map(BmsOrderDetailTb::getOrderNum).distinct().collect(Collectors.toList()).size() != 1) {
                throw new BusinessException("只有同一个订单下可以批次上传发票");
            }
        }
        if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
            bmsOrderDetailTbList.forEach(bmsOrderDetailTb -> {
                List<String> invoiceUrlList = new ArrayList<>();
                if (StringUtils.isNotEmpty(bmsOrderDetailTb.getInvoiceUrls())) {
                    invoiceUrlList = JSONUtil.toList(bmsOrderDetailTb.getInvoiceUrls(), String.class);
                }
                invoiceUrlList.add(bmsOrderDetailUploadInvoiceReqDTO.getInvoiceUrl().trim());
                bmsOrderDetailTb.setInvoiceUrls(JSONUtil.toJsonStr(invoiceUrlList));
                bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
            });
        }

    }

    @Override
    public void deleteInvoice(BmsOrderDetailDeleteInvoiceReqDTO bmsOrderDetailDeleteInvoiceReqDTO) {
        BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectById(bmsOrderDetailDeleteInvoiceReqDTO.getId());
        if (bmsOrderDetailTb == null) {
            throw new BusinessException("订单不存在");
        }
        List<String> invoiceUrlList = JSONUtil.toList(bmsOrderDetailTb.getInvoiceUrls(), String.class);
        if (CollectionUtil.isNotEmpty(invoiceUrlList)) {
            invoiceUrlList = invoiceUrlList.stream().map(invoiceUrl -> invoiceUrl.trim()).collect(Collectors.toList());
            invoiceUrlList.remove(bmsOrderDetailDeleteInvoiceReqDTO.getInvoiceUrl().trim());
            bmsOrderDetailTb.setInvoiceUrls(JSONUtil.toJsonStr(invoiceUrlList));
            bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
        }


    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportAccount(BmsOrderDetailReportAccountReqDTO bmsOrderDetailReportAccountReqDTO) {
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectBatchIds(bmsOrderDetailReportAccountReqDTO.getIdList());
        if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
            bmsOrderDetailTbList.forEach(bmsOrderDetailTb -> {
                bmsOrderDetailTb.setReportAccountTime(bmsOrderDetailReportAccountReqDTO.getAccountTime());
                bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
            });
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void uploadPaymentVoucher(BmsOrderDetailUploadPaymentVoucherReqDTO bmsOrderDetailUploadPaymentVoucherReqDTO) {
        List<BmsOrderDetailTb> bmsOrderDetailTbList = bmsOrderDetailTbMapper.selectBatchIds(bmsOrderDetailUploadPaymentVoucherReqDTO.getIdList());
        if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
            if (bmsOrderDetailTbList.stream().map(BmsOrderDetailTb::getOrderNum).distinct().collect(Collectors.toList()).size() != 1) {
                throw new BusinessException("只有同一个订单下可以批次上传结算凭证");
            }
        }
        if (CollectionUtil.isNotEmpty(bmsOrderDetailTbList)) {
            bmsOrderDetailTbList.forEach(bmsOrderDetailTb -> {
                List<String> paymentVoucherUrlList = new ArrayList<>();
                paymentVoucherUrlList = JSONUtil.toList(bmsOrderDetailTb.getPaymentVoucherUrls(), String.class);
                paymentVoucherUrlList.add(bmsOrderDetailUploadPaymentVoucherReqDTO.getPaymentVoucherUrl());
                bmsOrderDetailTb.setPaymentVoucherUrls(JSONUtil.toJsonStr(paymentVoucherUrlList));
                bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
            });
        }

    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deletePaymentVoucher(BmsOrderDetailDeletePaymentVoucherReqDTO bmsOrderDetailDeletePaymentVoucherReqDTO) {
        BmsOrderDetailTb bmsOrderDetailTb = bmsOrderDetailTbMapper.selectById(bmsOrderDetailDeletePaymentVoucherReqDTO.getId());
        if (bmsOrderDetailTb == null) {
            throw new BusinessException("找不到订单信息");
        }
        List<String> paymentVoucherUrlList = JSONUtil.toList(bmsOrderDetailTb.getPaymentVoucherUrls(), String.class);
        if (CollectionUtil.isNotEmpty(paymentVoucherUrlList)) {
            paymentVoucherUrlList = paymentVoucherUrlList.stream().map(paymentVoucherUrl -> paymentVoucherUrl.trim()).collect(Collectors.toList());
            paymentVoucherUrlList.remove(bmsOrderDetailDeletePaymentVoucherReqDTO.getPaymentVoucherUrl().trim());
            bmsOrderDetailTb.setPaymentVoucherUrls(JSONUtil.toJsonStr(paymentVoucherUrlList));
            bmsOrderDetailTbMapper.updateById(bmsOrderDetailTb);
        }


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
        //    if (bmsOrderDetailTb.getReceiveNumber() != bmsOrderDetailTb.getPurchaseNumber().intValue()) {
        //      throw new BusinessException("耗材未全部到货");
        //}

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
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(bmsOrderDetailTb.getTaskNum());
        if (bioTaskDtlTb == null) {
            throw new BusinessException("数据异常，找不到此发起工单");
        }

        List<FlowHisCommitTb> flowHisCommitTbList = flowEngineService.getQueryService().getFlowCommitTbByInstanceId(bioTaskDtlTb.getInstanceId());
        List<String> userIdList = flowHisCommitTbList.stream().map(FlowEntity::getCreateId).collect(Collectors.toList());
        if (SecurityContextHolder.getUserId().intValue() != bmsOrderDetailTb.getApplyUserId() && !userIdList.contains(SecurityContextHolder.getUserId().toString())) {
            throw new BusinessException("只有审批参与人或者发起人可以修改");
        }

        List<BmsProductStockInLog> bmsProductStockInLogList = bmsProductStockInLogMapper.selectAllByOrderDetailNum(bmsOrderDetailTb.getOrderDetailNum());
        if (CollectionUtil.isNotEmpty(bmsProductStockInLogList) && bmsProductStockInLogList.stream().filter(bmsProductStockInLog -> bmsProductStockInLog.getKdNumber() != null).collect(Collectors.toList()).size() > 0) {
            throw new BusinessException("此订单已经入库且已经和金蝶进行过账务同步，无法更改");
        }

        List<BmsReturnOrderDetailTb> bmsReturnOrderDetailTbList = bmsReturnOrderDetailTbMapper.selectAllByOrderDetailNum(bmsOrderDetailTb.getOrderDetailNum());
        if (CollectionUtil.isNotEmpty(bmsReturnOrderDetailTbList) && bmsReturnOrderDetailTbList.stream().filter(bmsReturnOrderDetailTb -> bmsReturnOrderDetailTb.getKdNumber() != null).collect(Collectors.toList()).size() > 0) {
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
                bmsProductStockInLogMapper.updateById(bmsProductStockInLog);
            });
        }

        //更新退回信息
        if (CollectionUtil.isNotEmpty(bmsReturnOrderDetailTbList)) {
            bmsReturnOrderDetailTbList.forEach(bmsReturnOrderDetailTb -> {
                bmsReturnOrderDetailTb.setProductPrice(bmsOrderDetailModifyPriceReqDTO.getPurchasePrice());
                bmsReturnOrderDetailTb.setReturnAmount(bmsReturnOrderDetailTb.getProductPrice().multiply(new BigDecimal(bmsReturnOrderDetailTb.getReturnNumber())));
                bmsReturnOrderDetailTbMapper.updateById(bmsReturnOrderDetailTb);
            });
        }


    }
}
