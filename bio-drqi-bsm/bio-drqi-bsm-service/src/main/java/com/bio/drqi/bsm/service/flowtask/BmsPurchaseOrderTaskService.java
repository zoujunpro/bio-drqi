package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
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
import com.bio.drqi.common.contents.BioDrQiContents;
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

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

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
        productValid(bmsPurchaseOrderDTO);

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
        productValid(bmsPurchaseOrderDTO);

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
                if (StringUtils.isEmpty(product.getBrandCode())) {
                    BmsBrandTb bmsBrandTb = bmsBrandService.add(BmsBrandAddReqDTO.builder().brandName(product.getBrandName()).build());
                    product.setBrandCode(bmsBrandTb.getBrandCode());
                }
                BmsProductTb bmsProductTb = null;
                if (StringUtils.isEmpty(product.getProductInnerCode())) {
                    bmsProductTb = bmsProductService.add(BeanUtils.copyProperties(product, BmsProductAddReqDTO.class));
                } else {
                    bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(product.getProductInnerCode());
                }
                product.setProductInnerCode(bmsProductTb.getProductInnerCode());
                BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(product.getBrandCode());
                //创建订单
                BmsOrderDetailTb bmsOrderDetailTb = new BmsOrderDetailTb();
                bmsOrderDetailTb.setOrderNum(bmsOrderTb.getOrderNum());
                bmsOrderDetailTb.setOrderDetailNum(IdUtils.simpleUUID());
                bmsOrderDetailTb.setProjectCode(product.getProjectCode());
                bmsOrderDetailTb.setSupplierCode(product.getSupplierCode());
                bmsOrderDetailTb.setContactUserTelephone(bmsSupplierTb.getContactUserTelephone());
                bmsOrderDetailTb.setContactUserName(bmsSupplierTb.getContactUserName());
                bmsOrderDetailTb.setBrandCode(bmsBrandTb.getBrandCode());
                bmsOrderDetailTb.setProductName(product.getProductName());
                bmsOrderDetailTb.setProductSpecs(product.getProductSpecs());
                bmsOrderDetailTb.setProductOutCode(product.getProductOutCode());
                bmsOrderDetailTb.setProductInnerCode(product.getProductInnerCode());
                bmsOrderDetailTb.setPurchasePrice(new BigDecimal(product.getPurchasePrice()));
                bmsOrderDetailTb.setPurchaseNumber(product.getPurchaseNumber());
                bmsOrderDetailTb.setPayAmount(new BigDecimal(product.getPurchaseAmount()));
                bmsOrderDetailTb.setProductCategoryCode(product.getProductCategoryCode());
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
                bmsOrderDetailTb.setReceiveNumber(new BigDecimal(0));
                bmsOrderDetailTb.setInvoiceUrls(null);
                bmsOrderDetailTb.setContractUrls(null);
                bmsOrderDetailTb.setReportAccountTime(null);
                bmsOrderDetailTb.setContractNumber(null);
                bmsOrderDetailTb.setPaymentVoucherUrls(null);
                bmsOrderDetailTb.setReturnNumber(new BigDecimal(0));
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


    private void productValid(BmsPurchaseOrderDTO bmsPurchaseOrderDTO) {
        if (CollectionUtil.isEmpty(bmsPurchaseOrderDTO.getProductList())) {
            throw new BusinessException("订单商品信息缺失");
        }
        for (BmsPurchaseOrderDTO.Product product : bmsPurchaseOrderDTO.getProductList()) {
            BeanUtils.trimFiledSpace(product);
            ValidatorUtil.validator(product);

            //项目校验
            BmsProjectDict bmsProjectDict = bmsProjectDictMapper.selectOneByProjectCode(product.getProjectCode());
            if (bmsProjectDict == null) {
                throw new BusinessException("项目不存在:" + product.getProjectCode());
            }
            //商品类别校验
            if (StringUtils.isEmpty(product.getProductCategoryCode())) {
                throw new BusinessException("商品规格缺失");
            }
            BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryCode(product.getProductCategoryCode());
            if (bmsProductCategoryTb == null) {
                throw new BusinessException("数据异常，商品规格查询不到");
            }
            //品牌校验
            if (StringUtils.isNotEmpty(product.getBrandCode())) {
                BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(product.getBrandCode());
                if (bmsBrandTb == null) {
                    throw new BusinessException("品牌填写数据异常,根据品牌编号找不到品牌信息");
                }
                product.setBrandName(bmsBrandTb.getBrandName());
            } else {
                if (StringUtils.isEmpty(product.getProductName())) {
                    throw new BusinessException("缺少品牌信息");
                }
                BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandName(product.getBrandName());
                if (bmsBrandTb != null) {
                    if (BioDrQiContents.N.equals(bmsBrandTb.getBrandStatus())) {
                        throw new BusinessException("品牌已经禁用，请先启用");
                    }
                    product.setBrandCode(bmsBrandTb.getBrandCode());
                }
            }
            //商品校验
            if (StringUtils.isNotEmpty(product.getProductInnerCode())) {
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(product.getProductInnerCode());
                if (bmsProductTb == null) {
                    throw new BusinessException("商品信息异常,根据编号查询不到商品信息：" + product.getProductInnerCode());
                }
                if (!product.getProductCategoryCode().equals(bmsProductTb.getProductCategoryCode())) {
                    throw new BusinessException("商品规格填写错误");
                }
                if (!bmsProductTb.getBrandCode().equals(product.getBrandCode())) {
                    throw new BusinessException(product.getProductName() + "商品品牌填写错误");
                }
                product.setProductSpecs(bmsProductTb.getProductSpecs());
                product.setProductName(bmsProductTb.getProductName());
                product.setProductCategoryCode(bmsProductTb.getProductCategoryCode());
                product.setProductCategoryName(bmsProductCategoryTb.getProductCategoryName());
            } else {
                if (StringUtils.isEmpty(product.getProductSpecs())) {
                    throw new BusinessException("商品规格必填");
                }
                if (StringUtils.isEmpty(product.getProductOutCode())) {
                    throw new BusinessException("商品编码必填");
                }
                if (StringUtils.isEmpty(product.getProductName())) {
                    throw new BusinessException("商品名称必填");
                }
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductNameAndBrandCodeAndProductSpecs(product.getProductName(), product.getBrandCode(), product.getProductSpecs());
                if (bmsProductTb != null) {
                    if (BioDrQiContents.N.equals(bmsProductTb.getProductStatus())) {
                        throw new BusinessException("商品已经禁用，请先启用");
                    }
                    product.setProductInnerCode(bmsProductTb.getProductInnerCode());
                    product.setProductSpecs(bmsProductTb.getProductSpecs());
                }
            }
            if (StringUtils.isNotEmpty(product.getSupplierCode())) {
                BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(product.getSupplierCode());
                if (bmsSupplierTb == null) {
                    throw new BusinessException("供应商找不到");
                }
            } else {
                BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierName(product.getSupplierCode());
                if (bmsSupplierTb != null) {
                    if (BioDrQiContents.N.equals(bmsSupplierTb.getSupplierStatus())) {
                        throw new BusinessException("供应商已经禁用,请先启用");
                    }
                    product.setSupplierCode(bmsSupplierTb.getSupplierCode());
                }
            }


        }
    }

}