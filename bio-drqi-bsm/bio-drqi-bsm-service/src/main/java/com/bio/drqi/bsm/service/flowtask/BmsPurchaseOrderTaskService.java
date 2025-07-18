package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.dto.BmsPurchaseOrderDTO;
import com.bio.drqi.bsm.enums.PurchaseTypeEnum;
import com.bio.drqi.bsm.enums.PurchaseUnitEnum;
import com.bio.drqi.bsm.req.BmsBrandAddReqDTO;
import com.bio.drqi.bsm.req.BmsProductAddReqDTO;
import com.bio.drqi.bsm.service.BmsBrandService;
import com.bio.drqi.bsm.service.BmsProductService;
import com.bio.drqi.domain.*;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.Date;

@Service("bms_purchase_apply")
@Slf4j
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

    @Resource
    private BmsBrandService bmsBrandService;

    @Resource
    private BmsProductService bmsProductService;

    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        BmsPurchaseOrderDTO bmsPurchaseOrderDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsPurchaseOrderDTO.class);

        ValidatorUtil.validator(bmsPurchaseOrderDTO);
        BeanUtils.trimFiledSpace(bmsPurchaseOrderDTO);

        //单位校验
        if (PurchaseUnitEnum.valueOf(bmsPurchaseOrderDTO.getUnitCode()) == null) {
            throw new BusinessException("单位填写错误");
        }
        //商品校验
        productValid(bmsPurchaseOrderDTO, bmsPurchaseOrderDTO.getPurchaseTypeCode());

        //数据重新塞入到json
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(bmsPurchaseOrderDTO));

        bioTaskDtlTb.setTaskDesc(bmsPurchaseOrderDTO.getAllProductName());

    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        BmsPurchaseOrderDTO bmsPurchaseOrderDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsPurchaseOrderDTO.class);
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            if (CollectionUtil.isEmpty(bmsPurchaseOrderDTO.getProductList())) {
                throw new BusinessException("未选择采购商品信息");
            }
        }
        //商品校验
        productValid(bmsPurchaseOrderDTO, bmsPurchaseOrderDTO.getPurchaseTypeCode());

        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {

            //插入订单
            BmsOrderTb bmsOrderTb = initBmsOrderTb(bioTaskDtlTb, bmsPurchaseOrderDTO);
            //插入订单明细
            createOrderDetail(bioTaskDtlTb, bmsPurchaseOrderDTO, bmsOrderTb);
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }


    private void createOrderDetail(BioTaskDtlTb bioTaskDtlTb, BmsPurchaseOrderDTO bmsPurchaseOrderDTO, BmsOrderTb bmsOrderTb) {
        if (CollectionUtil.isNotEmpty(bmsPurchaseOrderDTO.getProductList())) {
            for (BmsPurchaseOrderDTO.Product product : bmsPurchaseOrderDTO.getProductList()) {
                BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(product.getSupplierCode());
                //品牌没有就创建
                BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandName(product.getBrandName());
                if (bmsBrandTb == null) {
                    bmsBrandTb = bmsBrandService.add(BmsBrandAddReqDTO.builder().brandName(product.getBrandName()).build());
                    product.setBrandCode(bmsBrandTb.getBrandCode());
                }
                //非常规采购进行商品创建
                if (PurchaseTypeEnum.TYPE_2.code.equals(bmsOrderTb.getPurchaseTypeCode())) {
                    BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductNameAndBrandCodeAndProductSpecs(product.getProductName(), product.getBrandCode(), product.getProductSpecs());
                    if(bmsProductTb==null){
                         bmsProductTb = bmsProductService.add(BeanUtils.copyProperties(product, BmsProductAddReqDTO.class));
                    }
                    product.setProductInnerCode(bmsProductTb.getProductInnerCode());
                }

                BmsProjectDict bmsProjectDict = bmsProjectDictMapper.selectOneByProjectCode(product.getProjectCode());
                //创建订单
                BmsOrderDetailTb bmsOrderDetailTb = new BmsOrderDetailTb();
                bmsOrderDetailTb.setOrderNum(bmsOrderTb.getOrderNum());
                bmsOrderDetailTb.setOrderDetailNum(IdUtils.simpleUUID());
                bmsOrderDetailTb.setProjectCode(product.getProjectCode());
                bmsOrderDetailTb.setProjectName(bmsProjectDict.getProjectName());
                bmsOrderDetailTb.setSupplierName(product.getSupplierName());
                bmsOrderDetailTb.setSupplierCode(product.getSupplierCode());
                bmsOrderDetailTb.setContactUserTelephone(bmsSupplierTb.getContactUserTelephone());
                bmsOrderDetailTb.setContactUserName(bmsSupplierTb.getContactUserName());
                bmsOrderDetailTb.setBrandCode(bmsBrandTb.getBrandCode());
                bmsOrderDetailTb.setBrandName(bmsBrandTb.getBrandName());
                bmsOrderDetailTb.setProductName(product.getProductName());
                bmsOrderDetailTb.setProductSpecs(product.getProductSpecs());
                bmsOrderDetailTb.setProductOutCode(product.getProductOutCode());
                bmsOrderDetailTb.setProductInnerCode(product.getProductInnerCode());
                bmsOrderDetailTb.setPurchasePrice(new BigDecimal(product.getPurchasePrice()));
                bmsOrderDetailTb.setPurchaseNumber(product.getPurchaseNumber());
                bmsOrderDetailTb.setPayAmount(new BigDecimal(product.getPurchaseAmount()));
                bmsOrderDetailTb.setProductCategoryCode(product.getProductCategoryCode());
                bmsOrderDetailTb.setProductCategoryName(product.getProductCategoryName());
                bmsOrderDetailTb.setCreateTime(new Date());
                bmsOrderDetailTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
                bmsOrderDetailTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
                bmsOrderDetailTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                bmsOrderDetailTb.setPictureUrls(product.getPictureUrls());
                bmsOrderDetailTb.setPurchaseDate(bmsOrderTb.getPurchaseDate());
                bmsOrderDetailTb.setApplyUnitCode(bmsOrderTb.getApplyUnitCode());
                bmsOrderDetailTb.setApplyUnitName(bmsOrderTb.getApplyUnitName());
                bmsOrderDetailTb.setPurchaseDepartment(bmsOrderTb.getPurchaseDepartment());
                bmsOrderDetailTb.setTaxRate(null);
                bmsOrderDetailTb.setExpectedDeliveryTime(product.getExpectedDeliveryTime());
                bmsOrderDetailTb.setDemandUsageTime(bmsOrderTb.getDemandUsageTime());
                bmsOrderDetailTb.setDemandRequireTime(bmsOrderTb.getDemandRequireTime());


                bmsOrderDetailTb.setReceiveNumber(0);
                bmsOrderDetailTb.setInvoiceUrls(null);
                bmsOrderDetailTb.setContractUrls(null);
                bmsOrderDetailTb.setReportAccountTime(null);
                bmsOrderDetailTb.setContractNumber(null);
                bmsOrderDetailTb.setPaymentVoucherUrls(null);
                bmsOrderDetailTb.setReturnNumber(0);
                bmsOrderDetailTbMapper.insert(bmsOrderDetailTb);
            }
        }
    }

    /**
     * 采购订单
     */
    @NotNull
    private BmsOrderTb initBmsOrderTb(BioTaskDtlTb bioTaskDtlTb, BmsPurchaseOrderDTO bmsPurchaseOrderDTO) {
        BmsOrderTb bmsOrderTb = new BmsOrderTb();
        bmsOrderTb.setOrderNum(bioTaskDtlTb.getTaskNum());
        bmsOrderTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
        bmsOrderTb.setApplyTime(DateUtil.formatDateTime(new Date()));
        bmsOrderTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
        bmsOrderTb.setApplyUserDepartment(bmsPurchaseOrderDTO.getApplyUserDept());
        bmsOrderTb.setPurchaseDepartment(bmsPurchaseOrderDTO.getPurchaseDepartment());
        bmsOrderTb.setApplyUnitCode(bmsPurchaseOrderDTO.getUnitCode());
        bmsOrderTb.setApplyUnitName(bmsPurchaseOrderDTO.getUnitName());
        bmsOrderTb.setPurchaseDate(null);
        bmsOrderTb.setPurchaseTypeCode(bmsPurchaseOrderDTO.getPurchaseTypeCode());
        bmsOrderTb.setPurchaseTypeName(PurchaseTypeEnum.getNameByCode(bmsPurchaseOrderDTO.getPurchaseTypeCode()));
        bmsOrderTb.setPurchaseReasonRemark(bmsPurchaseOrderDTO.getPurchaseReasonRemark());
        bmsOrderTb.setDemandRequireTime(bmsPurchaseOrderDTO.getDemandRequireTime());
        bmsOrderTb.setDemandUsageTime(bmsPurchaseOrderDTO.getDemandUsageTime());
        bmsOrderTb.setAttachmentUrls(bmsPurchaseOrderDTO.getAttachmentUrls());
        bmsOrderTb.setCreateTime(new Date());
        bmsOrderTb.setTaskNum(bioTaskDtlTb.getTaskNum());
        bmsOrderTb.setOverFlag(BioBsmContents.N);
        bmsOrderTbMapper.insert(bmsOrderTb);
        return bmsOrderTb;
    }


    private void productValid(BmsPurchaseOrderDTO bmsPurchaseOrderDTO, String purchaseTypeCode) {
        if (CollectionUtil.isEmpty(bmsPurchaseOrderDTO.getProductList())) {
            throw new BusinessException("订单商品信息缺失");
        }
        for (BmsPurchaseOrderDTO.Product product : bmsPurchaseOrderDTO.getProductList()) {
            BeanUtils.trimFiledSpace(product);
            //常规采购
            if (PurchaseTypeEnum.TYPE_1.code.equals(purchaseTypeCode)) {
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(product.getProductInnerCode());
                if (bmsProductTb == null) {
                    log.error("不存在的商品信息={}", product);
                    throw new BusinessException("商品不存在");
                }
                product.setProductName(bmsProductTb.getProductName());
                product.setProductSpecs(bmsProductTb.getProductSpecs());
            } else if (PurchaseTypeEnum.TYPE_2.code.equals(purchaseTypeCode)) {
                //非常规采购
                product.setProductName(product.getProductName().trim());
                product.setProductSpecs(product.getProductSpecs().trim());
            } else {
                throw new BusinessException("采购类型错误");
            }

            BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(product.getSupplierCode());
            if (bmsSupplierTb == null) {
                log.error("不存在供应商信息={}", product);
                throw new BusinessException("供应商不存在");
            }
            if (BioBsmContents.Y.equals(bmsSupplierTb.getDeleteFlag())) {
                log.error("供应商已经删除={}", product);
                throw new BusinessException("供应商已经删除");
            }

            BmsProjectDict bmsProjectDict = bmsProjectDictMapper.selectOneByProjectCode(product.getProjectCode());
            if(bmsProjectDict==null){
                throw new BusinessException("项目不存在:"+product.getProjectCode());
            }
        }
    }

}