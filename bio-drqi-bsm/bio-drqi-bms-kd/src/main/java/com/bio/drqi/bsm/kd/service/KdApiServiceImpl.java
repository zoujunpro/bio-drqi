package com.bio.drqi.bsm.kd.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.bsm.kd.dto.*;
import com.bio.drqi.bsm.kd.dto.model.*;
import com.bio.drqi.bsm.kd.enums.FormIdEnum;
import com.bio.drqi.bsm.kd.enums.KdParentGroupEnum;
import com.bio.drqi.bsm.kd.enums.OperateEnum;
import com.bio.drqi.bsm.kd.enums.OrgEnum;
import com.bio.drqi.bsm.kd.util.KdRequestUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.formula.functions.T;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;

@Service
@Slf4j
public class KdApiServiceImpl implements KdApiService {

    @Value("${spring.profiles.active}")
    private String active;

    @Resource
    private BmsProductCategoryTbMapper bmsProductCategoryTbMapper;

    @Resource
    private BmsBrandTbMapper bmsBrandTbMapper;

    @Resource
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;

    @Resource
    private BmsProjectDictMapper bmsProjectDictMapper;


    @Override
    public String execute(OperateEnum operateEnum, Object obj, String unitCode) {
        switch (operateEnum) {
            case brandSave:
                return executeBrandSave(obj, unitCode);
            case brandModify:
                return executeBrandModify(obj);
            case brandDisable:
                return executeBrandDisable(obj);
            case projectSave:
                return executeProjectSave(obj, unitCode);
            case projectModify:
                return executeProjectModify(obj);
            case projectDisable:
                return executeProjectDisable(obj);
            case projectQuery:
                return executeProjectQuery(obj);
            case stockSave:
                return executeStockSave(obj, unitCode);
            case stockQuery:
                return executeStockQuery(obj, unitCode);
            case stockModify:
                return executeStockModify(obj);
            case stockDisable:
                return executeStockDisable(obj);
            case materialSave:
                return executeMaterialSave(obj, unitCode);
            case materialModify:
                return executeMaterialModify(obj);
            case materialDisable:
                return executeMaterialDisable(obj, unitCode);
            case materialQuery:
                return materialQuery(obj, unitCode);
            case groupSave:
                return groupSave(obj);
            case groupQuery:
                return groupQuery(obj);
            case inStockSave:
                return inStockSave(obj, unitCode);
            case inStockQuery:
                return inStockQuery(obj);
            case outStockSave:
                return outStockSave(obj, unitCode);
            case outStockQuery:
                return outStockQuery(obj);
            case moveStockSave:
                return moveStockSave(obj, unitCode);
            case moveStockQuery:
                return moveStockQuery(obj);
            case returnStockSave:
                return returnStockSave(obj, unitCode);
            case returnStockQuery:
                return returnStockQuery(obj);
            default:
                throw new BusinessException("数据异常，请检查金蝶配置");
        }
    }

    private String moveStockQuery(Object obj) {
        BmsMoveOrderDetailTb bmsMoveOrderDetailTb = (BmsMoveOrderDetailTb) obj;
        ExecuteBillQueryModelDTO executeBillQueryModelDTO = new ExecuteBillQueryModelDTO();
        executeBillQueryModelDTO.setFormId(FormIdEnum.STK_TransferDirect.name());
        executeBillQueryModelDTO.setFieldKeys("FID,FBillno,FDocumentStatus");
        String filterString = "F_WAUJ_UUID='%s' and  FBillTypeID.FNumber ='ZJDB01_SYS'";
        executeBillQueryModelDTO.setFilterString(String.format(filterString, bmsMoveOrderDetailTb.getId().toString()));
        List<List<Object>> result = KdRequestUtil.query(executeBillQueryModelDTO);
        if (CollectionUtil.isNotEmpty(result) && CollectionUtil.isNotEmpty(result.get(0))) {
            return result.get(0).get(0).toString();
        }
        return null;
    }

