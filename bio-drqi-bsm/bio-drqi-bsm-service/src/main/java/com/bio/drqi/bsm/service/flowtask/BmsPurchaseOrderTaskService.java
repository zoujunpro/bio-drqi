package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.core.uuid.IdUtils;
import com.bio.drqi.bsm.contents.BioBsmContents;
import com.bio.drqi.bsm.dto.BmsPurchaseOrderDTO;
import com.bio.drqi.bsm.enums.PurchaseUnitEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.enums.BioTaskStatusEnum;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

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

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        BmsPurchaseOrderDTO bmsPurchaseOrderDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsPurchaseOrderDTO.class);

        ValidatorUtil.validator(bmsPurchaseOrderDTO);

        //单位校验
        if (PurchaseUnitEnum.valueOf(bmsPurchaseOrderDTO.getUnitCode()) == null) {
            throw new BusinessException("单位填写错误");
        }
        //商品校验
        productValid(bmsPurchaseOrderDTO);

        //数据重新塞入到json
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(bmsPurchaseOrderDTO));


    }


    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        BmsPurchaseOrderDTO bmsPurchaseOrderDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsPurchaseOrderDTO.class);
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
                BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(product.getBrandCode());
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByBrandCodeAndProductNameAndProductSpecs(product.getBrandCode(), product.getProductName(), product.getProductSpecs());
                if (bmsProductTb == null) {
                    // 添加商品
                    bmsProductTb = new BmsProductTb();
                    bmsProductTb.setProductName(product.getProductName());
                    bmsProductTb.setProductOutCode(product.getProductCode());
                    bmsProductTb.setProductInnerCode(IdUtils.simpleUUID());
                    bmsProductTb.setProductCategoryCode(product.getProductCategoryCode());
                    bmsProductTb.setProductTypeCode(product.getProductTypeCode());
                    bmsProductTb.setSupplierCode(bmsSupplierTb.getSupplierCode());
                    bmsProductTb.setBrandName(bmsBrandTb.getBrandName());
                    bmsProductTb.setBrandCode(bmsBrandTb.getBrandCode());
                    bmsProductTb.setProductSpecs(product.getProductSpecs());
                    bmsProductTb.setCreateTime(new Date());
                    bmsProductTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
                    bmsProductTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
                    bmsProductTb.setDeleteFlag(BioBsmContents.N);
                    bmsProductTb.setPictureUrls(product.getPictureUrls());
                    bmsProductTbMapper.insert(bmsProductTb);
                }
                //创建订单
                BmsOrderDetailTb bmsOrderDetailTb = new BmsOrderDetailTb();
                bmsOrderDetailTb.setOrderNum(bmsOrderTb.getOrderNum());
                bmsOrderDetailTb.setOrderDetailNum(IdUtils.simpleUUID());
                bmsOrderDetailTb.setProjectCode(product.getProjectCode());
                bmsOrderDetailTb.setProjectName(product.getProductName());
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
                bmsOrderDetailTb.setTaskNum(bioTaskDtlTb.getTaskNum());
                bmsOrderDetailTbMapper.insert(bmsOrderDetailTb);
            }
        }
    }

    private void productValid(BmsPurchaseOrderDTO bmsPurchaseOrderDTO) {
        if (CollectionUtil.isNotEmpty(bmsPurchaseOrderDTO.getProductList())) {
            for (BmsPurchaseOrderDTO.Product product : bmsPurchaseOrderDTO.getProductList()) {
                //空格处理
                product.setProductName(product.getProductName().trim());
                product.setProductSpecs(product.getProductSpecs().trim());

                BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(product.getSupplierCode());
                if (bmsSupplierTb == null) {
                    throw new BusinessException("供应商不存在");
                }
                if (BioBsmContents.Y.equals(bmsSupplierTb.getDeleteFlag())) {
                    throw new BusinessException("供应商已经删除");
                }
                BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(product.getBrandCode());
                if (bmsBrandTb == null) {
                    throw new BusinessException("品牌不存在");
                }
                if (BioBsmContents.Y.equals(bmsBrandTb.getDeleteFlag())) {
                    throw new BusinessException("品牌已经删除");
                }
                List<BmsProductTb> bmsProductTbList = bmsProductTbMapper.selectAllByProductNameAndBrandCode(product.getProductName(), product.getBrandCode());
                if (CollectionUtil.isNotEmpty(bmsProductTbList)) {
                    for (BmsProductTb bmsProductTb : bmsProductTbList) {
                        if (product.getProductSpecs().toLowerCase().replace(" ", "").equals(bmsProductTb.getProductSpecs().toLowerCase().replace(" ", ""))) {
                            log.info("已有此规格的耗材: " + JSONUtil.toJsonStr(bmsProductTbList));
                            throw new BusinessException("已有此规格的耗材：" + bmsProductTb.getProductSpecs());
                        }
                    }
                }

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
        bmsOrderTb.setApplyUserId(SecurityContextHolder.getUserId());
        bmsOrderTb.setApplyTime(DateUtil.formatDateTime(new Date()));
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
        return bmsOrderTb;
    }

}