package com.bio.drqi.manage.service.seed.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONUtil;

import com.bio.drqi.manage.seed.SeedQualityCheckReqDTO;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ExcelData;
import com.bio.common.core.dto.ExcelHead;
import com.bio.common.core.util.ExcelUtil;
import com.bio.drqi.domain.SeedQualityCheckConfig;
import com.bio.drqi.domain.SeedQualityCheckDtlTb;
import com.bio.drqi.domain.SeedStockTb;
import com.bio.drqi.manage.service.seed.SeedQualityCheckService;
import com.bio.drqi.mapper.SeedQualityCheckConfigMapper;
import com.bio.drqi.mapper.SeedQualityCheckDtlTbMapper;
import com.bio.drqi.mapper.SeedStockTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
public class SeedQualityCheckServiceImpl implements SeedQualityCheckService {

    @Resource
    private SeedQualityCheckConfigMapper seedQualityCheckConfigMapper;

    @Resource
    private SeedQualityCheckDtlTbMapper seedQualityCheckDtlTbMapper;

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Override
    public void downTemplate(HttpServletResponse httpServletResponse) {
        ExcelUtil.write(httpServletResponse, getExcelHeads(), Arrays.asList(new HashMap<>()), "种子日常质控考种模板.xlsx");
    }

    @NotNull
    private List<ExcelHead> getExcelHeads() {
        List<ExcelHead> excelHeadList = new ArrayList<>();
        excelHeadList.add(new ExcelHead("seedNum", "种子编号"));
        List<SeedQualityCheckConfig> seedQualityCheckConfigList = seedQualityCheckConfigMapper.selectAllOrderByIdDesc();
        if (CollectionUtil.isNotEmpty(seedQualityCheckConfigList)) {
            seedQualityCheckConfigList.forEach(seedQualityCheckConfig -> {
                excelHeadList.add(new ExcelHead(seedQualityCheckConfig.getFieldCode(), seedQualityCheckConfig.getFieldName()));

            });
        }
        return excelHeadList;
    }

    @Override
    public List<Map<String, String>> fieldList() {
        List<Map<String, String>> result = new ArrayList<>();
        result.add(MapUtil.of("seedNum", "种子编号"));
        result.addAll(fieldListNotTimeAndSeedNum());
        result.add(MapUtil.of("createTime", "创建时间"));
        result.add(MapUtil.of("createUser", "创建人"));
        return result;
    }

    @Override
    public List<Map<String, String>> fieldListNotTimeAndSeedNum() {
        List<Map<String, String>> result = new ArrayList<>();
        List<SeedQualityCheckConfig> seedQualityCheckConfigList = seedQualityCheckConfigMapper.selectAllOrderByIdDesc();
        if (CollectionUtil.isNotEmpty(seedQualityCheckConfigList)) {
            seedQualityCheckConfigList.stream().forEach(seedQualityCheckConfig -> {
                Map<String, String> map = new HashMap<>();
                map.put(seedQualityCheckConfig.getFieldCode(), seedQualityCheckConfig.getFieldName());
                result.add(map);
            });
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLoadData(MultipartFile multipartFile) {
        File tempFile = FileUtil.createTempFile(multipartFile.getOriginalFilename() + ".xlsx", true);
        try {
            FileUtils.copyToFile(multipartFile.getInputStream(), tempFile);
        } catch (IOException e) {
            log.info("文件格式错误，处理失败", e);
            throw new BusinessException("文件格式错误，处理失败");
        }
        ExcelData excelData = ExcelUtil.readExcelToExcelData(tempFile.getAbsolutePath());
        List<SeedQualityCheckConfig> seedQualityCheckConfigList = seedQualityCheckConfigMapper.selectAllOrderByIdDesc();

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
            //考种记录翻译
            Map<String, String> resultMap = new HashMap<>();
            seedQualityCheckConfigList.forEach(seedQualityCheckConfig -> {
                resultMap.put(seedQualityCheckConfig.getFieldCode(), Objects.isNull(cellMap.get(seedQualityCheckConfig.getFieldName())) ? null : cellMap.get(seedQualityCheckConfig.getFieldName()).toString());
            });
            //插入考种记录
            SeedQualityCheckDtlTb seedQualityCheckDtlTb = new SeedQualityCheckDtlTb();
            seedQualityCheckDtlTb.setSeedNum(seedNum.toString());
            seedQualityCheckDtlTb.setCheckResult(JSONUtil.toJsonStr(resultMap));
            seedQualityCheckDtlTb.setCreateTime(new Date());
            seedQualityCheckDtlTb.setCreateUser(SecurityContextHolder.getNickName());
            seedQualityCheckDtlTbMapper.insert(seedQualityCheckDtlTb);

            //合并最新的考种信息
            Map<String, String> currentCheckResultMap = JSONUtil.toBean(seedStockTb.getCheckResult(), Map.class);
            currentCheckResultMap.putAll(resultMap);

            //更新最新的种子考种信息
            seedStockTb.setCheckResult(JSONUtil.toJsonStr(currentCheckResultMap));
            seedStockTbMapper.updateById(seedStockTb);
        }
    }

    @Override
    public PageInfo<Map<String, String>> listPage(SeedQualityCheckReqDTO seedQualityCheckReqDTO) {
        PageHelper.startPage(seedQualityCheckReqDTO.getPageNum(), seedQualityCheckReqDTO.getPageSize());
        List<SeedQualityCheckDtlTb> seedQualityCheckDtlTbList = seedQualityCheckDtlTbMapper.selectSelective(SeedQualityCheckDtlTb.builder().seedNum(seedQualityCheckReqDTO.getSeedNum()).build());
        PageInfo<SeedQualityCheckDtlTb> pageInfo = new PageInfo(seedQualityCheckDtlTbList);
        PageInfo<Map<String, String>> resultPageInfo = new PageInfo<>();
        resultPageInfo.setTotal(pageInfo.getTotal());
        List<Map<String, String>> resultList = new ArrayList<>();
        List<SeedQualityCheckConfig> seedQualityCheckConfigList = seedQualityCheckConfigMapper.selectAllOrderByIdDesc();
        seedQualityCheckDtlTbList.forEach(seedQualityCheckDtlTb -> {
            Map<String, String> resultMap=new HashMap<>();
            resultMap.put("seedNum", seedQualityCheckDtlTb.getSeedNum());
            Map<String, String> checkResultMap = JSONUtil.toBean(seedQualityCheckDtlTb.getCheckResult(), Map.class);
            seedQualityCheckConfigList.forEach(seedQualityCheckConfig -> {
                resultMap.put(seedQualityCheckConfig.getFieldCode(),checkResultMap.get(seedQualityCheckConfig.getFieldCode()));
            });
            resultMap.put("createTime",DateUtil.format(seedQualityCheckDtlTb.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN));
            resultMap.put("createUser",seedQualityCheckDtlTb.getCreateUser());
            resultList.add(resultMap);
        });
        resultPageInfo.setList(resultList);

        return resultPageInfo;
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