    private String returnStockQuery(Object obj) {
        BmsReturnOrderDetailTb bmsReturnOrderDetailTb = (BmsReturnOrderDetailTb) obj;
        ExecuteBillQueryModelDTO executeBillQueryModelDTO = new ExecuteBillQueryModelDTO();
        executeBillQueryModelDTO.setFormId(FormIdEnum.PUR_MRB.name());
        executeBillQueryModelDTO.setFieldKeys("FID,FBillno,FDocumentStatus");
        String filterString = "F_WAUJ_UUID='%s' and FBillTypeID.FNumber ='TLD01_SYS'";
        executeBillQueryModelDTO.setFilterString(String.format(filterString, bmsReturnOrderDetailTb.getId().toString()));
        List<List<Object>> result = KdRequestUtil.query(executeBillQueryModelDTO);
        if (CollectionUtil.isNotEmpty(result) && CollectionUtil.isNotEmpty(result.get(0))) {
            return result.get(0).get(0).toString();
        }
        return null;
    }

    private String outStockQuery(Object obj) {
        BmsProductStockOutLog bmsProductStockOutLog = (BmsProductStockOutLog) obj;
        ExecuteBillQueryModelDTO executeBillQueryModelDTO = new ExecuteBillQueryModelDTO();
        executeBillQueryModelDTO.setFormId(FormIdEnum.STK_MisDelivery.name());
        executeBillQueryModelDTO.setFieldKeys("FID,FBillno,FDocumentStatus");
        String filterString = "F_WAUJ_UUID='%s' and FBillTypeID.FNumber ='QTCKD01_SYS'";
        executeBillQueryModelDTO.setFilterString(String.format(filterString, bmsProductStockOutLog.getId().toString()));
        List<List<Object>> result = KdRequestUtil.query(executeBillQueryModelDTO);
        if (CollectionUtil.isNotEmpty(result) && CollectionUtil.isNotEmpty(result.get(0))) {
            return result.get(0).get(0).toString();
        }
        return null;
    }

    private String inStockQuery(Object obj) {
        BmsProductStockInLog bmsProductStockInLog = (BmsProductStockInLog) obj;
        ExecuteBillQueryModelDTO executeBillQueryModelDTO = new ExecuteBillQueryModelDTO();
        executeBillQueryModelDTO.setFormId(FormIdEnum.STK_InStock.name());
        executeBillQueryModelDTO.setFieldKeys("FID,FBillno,FDocumentStatus");
        String filterString = "F_WAUJ_UUID='%s' and FBillTypeID.FNumber ='RKD01_SYS'";
        executeBillQueryModelDTO.setFilterString(String.format(filterString, bmsProductStockInLog.getId().toString()));
        List<List<Object>> result = KdRequestUtil.query(executeBillQueryModelDTO);
        if (CollectionUtil.isNotEmpty(result) && CollectionUtil.isNotEmpty(result.get(0))) {
            return result.get(0).get(0).toString();
        }
        return null;
    }

    private String executeProjectQuery(Object obj) {
        BmsProjectDict bmsProjectDict = (BmsProjectDict) obj;
        ExecuteBillQueryModelDTO executeBillQueryModelDTO = new ExecuteBillQueryModelDTO();
        executeBillQueryModelDTO.setFormId(FormIdEnum.BOS_ASSISTANTDATA_DETAIL.name());
        executeBillQueryModelDTO.setFieldKeys("FEntryID,FNUMBER,FDataValue");
        String filterString = "FId.FNUMBER='XM' and Fnumber='%s'";
        executeBillQueryModelDTO.setFilterString(String.format(filterString, bmsProjectDict.getKdProjectCode()));
        List<List<Object>> result = KdRequestUtil.query(executeBillQueryModelDTO);
        if (CollectionUtil.isNotEmpty(result) && CollectionUtil.isNotEmpty(result.get(0))) {
            return result.get(0).get(0).toString();
        }
        return null;
    }


