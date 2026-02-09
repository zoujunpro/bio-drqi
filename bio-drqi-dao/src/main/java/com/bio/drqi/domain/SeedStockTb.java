package com.bio.drqi.domain;

import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @TableName seed_stock_tb
 */
@TableName(value = "seed_stock_tb")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SeedStockTb implements Serializable {
    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 种植编号
     */
    private String plantCode;

    /**
     * 种子编号
     */
    private String seedNum;

    /**
     * 上一代种子编号
     */
    private String parentNum;

    /**
     * 父本信息
     */
    private String fatherInfo;

    /**
     * 母本信息
     */
    private String matherInfo;

    /**
     * 代次
     */
    private String generation;

    /**
     * 项目物种
     */
    private String speciesCode;

    /**
     * 品种
     */
    private String breedCode;

    /**
     * 授粉方式
     */
    private String pollinationMethod;

    /**
     * 收获方式
     */
    private String harvestType;

    /**
     * 收获时间
     */
    private String harvestTime;

    /**
     * 种子数量
     */
    private BigDecimal seedNumber;

    /**
     * 计量单位g/kg/粒ml
     */
    private String unit;

    /**
     * 种子来源（1 CER/ 2 温室/3 大田/4 外单位）
     */
    private String sourceType;

    /**
     * 生产地点（天津/海南/新乡）
     */
    private String productionLocationCode;

    /**
     * 库位编号
     */
    private String stockLocationNum;

    /**
     * 提交人ID
     */
    private Integer submitUserId;

    /**
     * 提交人姓名
     */
    private String submitUserName;

    /**
     * 创建日期
     */
    private Date createTime;

    /**
     * 更新日期
     */
    private Date updateTime;

    /**
     * 备注
     */
    private String remarks;

    /**
     * 入库时数量
     */
    private BigDecimal totalNumber;

    /**
     * 目标性状
     */
    private String targetCharacter;

    /**
     * 别名
     */
    private String aliasName;

    /**
     * 基因型
     */
    private String geneType;

    /**
     * 检测结果
     */
    private String checkResult;


    /**
     * 材料类型
     */
    private String materialType;

    /**
     * 母本种子编号
     */
    private String matherSeedNum;

    /**
     * 父本种子编号
     */
    private String fatherSeedNum;

    /**
     * 母本小区编号
     */
    private String matherRegionNum;

    /**
     * 父本小区编号
     */
    private String fatherRegionNum;


    /**
     * 系谱
     */
    private String genealogy;

    /**
     * 是否基因分离
     */
    private String geneSeparateFlag;

    /**
     * 是否转基因
     */
    private String transFlag;

    /**
     * 实施方案编号
     */
    private String vectorTaskCode;


    private String experimentNum;

    private String projectCode;

    /**
     * 父本单株编号
     */
    private String fatherSingleNum;

    /**
     * 母本单株编号
     */
    private String matherSingleNum;

    private String pdImplementCode;

    private String spotCheckResult;


    /**
     * yyyyMMdd
     * 检索开始时间
     */
    @TableField(exist = false)
    private String beginDate;
    /**
     * yyyyMMdd
     * 检索结束时间
     */
    @TableField(exist = false)
    private String endDate;

    @TableField(exist = false)
    private boolean notEmptySeedNumberFlag;


    /**
     * 开始收获时间
     */
    @TableField(exist = false)
    private String beninHarvestTime;

    /**
     * 结束收获时间
     */
    @TableField(exist = false)
    private String endHarvestTime;

    @TableField(exist = false)
    private String orderType;

    @TableField(exist = false)
    private String orderField;

    @TableField(exist = false)
    private String filterNullFlag;




    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

    @Data
    public static class CheckResultContent {
        private String type;
        private String desc;
        private Object value;
        private Integer userId;
        private String userName;
        private String time;
    }

    public SeedStockTb buildCheckResult(List<CheckResultContent> checkResultContentList) {
        List<CheckResultContent> currentCheckResultContentList = new ArrayList<>();
        if (this.checkResult!=null&&!"".equals(this.checkResult)) {
            currentCheckResultContentList = JSONUtil.toList(this.checkResult, CheckResultContent.class);
        }
        Map<String, CheckResultContent> checkResultContentMap = currentCheckResultContentList.stream().collect(Collectors.toMap(CheckResultContent::getType, checkResultContent -> checkResultContent));
        for (CheckResultContent resultContent : checkResultContentList) {
            if (Objects.isNull(checkResultContentMap.get(resultContent.getType()))) {
                currentCheckResultContentList.add(resultContent);
            } else {
                CheckResultContent checkResultContent=checkResultContentMap.get(resultContent.getType());
                checkResultContent.setType(resultContent.getType());
                checkResultContent.setDesc(resultContent.getDesc());
                checkResultContent.setValue(resultContent.getValue());
                checkResultContent.setUserId(resultContent.getUserId());
                checkResultContent.setUserName(resultContent.getUserName());
                checkResultContent.setTime(resultContent.getTime());
            }
        }
        this.checkResult=JSONUtil.toJsonStr(checkResultContentList);
        return this;
    }

}