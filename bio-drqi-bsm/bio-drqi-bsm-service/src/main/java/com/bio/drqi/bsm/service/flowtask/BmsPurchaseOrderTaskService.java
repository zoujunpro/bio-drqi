package com.bio.drqi.bsm.service.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
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
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
                BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(product.getProductInnerCode());
                BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(product.getSupplierCode());
                BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsProductTb.getBrandCode());
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
                bmsOrderDetailTb.setPayType(product.getPayType());
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
            //供应商校验
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
            //商品校验
            BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(product.getProductInnerCode());
            if (bmsProductTb == null) {
                throw new BusinessException("无此商品:" + bmsProductTb.getProductInnerCode());
            }

        }
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        BmsPurchaseOrderDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), BmsPurchaseOrderDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("申请人", dto.getApplyUserName()));
        applyFields.add(buildField("申请部门", dto.getApplyUserDept()));
        applyFields.add(buildField("采购单位", dto.getUnitName()));
        applyFields.add(buildField("采购部门", dto.getPurchaseDepartment()));
        applyFields.add(buildField("采购类型", PurchaseTypeEnum.getNameByCode(dto.getPurchaseTypeCode())));
        applyFields.add(buildField("需求提出日期", dto.getDemandRequireTime()));
        applyFields.add(buildField("需求使用日期", dto.getDemandUsageTime()));
        applyFields.add(buildField("采购总金额", decimalText(dto.getPurchaseTotalAmount())));
        sections.add(buildFieldSection("申请信息", applyFields));
        if (StringUtils.isNotEmpty(dto.getPurchaseReasonRemark())) {
            List<BioHtmlModelDTO.ModelField> reasonFields = new ArrayList<>();
            reasonFields.add(buildField("申购事由", dto.getPurchaseReasonRemark()));
            sections.add(buildFieldSection("申购事由", reasonFields));
        }

        Map<String, String> projectNameMap = bmsProjectDictMapper.selectAllOrderByIdDesc().stream()
                .collect(Collectors.toMap(BmsProjectDict::getProjectCode, BmsProjectDict::getProjectName, (left, right) -> left));
        Map<String, String> supplierNameMap = bmsSupplierTbMapper.selectSelective(new BmsSupplierTb()).stream()
                .collect(Collectors.toMap(BmsSupplierTb::getSupplierCode, BmsSupplierTb::getSupplierName, (left, right) -> left));
        Map<String, String> brandNameMap = bmsBrandTbMapper.selectSelective(new BmsBrandTb()).stream()
                .collect(Collectors.toMap(BmsBrandTb::getBrandCode, BmsBrandTb::getBrandName, (left, right) -> left));

        List<String> headers = java.util.Arrays.asList("项目", "供应商", "品牌", "商品名称", "外部编码", "内部编码", "规格", "采购数量", "采购单价", "采购金额", "预计到货时间", "付款类型");
        List<Map<String, Object>> rows = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(dto.getProductList())) {
            for (BmsPurchaseOrderDTO.Product item : dto.getProductList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("项目", defaultText(projectNameMap.get(item.getProjectCode()), item.getProjectCode()));
                row.put("供应商", defaultText(supplierNameMap.get(item.getSupplierCode()), item.getSupplierName()));
                row.put("品牌", defaultText(brandNameMap.get(item.getBrandCode()), item.getBrandName()));
                row.put("商品名称", item.getProductName());
                row.put("外部编码", item.getProductOutCode());
                row.put("内部编码", item.getProductInnerCode());
                row.put("规格", item.getProductSpecs());
                row.put("采购数量", decimalText(item.getPurchaseNumber()));
                row.put("采购单价", item.getPurchasePrice());
                row.put("采购金额", item.getPurchaseAmount());
                row.put("预计到货时间", item.getExpectedDeliveryTime());
                row.put("付款类型", item.getPayType());
                rows.add(row);
            }
        }
        sections.add(buildTableSection("采购商品明细", headers, rows));
        return sections;
    }

    private String decimalText(BigDecimal value) {
        return value == null ? "" : value.stripTrailingZeros().toPlainString();
    }

    private String defaultText(String first, String fallback) {
        return StringUtils.isNotEmpty(first) ? first : fallback;
    }
}