    private String materialQuery(Object obj, String unitCode) {
        BmsProductTb bmsProductTb = (BmsProductTb) obj;
        ExecuteBillQueryModelDTO executeBillQueryModelDTO = new ExecuteBillQueryModelDTO();
        executeBillQueryModelDTO.setFormId(FormIdEnum.BD_MATERIAL.name());
        executeBillQueryModelDTO.setFieldKeys("FMATERIALID,FNUMBER,FNAME,FDocumentStatus,FForbidStatus");
        String filterString = "FNUMBER='%s' and FCreateOrgId.FNumber = '%s' and FUseOrgId.FNumber = '%s' and FDocumentStatus = 'C' and FForbidStatus = 'A' ";
        executeBillQueryModelDTO.setFilterString(String.format(filterString, bmsProductTb.getProductInnerCode(), OrgEnum.getOrgByActiveAndUnitCode(active, unitCode), OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));
        List<List<Object>> result = KdRequestUtil.query(executeBillQueryModelDTO);
        if (CollectionUtil.isNotEmpty(result) && CollectionUtil.isNotEmpty(result.get(0))) {
            return result.get(0).get(0).toString();
        }
        return null;
    }

    private String executeStockQuery(Object obj, String unitCode) {
        BmsStockDict bmsStockDict = (BmsStockDict) obj;
        ExecuteBillQueryModelDTO executeBillQueryModelDTO = new ExecuteBillQueryModelDTO();
        executeBillQueryModelDTO.setFormId(FormIdEnum.BD_STOCK.name());
        executeBillQueryModelDTO.setFieldKeys("FSTOCKID,FNUMBER,FNAME,FDocumentStatus,FForbidStatus");
        String filterString = "Fnumber='%s' and FCreateOrgId.FNumber ='%s' and  FUseOrgId.FNumber='%s' and  FDocumentStatus='C' and FForbidStatus='A'";
        executeBillQueryModelDTO.setFilterString(String.format(filterString, bmsStockDict.getStockCode(), OrgEnum.getOrgByActiveAndUnitCode(active, unitCode), OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));
        List<List<Object>> result = KdRequestUtil.query(executeBillQueryModelDTO);
        if (CollectionUtil.isNotEmpty(result) && CollectionUtil.isNotEmpty(result.get(0))) {
            return result.get(0).get(0).toString();
        }
        return null;

    }

    private String groupQuery(Object obj) {
        BmsProductCategoryTb bmsProductCategoryTb = (BmsProductCategoryTb) obj;
        ExecuteBillQueryModelDTO executeBillQueryModelDTO = new ExecuteBillQueryModelDTO();
        executeBillQueryModelDTO.setFormId(FormIdEnum.Sal_MATERIALGROUP.name());
        executeBillQueryModelDTO.setFieldKeys("FID,FNUMBER,FNAME,FPARENTID");
        executeBillQueryModelDTO.setFilterString(String.format("FNUMBER='%s'", bmsProductCategoryTb.getProductCategoryCode()));
        List<List<Object>> result = KdRequestUtil.query(executeBillQueryModelDTO);
        if (CollectionUtil.isNotEmpty(result) && CollectionUtil.isNotEmpty(result.get(0))) {
            return result.get(0).get(0).toString();
        }
        return null;
    }

    @Override
    public List<QuerySupplierDTO> querySupplier() {
        return KdRequestUtil.executeQuerySupplier();
    }


    /**
     * 保存品牌
     *
     * @param obj
     * @param unitCode
     * @return
     */
    private String executeBrandSave(Object obj, String unitCode) {
        BmsBrandTb bmsBrandTb = (BmsBrandTb) obj;
        BrandKdModel brandKdModel = new BrandKdModel();
        brandKdModel.setFID("0");
        brandKdModel.setFnumber(bmsBrandTb.getBrandCode());
        brandKdModel.setFname(bmsBrandTb.getBrandName());
        return KdRequestUtil.save(FormIdEnum.CMK_BD_Brand, KdApiBaseSaveRequestDTO.buildOfSave(brandKdModel, OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));
    }

