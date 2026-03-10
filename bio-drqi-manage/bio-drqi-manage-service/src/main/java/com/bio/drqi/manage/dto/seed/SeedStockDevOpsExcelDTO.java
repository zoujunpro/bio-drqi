package com.bio.drqi.manage.dto.seed;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class SeedStockDevOpsExcelDTO {

    /**
     * 种植编号
     */
    @ExcelProperty("种植编号")
    private String plantCode;

    /**
     * 种子编号
     */
    @ExcelProperty("种子编号")
    private String seedNum;



    /**
     * 代次
     */
    @ExcelProperty("代次")
    private String generation;

    /**
     * 项目物种
     */
    @ExcelIgnore
    private String speciesCode;
    /**
     * 品种
     */
    @ExcelIgnore
    private String breedCode;

    /**
     * 品种
     */
    @ExcelProperty("品种")
    private String breedName;

    /**
     * 项目物种
     */
    @ExcelProperty("物种")
    private String speciesName;



    /**
     * 授粉方式
     */
    @ExcelIgnore
    private String pollinationMethod;
    /**
     * 授粉方式
     */
    @ExcelProperty("授粉方式")
    private String pollinationMethodName;

    /**
     * 收获方式
     */
    @ExcelIgnore
    private String harvestType;

    /**
     * 收获方式
     */
    @ExcelProperty("收获方式")
    private String harvestTypeName;

    /**
     * 收获时间
     */
    @ExcelProperty("收获时间")
    private String harvestTime;

    /**
     * 种子数量
     */
    @ExcelProperty("种子数量")
    private BigDecimal seedNumber;

    /**
     * 计量单位g/kg/粒ml
     */
    @ExcelProperty("计量单位")
    private String unit;

    /**
     * 种子来源（1 CER/ 2 温室/3 大田/4 外单位）
     */
    @ExcelIgnore
    private String sourceType;

    @ExcelProperty("种子来源")
    private String sourceTypeName;

    /**
     * 生产地点（天津/海南/新乡）
     */
    @ExcelIgnore
    private String productionLocationCode;


    /**
     * 生产地点（天津/海南/新乡）
     */
    @ExcelProperty("生产地点")
    private String productionLocationCodeName;

    /**
     * 库位编号
     */
    @ExcelProperty("库位编号")
    private String stockLocationNum;



    /**
     * 提交人姓名
     */
    @ExcelProperty("提交人姓名")
    private String submitUserName;

    /**
     * 创建日期
     */
    @ExcelProperty("创建日期")
    private Date createTime;



    /**
     * 备注
     */
    @ExcelProperty("备注")
    private String remarks;

    /**
     * 入库时数量
     */
    @ExcelProperty("入库时数量")
    private BigDecimal totalNumber;

    /**
     * 目标性状
     */
    @ExcelProperty("目标性状")
    private String targetCharacter;

    /**
     * 别名
     */
    @ExcelProperty("别名")
    private String aliasName;

    /**
     * 基因型
     */
    @ExcelProperty("基因型")
    private String geneType;

    /**
     * 检测结果
     */
    @ExcelProperty("检测结果")
    private String checkResult;


    /**
     * 材料类型
     */
    @ExcelIgnore
    private String materialType;

    /**
     * 材料类型
     */
    @ExcelProperty("材料类型")
    private String materialTypeName;

    /**
     * 母本种子编号
     */
    @ExcelProperty("母本种子编号")
    private String matherSeedNum;

    /**
     * 父本种子编号
     */
    @ExcelProperty("父本种子编号")
    private String fatherSeedNum;

    /**
     * 母本小区编号
     */
    @ExcelProperty("母本小区编号")
    private String matherRegionNum;

    /**
     * 父本小区编号
     */
    @ExcelProperty("父本小区编号")
    private String fatherRegionNum;



    /**
     * 是否基因分离
     */
    @ExcelProperty("是否基因分离")
    private String geneSeparateFlag;

    /**
     * 是否转基因
     */
    @ExcelProperty("是否转基因")
    private String transFlag;

    /**
     * 实施方案编号
     */
    @ExcelProperty("实施方案编号")
    private String vectorTaskCode;

    @ExcelProperty("试验编号")
    private String experimentNum;


    /**
     * 父本单株编号
     */
    @ExcelProperty("父本单株编号")
    private String fatherSingleNum;

    /**
     * 母本单株编号
     */
    @ExcelProperty("母本单株编号")
    private String matherSingleNum;


    @ExcelProperty("抽检反馈结果")
    private String spotCheckResult;

    @ExcelProperty("出库工单编号")
    private String outTaskNum;






}
