package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.dto.BmsPurchaseOrderDTO;
import com.bio.drqi.bsm.enums.PurchaseUnitEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import java.util.Date;

@Service
public class BmsPurchaseOrderTaskService extends AbstractBsmBaseTaskService {

    @Resource
    private BmsOrderTbMapper bmsOrderTbMapper;

    @Resource
    private BmsOrderDetailTbMapper bmsOrderDetailTbMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        BmsPurchaseOrderDTO bmsPurchaseOrderDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsPurchaseOrderDTO.class);
        //数据校验
        if (PurchaseUnitEnum.valueOf(bmsPurchaseOrderDTO.getUnitCode()) == null) {
            throw new BusinessException("单位天街错误");
        }



        /**
         *
         * 采购订单
         */
        BmsOrderTb bmsOrderTb = new BmsOrderTb();
        bmsOrderTb.setOrderNum(bioTaskDtlTb.getTaskNum());
        bmsOrderTb.setApplyUserId(SecurityContextHolder.getUserId());
        bmsOrderTb.setApplyTime(bmsPurchaseOrderDTO.getApplyTime());
        bmsOrderTb.setApplyUserName(SecurityContextHolder.getNickName());
        bmsOrderTb.setApplyUserDepartment(bmsPurchaseOrderDTO.getApplyUserDept());
        bmsOrderTb.setPurchaseDepartment(bmsPurchaseOrderDTO.getPurchaseDepartment());
        bmsOrderTb.setApplyUnitCode(bmsPurchaseOrderDTO.getUnitCode());
        bmsOrderTb.setApplyUnitName(bmsPurchaseOrderDTO.getUnitName());
        bmsOrderTb.setContractUrls(null);
        bmsOrderTb.setPurchaseDate(null);
        //bmsOrderTb.setPurchaseTypeCode(bmsPurchaseOrderDTO.getPurchaseTypeCode());
        //bmsOrderTb.setPurchaseTypeName(bmsPurchaseOrderDTO.getPurchaseTypeName());
        bmsOrderTb.setPurchaseReasonRemark(bmsPurchaseOrderDTO.getPurchaseReasonRemark());
        bmsOrderTb.setDemandRequireTime(bmsPurchaseOrderDTO.getDemandRequireTime());
        bmsOrderTb.setDemandUsageTime(bmsPurchaseOrderDTO.getDemandUsageTime());
        bmsOrderTb.setInvoiceUrls(null);
        bmsOrderTb.setAttachmentUrls(bmsPurchaseOrderDTO.getAttachmentUrls());
        bmsOrderTb.setCreateTime(new Date());
        bmsOrderTb.setTaskNum(bioTaskDtlTb.getTaskNum());
        bmsOrderTb.setReportAccountTime(bmsPurchaseOrderDTO.getDemandRequireTime());
        bmsOrderTbMapper.insert(bmsOrderTb);

        //插入订单明细
        createOrderDetail(bioTaskDtlTb.getTaskNum(), bmsPurchaseOrderDTO, bmsOrderTb);


    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        BmsPurchaseOrderDTO bmsPurchaseOrderDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsPurchaseOrderDTO.class);
        BmsOrderTb bmsOrderTb = bmsOrderTbMapper.selectOneByTaskNum(bioTaskDtlTb.getTaskNum());
        if (bmsOrderTb == null) {
            throw new BusinessException("数据异常，订单不存在");
        }
        //插入订单明细
        createOrderDetail(bioTaskDtlTb.getTaskNum(), bmsPurchaseOrderDTO, bmsOrderTb);
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }


    private void createOrderDetail(String taskNum, BmsPurchaseOrderDTO bmsPurchaseOrderDTO, BmsOrderTb bmsOrderTb) {
        if (CollectionUtil.isNotEmpty(bmsPurchaseOrderDTO.getProductList())) {
            for (BmsPurchaseOrderDTO.Product product : bmsPurchaseOrderDTO.getProductList()) {
                BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(product.getSupplierCode());
                BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(product.getBrandCode());
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByBrandCodeAndProductNameAndProductSpecs(product.getBrandCode(), product.getProductName(), product.getProductSpecs());
                if (bmsProductTb == null) {
                    //todo 添加商品
                }
                BmsOrderDetailTb bmsOrderDetailTb = new BmsOrderDetailTb();
                bmsOrderDetailTb.setOrderNum(bmsOrderTb.getOrderNum());
                bmsOrderDetailTb.setOrderDetailNum(IdUtils.simpleUUID());
                bmsOrderDetailTb.setProjectCode(product.getProjectCode());
                bmsOrderDetailTb.setSupplierName(product.getSupplierCode());
                bmsOrderDetailTb.setSupplierCode(product.getSupplierCode());
                bmsOrderDetailTb.setContactUserTelephone(bmsSupplierTb.getContactUserTelephone());
                bmsOrderDetailTb.setContactUserName(bmsSupplierTb.getContactUserName());
                bmsOrderDetailTb.setBrandCode(bmsBrandTb.getBrandCode());
                bmsOrderDetailTb.setBrandName(bmsBrandTb.getBrandName());
                bmsOrderDetailTb.setProductName(product.getProductName());
                bmsOrderDetailTb.setProductSku(product.getProductCode());
                bmsOrderDetailTb.setProductOutCode(product.getProductCode());
                bmsOrderDetailTb.setPurchasePrice(product.getPurchasePrice());
                bmsOrderDetailTb.setPurchaseNumber(product.getPurchaseNumber());
                bmsOrderDetailTb.setPayAmount(product.getPurchaseAmount());
                bmsOrderDetailTb.setProductCategoryCode(product.getProductCategoryCode());
                bmsOrderDetailTb.setProductCategoryName(product.getProductCategoryName());
                bmsOrderDetailTb.setCreateTime(new Date());
                bmsOrderDetailTb.setApplyUserId(SecurityContextHolder.getUserId());
                bmsOrderDetailTb.setApplyUserName(SecurityContextHolder.getNickName());
                bmsOrderDetailTb.setTaskNum(taskNum);
                bmsOrderDetailTbMapper.insert(bmsOrderDetailTb);
            }
        }
    }
}