    /**
     * 修改品牌
     *
     * @param obj
     * @return
     */
    private String executeBrandModify(Object obj) {
        BmsBrandTb bmsBrandTb = (BmsBrandTb) obj;
        BrandKdModel brandKdModel = new BrandKdModel();
        brandKdModel.setFID(bmsBrandTb.getKdNumber());
        brandKdModel.setFnumber(bmsBrandTb.getBrandCode());
        brandKdModel.setFname(bmsBrandTb.getBrandName());
        return KdRequestUtil.save(FormIdEnum.CMK_BD_Brand, KdApiBaseSaveRequestDTO.buildOfModify(brandKdModel));
    }

    /**
     * 禁用品牌
     *
     * @param obj
     * @return
     */
    private String executeBrandDisable(Object obj) {
        BmsBrandTb bmsBrandTb = (BmsBrandTb) obj;
        KdApiBaseDisableRequestDTO kdApiBaseDisableRequestDTO = new KdApiBaseDisableRequestDTO();
        kdApiBaseDisableRequestDTO.setIds(bmsBrandTb.getKdNumber());
        return KdRequestUtil.disable(FormIdEnum.CMK_BD_Brand, kdApiBaseDisableRequestDTO);
    }

    /**
     * 执行项目保存
     *
     * @param obj
     * @param unitCode
     * @return
     */
    private String executeProjectSave(Object obj, String unitCode) {
        BmsProjectDict bmsProjectDict = (BmsProjectDict) obj;
        ProjectModel projectModel = new ProjectModel();
        projectModel.setFEntryID("0");
        projectModel.setFnumber(bmsProjectDict.getKdProjectCode());
        projectModel.setFDataValue(bmsProjectDict.getKdProjectName());
        return KdRequestUtil.save(FormIdEnum.BOS_ASSISTANTDATA_DETAIL, KdApiBaseSaveRequestDTO.buildOfSave(projectModel, OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));
    }


    /**
     * 项目修改
     *
     * @param obj
     * @return
     */
    private String executeProjectModify(Object obj) {
        BmsProjectDict bmsProjectDict = (BmsProjectDict) obj;
        ProjectModel projectModel = new ProjectModel();
        projectModel.setFEntryID(bmsProjectDict.getKdNumber());
        projectModel.setFnumber(bmsProjectDict.getKdProjectCode());
        projectModel.setFDataValue(bmsProjectDict.getKdProjectName());
        return KdRequestUtil.save(FormIdEnum.BOS_ASSISTANTDATA_DETAIL, KdApiBaseSaveRequestDTO.buildOfModify(projectModel));
    }

    /**
     * 项目禁用
     *
     * @param obj
     * @return
     */
    private String executeProjectDisable(Object obj) {
        BmsProjectDict bmsProjectDict = (BmsProjectDict) obj;
        KdApiBaseDisableRequestDTO kdApiBaseDisableRequestDTO = new KdApiBaseDisableRequestDTO();
        kdApiBaseDisableRequestDTO.setIds(bmsProjectDict.getKdNumber());
        return KdRequestUtil.disable(FormIdEnum.BOS_ASSISTANTDATA_DETAIL, kdApiBaseDisableRequestDTO);
    }


    /**
     * 仓库保存
     *
     * @param obj
     * @param unitCode
     * @return
     */
    private String executeStockSave(Object obj, String unitCode) {
        BmsStockDict bmsStockDict = (BmsStockDict) obj;
        StockModel stockModel = new StockModel();
        stockModel.setFStockId(bmsStockDict.getKdNumber());
        stockModel.setFnumber(bmsStockDict.getStockCode());
        stockModel.setFname(bmsStockDict.getStockName());
        stockModel.setFStockProperty("1");
        stockModel.setFStockStatusType("0,1,2,3,4,5,6,7,8");
        return KdRequestUtil.save(FormIdEnum.BD_STOCK, KdApiBaseSaveRequestDTO.buildOfSave(stockModel, OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));
    }

