package com.bio.drqi.bsm.kd.service;

import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.bsm.kd.dto.GroupSaveDTO;
import com.bio.drqi.bsm.kd.dto.KdApiBaseDisableRequestDTO;
import com.bio.drqi.bsm.kd.dto.QuerySupplierDTO;
import com.bio.drqi.bsm.kd.dto.model.*;
import com.bio.drqi.bsm.kd.dto.KdApiBaseSaveRequestDTO;
import com.bio.drqi.bsm.kd.enums.FormIdEnum;
import com.bio.drqi.bsm.kd.enums.KdParentGroupEnum;
import com.bio.drqi.bsm.kd.enums.OperateEnum;
import com.bio.drqi.bsm.kd.enums.OrgEnum;
import com.bio.drqi.bsm.kd.util.KdRequestUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import lombok.extern.slf4j.Slf4j;
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
    private BmsSupplierTbMapper bmsSupplierTbMapper;

    @Resource
    private BmsProductTbMapper bmsProductTbMapper;

    @Resource
    private BmsStockDictMapper bmsStockDictMapper;


    @Override
    public String execute(OperateEnum operateEnum, Object obj, String unitCode) {
        switch (operateEnum) {
            case bmsSave:
                return executeBmsSave(obj, unitCode);
            case bmsModify:
                return executeBmsModify(obj);
            case bmsDisable:
                return executeBmsDisable(obj);
            case projectSave:
                return executeProjectSave(obj, unitCode);
            case projectModify:
                return executeProjectModify(obj);
            case projectDisable:
                return executeProjectDisable(obj);
            case stockSave:
                return executeStockSave(obj, unitCode);
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
            case groupSave:
                return groupSave(obj);
            case inStockSave:
                return inStockSave(obj, unitCode);
            case outStockSave:
                return outStockSave(obj, unitCode);
            case moveStockSave:
                return moveStockSave(obj, unitCode);
            case returnStockSave:
                return returnStockSave(obj, unitCode);
            default:
                throw new BusinessException("数据异常，请检查金蝶配置");
        }
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
    private String executeBmsSave(Object obj, String unitCode) {
        BmsBrandTb bmsBrandTb = (BmsBrandTb) obj;
        BrandKdModel brandKdModel = new BrandKdModel();
        brandKdModel.setFID(0);
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
    private String executeBmsModify(Object obj) {
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
    private String executeBmsDisable(Object obj) {
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
        projectModel.setFnumber(bmsProjectDict.getProjectCode());
        projectModel.setFDataValue(bmsProjectDict.getProjectName());
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
        projectModel.setFnumber(bmsProjectDict.getProjectCode());
        projectModel.setFDataValue(bmsProjectDict.getProjectName());
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
        stockModel.setFStockId(0);
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


    private String groupSave(Object obj) {
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
        MaterialSaveModel materialSaveModel = new MaterialSaveModel(bmsProductTb.getProductInnerCode(), bmsProductTb.getProductName(), bmsProductTb.getProductSpecs(), bmsProductTb.getBrandName(), bmsProductCategoryTb.getProductCategoryCode(), bmsProductCategoryTb.getKdCategoryCode());
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
        BmsSupplierTb bmsSupplierTb = bmsSupplierTbMapper.selectOneBySupplierCode(bmsProductStockInLog.getSupplierCode());
        if (bmsSupplierTb == null) {
            throw new BusinessException("供应商不存在" + bmsProductStockInLog.getSupplierCode());
        }
        if (bmsSupplierTb.getKdNumber() == null) {
            throw new BusinessException("供应商未同步金蝶" + bmsProductStockInLog.getSupplierCode());
        }
        BmsProductTb bmsProductTb = bmsProductTbMapper.selectOneByProductInnerCode(bmsProductStockInLog.getProductInnerCode());
        if (bmsProductTb == null) {
            throw new BusinessException("耗材库中不存在此耗材：" + bmsProductStockInLog.getProductInnerCode());
        }
        if (bmsProductTb.getKdNumber() == null) {
            throw new BusinessException("耗材还未同步到金蝶" + bmsProductStockInLog.getProductInnerCode());
        }
        String orgCode = OrgEnum.getOrgByActiveAndUnitCode(active, unitCode);
        KdParentGroupEnum kdParentGroupEnum = KdParentGroupEnum.ofCode(bmsProductCategoryTb.getKdParentId(), active);

        InStockSaveModel inStockSaveModel = new InStockSaveModel(inDate, kdParentGroupEnum, orgCode, bmsSupplierTb.getKdNumber().toString(), bmsProductTb.getProductInnerCode(), bmsProductStockInLog.getProductPrice(), new BigDecimal(bmsProductStockInLog.getStoreNumber()), bmsProductStockInLog.getProjectCode(), bmsProductStockInLog.getStockCode(), new BigDecimal(bmsProductStockInLog.getTaxRate()));

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
        OutStockSaveModel outStockSaveModel = new OutStockSaveModel(outDate, kdParentGroupEnum, orgCode, bmsProductTb.getProductInnerCode(), new BigDecimal(bmsProductStockOutLog.getOutNumber()), bmsStockDict.getStockCode());
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
        String returnDate = DateUtil.format(bmsReturnOrderDetailTb.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN);
        String orgCode = OrgEnum.getOrgByActiveAndUnitCode(active, unitCode);
        KdParentGroupEnum kdParentGroupEnum = KdParentGroupEnum.ofCode(bmsProductCategoryTb.getKdParentId(), active);

        ReturnStockModel returnStockModel = new ReturnStockModel( orgCode, returnDate,  kdParentGroupEnum,  bmsSupplierTb.getKdNumber().toString(), bmsReturnOrderDetailTb.getProductInnerCode(),  new BigDecimal(bmsReturnOrderDetailTb.getReturnNumber()), bmsReturnOrderDetailTb.getStockCode(), bmsReturnOrderDetailTb.getProjectCode());
        return KdRequestUtil.save(FormIdEnum.PUR_MRB, KdApiBaseSaveRequestDTO.buildOfSave(returnStockModel, OrgEnum.getOrgByActiveAndUnitCode(active, unitCode)));

    }

    /**
     * 移库
     *
     * @param obj
     * @param unitCode
     * @return
     */
    private String moveStockSave(Object obj, String unitCode) {

        return null;
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
