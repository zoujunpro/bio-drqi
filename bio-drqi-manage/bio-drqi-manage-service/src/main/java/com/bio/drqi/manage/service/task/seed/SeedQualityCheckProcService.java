package com.bio.drqi.manage.service.task.seed;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.seed.SeedQualityCheckDTO;
import com.bio.drqi.mapper.SeedQualityCheckConfigMapper;
import com.bio.drqi.mapper.SeedQualityCheckDtlTbMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ExcelData;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

@Service("seed_quality_check")
@Slf4j
public class SeedQualityCheckProcService extends AbstractSeedTaskService {

    @Resource
    private OssService ossService;

    @Resource
    private SeedQualityCheckConfigMapper seedQualityCheckConfigMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private SeedQualityCheckDtlTbMapper seedQualityCheckDtlTbMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        SeedQualityCheckDTO seedQualityCheckDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedQualityCheckDTO.class);
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + seedQualityCheckDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, seedQualityCheckDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【种子日常质检】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        ExcelData excelData = ExcelUtil.readExcelToExcelData(tempFilePath);
        List<SeedQualityCheckConfig> seedQualityCheckConfigList = seedQualityCheckConfigMapper.selectAllOrderByIdDesc();
        Map<String, String> seedQualityCheckConfigMap = seedQualityCheckConfigList.stream().collect(Collectors.toMap(SeedQualityCheckConfig::getFieldName, SeedQualityCheckConfig::getFieldCode));
        List<Map<String, Object>> cellMapList = getCellMap(excelData);
        if (CollectionUtil.isEmpty(cellMapList)) {
            throw new BusinessException("上传数据为空");
        }
        //校验标题
        cellMapList.get(0).keySet().forEach(key -> {
            if (!"种子编号".equals(key)) {
                if (StringUtils.isEmpty(seedQualityCheckConfigMap.get(key))) {
                    throw new BusinessException("excel表格此" + key + "列数据未进行配置");
                }
            }
        });
        for (Map<String, Object> cellMap : cellMapList) {
            Object seedNum = cellMap.get("种子编号");
            if (Objects.isNull(seedNum)) {
                throw new BusinessException("上传数据异常，无种子编号");
            }
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedNum.toString());
            if (seedStockTb == null) {
                throw new BusinessException(seedNum + "种子编号不存在");
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        SeedQualityCheckDTO seedQualityCheckDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedQualityCheckDTO.class);
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + seedQualityCheckDTO.getExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, seedQualityCheckDTO.getExcelUrl());
        } catch (Exception e) {
            log.error("【种子日常质检】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        ExcelData excelData = ExcelUtil.readExcelToExcelData(tempFilePath);
        List<Map<String, Object>> cellMapList = getCellMap(excelData);

        for (Map<String, Object> cellMap : cellMapList) {
            Object seedNum = cellMap.get("种子编号");
            if (Objects.isNull(seedNum)) {
                throw new BusinessException("上传数据异常，无种子编号");
            }
            SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedNum.toString());
            if (seedStockTb == null) {
                throw new BusinessException(seedNum + "种子编号不存在");
            }
            List<SeedQualityCheckConfig> seedQualityCheckConfigList = seedQualityCheckConfigMapper.selectAllOrderByIdDesc();

            //考种记录翻译
            Map<String, Object> resultMap = new HashMap<>();
            seedQualityCheckConfigList.forEach(seedQualityCheckConfig -> {
                resultMap.put(seedQualityCheckConfig.getFieldCode(), Objects.isNull(cellMap.get(seedQualityCheckConfig.getFieldName())) ? null : cellMap.get(seedQualityCheckConfig.getFieldName()));
            });
            //插入考种记录
            SeedQualityCheckDtlTb seedQualityCheckDtlTb = new SeedQualityCheckDtlTb();
            seedQualityCheckDtlTb.setSeedNum(seedNum.toString());
            seedQualityCheckDtlTb.setCheckResult(JSONUtil.toJsonStr(resultMap));
            seedQualityCheckDtlTb.setCreateTime(new Date());
            seedQualityCheckDtlTb.setCreateUser(SecurityContextHolder.getNickName());
            seedQualityCheckDtlTbMapper.insert(seedQualityCheckDtlTb);

            //合并最新的考种信息
            List<SeedStockTb.CheckResultContent> currentCheckResultList = new ArrayList<>();
            seedQualityCheckConfigList.forEach(seedQualityCheckConfig -> {
                if (Objects.nonNull(cellMap.get(seedQualityCheckConfig.getFieldName()))) {
                    SeedStockTb.CheckResultContent checkResultContent = new SeedStockTb.CheckResultContent();
                    checkResultContent.setType(seedQualityCheckConfig.getFieldCode());
                    checkResultContent.setDesc(seedQualityCheckConfig.getFieldName());
                    checkResultContent.setValue(cellMap.get(seedQualityCheckConfig.getFieldName()));
                    checkResultContent.setUserId(SecurityContextHolder.getUserId());
                    checkResultContent.setUserName(SecurityContextHolder.getNickName());
                    checkResultContent.setTime(DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN));
                    currentCheckResultList.add(checkResultContent);
                }
            });
            //更新最新的种子考种信息
            seedStockTbMapper.updateById(seedStockTb.buildCheckResult(currentCheckResultList));
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        //不做任何处理
    }

    private static List<Map<String, Object>> getCellMap(ExcelData excelData) {
        List<Map<String, Object>> cellMapList = new ArrayList<>();
        for (List<ExcelData.Cell> cellList : excelData.getCellList()) {
            Map<String, Object> cellMap = cellList.stream().filter(cell -> Objects.nonNull(cell.getValue())).collect(Collectors.toMap(ExcelData.Cell::getCName, ExcelData.Cell::getValue));
            cellMapList.add(cellMap);
        }
        return cellMapList;
    }
}