    /**
     * 仓库修改
     *
     * @param obj
     * @return
     */
    private String executeStockModify(Object obj) {
        BmsStockDict bmsStockDict = (BmsStockDict) obj;
        StockModel stockModel = new StockModel();
        stockModel.setFStockId(bmsStockDict.getKdNumber());
        stockModel.setFnumber(bmsStockDict.getStockCode());
        stockModel.setFname(bmsStockDict.getStockName());
        return KdRequestUtil.save(FormIdEnum.BD_STOCK, KdApiBaseSaveRequestDTO.buildOfModify(stockModel));

    }

    /**
     * 仓库禁用
     *
     * @param obj
     * @return
     */
    private String executeStockDisable(Object obj) {
        BmsStockDict bmsStockDict = (BmsStockDict) obj;
        KdApiBaseDisableRequestDTO kdApiBaseDisableRequestDTO = new KdApiBaseDisableRequestDTO();
        kdApiBaseDisableRequestDTO.setIds(bmsStockDict.getKdNumber());
        return KdRequestUtil.disable(FormIdEnum.BD_STOCK, kdApiBaseDisableRequestDTO);

    }


    public String groupSave(Object obj) {
        BmsProductCategoryTb bmsProductCategoryTb = (BmsProductCategoryTb) obj;
        GroupSaveDTO groupSaveDTO = new GroupSaveDTO();
        groupSaveDTO.setFParentId(bmsProductCategoryTb.getKdParentId());
        groupSaveDTO.setFNumber(bmsProductCategoryTb.getProductCategoryCode());
        groupSaveDTO.setFName(bmsProductCategoryTb.getProductCategoryName());
        return KdRequestUtil.groupSave(FormIdEnum.BD_MATERIAL, groupSaveDTO);
    }

    private String executeMaterialSave(Object obj, String unitCode) {
        BmsProductTb bmsProductTb = (BmsProductTb) obj;

        BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryCode(bmsProductTb.getProductCategoryCode());
        if (bmsProductCategoryTb == null) {
            throw new BusinessException("找不到货品类别：当前货品:" + bmsProductTb.getProductInnerCode());
        }
        if (bmsProductCategoryTb.getKdNumber() == null) {
            throw new BusinessException("材料分组未同步");
        }
        BmsBrandTb bmsBrandTb = bmsBrandTbMapper.selectOneByBrandCode(bmsProductTb.getBrandCode());
        if(bmsBrandTb==null){
            throw new BusinessException("品牌找不到");
        }
        MaterialSaveModel materialSaveModel = new MaterialSaveModel(bmsProductTb.getProductInnerCode(), bmsProductTb.getProductName(), bmsProductTb.getProductSpecs(), bmsBrandTb.getBrandName(), bmsProductCategoryTb.getProductCategoryCode(), bmsProductCategoryTb.getKdCategoryCode());
        return KdRequestUtil.save(FormIdEnum.BD_MATERIAL, KdApiBaseSaveRequestDTO.buildOfSave(materialSaveModel, OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));
    }

    /**
     * 入库
     *
     * @param obj
     * @param unitCode
     * @return
     */
    private String inStockSave(Object obj, String unitCode) {
        BmsProductStockInLog bmsProductStockInLog = (BmsProductStockInLog) obj;
        String inDate = DateUtil.format(bmsProductStockInLog.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN);
        BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryCode(bmsProductStockInLog.getProductCategoryCode());
        if (bmsProductCategoryTb == null) {
            throw new BusinessException("找不到货品类别：当前货品:" + bmsProductStockInLog.getProductInnerCode());
        }
        if (bmsProductCategoryTb.getKdNumber() == null) {
            throw new BusinessException("材料分组未同步");
        }
        BmsProjectDict bmsProjectDict = bmsProjectDictMapper.selectOneByProjectCode(bmsProductStockInLog.getProjectCode());
        if (bmsProjectDict == null) {
            throw new BusinessException("入库数据找不到所属项目，项目编号为：" + bmsProductStockInLog.getProjectCode());
        }
        if (StringUtils.isEmpty(bmsProjectDict.getKdProjectType())) {
            throw new BusinessException("项目未配置项目类型是政府项目，研发项目还是合同项目");
        }

        BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(bmsProductStockInLog.getSupplierCode());
        if (bmsSupplierTb == null) {
            throw new BusinessException("供应商不存在" + bmsProductStockInLog.getSupplierCode());
        }
        if (StringUtils.isEmpty(bmsSupplierTb.getKdNumber())) {
            throw new BusinessException("供应商未同步金蝶" + bmsProductStockInLog.getSupplierCode());
        }
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsProductStockInLog.getProductInnerCode());
        if (bmsProductTb == null) {
            throw new BusinessException("耗材库中不存在此耗材：" + bmsProductStockInLog.getProductInnerCode());
        }
        if (StringUtils.isEmpty(bmsProductTb.getKdNumber())) {
            throw new BusinessException("耗材还未同步到金蝶" + bmsProductStockInLog.getProductInnerCode());
        }
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockCode(bmsProductStockInLog.getStockCode());
        if (bmsStockDict == null) {
            throw new BusinessException("仓库找不到");
        }
        if (StringUtils.isEmpty(bmsStockDict.getKdNumber())) {
            throw new BusinessException("仓库未同步到金蝶：" + bmsStockDict.getStockName());
        }
        String orgCode = OrgEnum.getOrgByActiveAndUnitCode(active, unitCode);
        KdParentGroupEnum kdParentGroupEnum = KdParentGroupEnum.ofCode(bmsProductCategoryTb.getKdParentId(), active);

        InStockSaveModel inStockSaveModel = new InStockSaveModel(bmsProjectDict.getKdProjectType(), bmsProductStockInLog.getId().toString(), inDate, kdParentGroupEnum, orgCode, bmsSupplierTb.getKdNumber(), bmsProductTb.getProductInnerCode(), bmsProductStockInLog.getProductPrice(), new BigDecimal(bmsProductStockInLog.getStoreNumber()), bmsProductStockInLog.getProjectCode(), bmsStockDict.getStockCode(), new BigDecimal(bmsProductStockInLog.getTaxRate() == null ? "0" : bmsProductStockInLog.getTaxRate()));

        return KdRequestUtil.save(FormIdEnum.STK_InStock, KdApiBaseSaveRequestDTO.buildOfSave(inStockSaveModel, OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));

    }


    /**
     * 出库
     *
     * @param obj
     * @param unitCode
     * @return
     */
    private String outStockSave(Object obj, String unitCode) {
        BmsProductStockOutLog bmsProductStockOutLog = (BmsProductStockOutLog) obj;
        String outDate = DateUtil.format(bmsProductStockOutLog.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN);
        BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryCode(bmsProductStockOutLog.getProductCategoryCode());
        if (bmsProductCategoryTb == null) {
            throw new BusinessException("找不到货品类别：当前货品:" + bmsProductStockOutLog.getProductInnerCode());
        }
        if (bmsProductCategoryTb.getKdNumber() == null) {
            throw new BusinessException("材料分组未同步");
        }
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsProductStockOutLog.getProductInnerCode());
        if (bmsProductTb == null) {
            throw new BusinessException("耗材库中不存在此耗材：" + bmsProductStockOutLog.getProductInnerCode());
        }
        if (bmsProductTb.getKdNumber() == null) {
            throw new BusinessException("耗材还未同步到金蝶" + bmsProductStockOutLog.getProductInnerCode());
        }
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockCode(bmsProductStockOutLog.getStockCode());
        if (bmsStockDict == null) {
            throw new BusinessException("库房异常，找不到此库房" + bmsProductStockOutLog.getStockCode());
        }
        if (bmsStockDict.getKdNumber() == null) {
            throw new BusinessException("库房未同步到金蝶" + bmsStockDict.getKdNumber());
        }
        String orgCode = OrgEnum.getOrgByActiveAndUnitCode(active, unitCode);
        KdParentGroupEnum kdParentGroupEnum = KdParentGroupEnum.ofCode(bmsProductCategoryTb.getKdParentId(), active);
        OutStockSaveModel outStockSaveModel = new OutStockSaveModel(bmsProductStockOutLog.getId().toString(), outDate, kdParentGroupEnum, orgCode, bmsProductTb.getProductInnerCode(), new BigDecimal(bmsProductStockOutLog.getOutNumber()), bmsStockDict.getStockCode());
        return KdRequestUtil.save(FormIdEnum.STK_MisDelivery, KdApiBaseSaveRequestDTO.buildOfSave(outStockSaveModel, OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));

    }

    /**
     * 退货
     *
     * @param obj
     * @param unitCode
     * @return
     */
    private String returnStockSave(Object obj, String unitCode) {
        BmsReturnOrderDetailTb bmsReturnOrderDetailTb = (BmsReturnOrderDetailTb) obj;
        BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryCode(bmsReturnOrderDetailTb.getProductCategoryCode());
        if (bmsProductCategoryTb == null) {
            throw new BusinessException("找不到货品类别：当前货品:" + bmsReturnOrderDetailTb.getProductInnerCode());
        }
        if (bmsProductCategoryTb.getKdNumber() == null) {
            throw new BusinessException("材料分组未同步");
        }
        BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(bmsReturnOrderDetailTb.getSupplierCode());
        if (bmsSupplierTb == null) {
            throw new BusinessException("供应商不存在" + bmsReturnOrderDetailTb.getSupplierCode());
        }
        if (bmsSupplierTb.getKdNumber() == null) {
            throw new BusinessException("供应商未同步金蝶" + bmsReturnOrderDetailTb.getSupplierCode());
        }
        BmsStockDict bmsStockDict = bmsStockDictMapper.selectOneByStockCode(bmsReturnOrderDetailTb.getStockCode());
        if (bmsStockDict == null) {
            throw new BusinessException("库房异常，找不到此库房" + bmsReturnOrderDetailTb.getStockCode());
        }
        if (bmsStockDict.getKdNumber() == null) {
            throw new BusinessException("库房未同步到金蝶" + bmsStockDict.getKdNumber());
        }
        String returnDate = DateUtil.format(bmsReturnOrderDetailTb.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN);
        String orgCode = OrgEnum.getOrgByActiveAndUnitCode(active, unitCode);
        KdParentGroupEnum kdParentGroupEnum = KdParentGroupEnum.ofCode(bmsProductCategoryTb.getKdParentId(), active);
        ReturnStockSaveModel returnStockSaveModel = new ReturnStockSaveModel(bmsReturnOrderDetailTb.getId().toString(), kdParentGroupEnum, orgCode, returnDate, bmsSupplierTb.getKdNumber(), bmsReturnOrderDetailTb.getProductInnerCode(), new BigDecimal(bmsReturnOrderDetailTb.getReturnNumber()), bmsStockDict.getStockCode(), bmsReturnOrderDetailTb.getProjectCode(), new BigDecimal(bmsReturnOrderDetailTb.getTaxRate() == null ? "0" : bmsReturnOrderDetailTb.getTaxRate()));
        return KdRequestUtil.save(FormIdEnum.PUR_MRB, KdApiBaseSaveRequestDTO.buildOfSave(returnStockSaveModel, OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));

    }

    /**
     * 移库
     *
     * @param obj
     * @param unitCode
     * @return
     */
    private String moveStockSave(Object obj, String unitCode) {
        BmsMoveOrderDetailTb bmsMoveOrderDetailTb = (BmsMoveOrderDetailTb) obj;
        BmsProductCategoryTb bmsProductCategoryTb = bmsProductCategoryTbMapper.selectOneByProductCategoryCode(bmsMoveOrderDetailTb.getProductCategoryCode());
        if (bmsProductCategoryTb == null) {
            throw new BusinessException("找不到货品类别：当前货品:" + bmsMoveOrderDetailTb.getProductInnerCode());
        }
        if (bmsProductCategoryTb.getKdNumber() == null) {
            throw new BusinessException("材料分组未同步");
        }
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsMoveOrderDetailTb.getProductInnerCode());
        if (bmsProductTb == null) {
            throw new BusinessException("耗材库中不存在此耗材：" + bmsMoveOrderDetailTb.getProductInnerCode());
        }
        if (bmsProductTb.getKdNumber() == null) {
            throw new BusinessException("耗材还未同步到金蝶" + bmsMoveOrderDetailTb.getProductInnerCode());
        }
        BmsStockDict srcBmsStockDict = bmsStockDictMapper.selectOneByStockCode(bmsMoveOrderDetailTb.getFromStockCode());
        if (srcBmsStockDict == null) {
            throw new BusinessException("库房异常，找不到此库房" + bmsMoveOrderDetailTb.getFromStockCode());
        }
        if (srcBmsStockDict.getKdNumber() == null) {
            throw new BusinessException("库房未同步到金蝶" + srcBmsStockDict.getKdNumber());
        }

        BmsStockDict targetBmsStockDict = bmsStockDictMapper.selectOneByStockCode(bmsMoveOrderDetailTb.getToStockCode());
        if (targetBmsStockDict == null) {
            throw new BusinessException("库房异常，找不到此库房" + bmsMoveOrderDetailTb.getFromStockCode());
        }
        if (targetBmsStockDict.getKdNumber() == null) {
            throw new BusinessException("库房未同步到金蝶" + targetBmsStockDict.getKdNumber());
        }

        String moveDate = DateUtil.format(bmsMoveOrderDetailTb.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN);
        KdParentGroupEnum kdParentGroupEnum = KdParentGroupEnum.ofCode(bmsProductCategoryTb.getKdParentId(), active);
        String orgCode = OrgEnum.getOrgByActiveAndUnitCode(active, unitCode);

        MoveStockSaveModel moveStockSaveModel = new MoveStockSaveModel(bmsMoveOrderDetailTb.getId().toString(), moveDate, kdParentGroupEnum, orgCode, bmsMoveOrderDetailTb.getProductInnerCode(), new BigDecimal(bmsMoveOrderDetailTb.getMoveNumber()), srcBmsStockDict.getStockCode(), targetBmsStockDict.getStockCode());
        return KdRequestUtil.save(FormIdEnum.STK_TransferDirect, KdApiBaseSaveRequestDTO.buildOfModify(moveStockSaveModel));
    }

    private String executeMaterialModify(Object obj) {
        BmsProductTb bmsProductTb = (BmsProductTb) obj;
        MaterialSaveModel materialSaveModel = new MaterialSaveModel();
        materialSaveModel.setFMATERIALID(bmsProductTb.getKdNumber());
        materialSaveModel.setFname(bmsProductTb.getProductName());
        materialSaveModel.setFspecification(bmsProductTb.getProductSpecs());
        return KdRequestUtil.save(FormIdEnum.BD_MATERIAL, KdApiBaseSaveRequestDTO.buildOfModify(materialSaveModel));
    }

    private String executeMaterialDisable(Object obj, String unitCode) {
        return null;
    }

}
