package com.bio.drqi.manage.service.task.seed;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.cer.contents.CerProjectContents;
import com.bio.cer.domain.*;
import com.bio.cer.dto.seed.SeedInStoreDTO;
import com.bio.cer.enums.GenerationEnum;
import com.bio.cer.mapper.*;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 入库
 */
@Service("seed_store_apply")
@Slf4j
public class SeedStoreApplyProcService extends AbstractSeedTaskService {

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;


    @Override
    public void taskCheck(BioTaskDtlTb bioTaskDtlTb) {
        SeedInStoreDTO seedInStoreDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedInStoreDTO.class);
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectList(null);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, CerBreedDict> cerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getSpeciesCode() + ":" + cerBreedDict.getBreedCode(), cerBreedDict -> cerBreedDict));
        Map<String, CerSpeciesConf> cerSpeciesConfMap = cerSpeciesConfList.stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, cerSpeciesConf -> cerSpeciesConf));

        for (SeedInStoreDTO.ExecuteFormContent executeFormContent : seedInStoreDTO.getExecuteForm().getExecuteFormContentList()) {
            log.info("种子入库 executeFormContent={}", JSONUtil.toJsonStr(executeFormContent));
            ValidatorUtil.validator(seedInStoreDTO);
            //通用校验

            if (StringUtils.isNotEmpty(executeFormContent.getHarvestTime())) {
                if (!validateDateFormat(executeFormContent.getHarvestTime())) {
                    throw new BusinessException("收获日期格式错误,要求yyyy-mm-dd，实际格式：" + executeFormContent.getHarvestTime());
                }
            }
            CerSpeciesConf cerSpeciesConf = cerSpeciesConfMap.get(executeFormContent.getSpeciesCode());
            if (cerSpeciesConf == null) {
                throw new BusinessException("不支持此物种入库:" + executeFormContent.getSpeciesCode());
            }

            CerBreedDict cerBreedDict = cerBreedDictMap.get(executeFormContent.getSpeciesCode() + ":" + executeFormContent.getBreedCode());
            if (cerBreedDict == null) {
                throw new BusinessException("不支持此品种入库：" + executeFormContent.getBreedCode());
            }

            if (executeFormContent.getSeedNumber() == null || executeFormContent.getSeedNumber().compareTo(BigDecimal.ZERO) < 0) {
                throw new BusinessException("入库数量非法");
            }
            String generationNum = GenerationEnum.getGenerationNum(executeFormContent.getGeneration());
            if (StringUtils.isEmpty(generationNum)) {
                throw new BusinessException("代次填写错误：" + executeFormContent.getGeneration());
            }
            if (StringUtils.isNotEmpty(executeFormContent.getSampleCode())) {
                if (StringUtils.isEmpty(executeFormContent.getProjectCode())) {
                    throw new BusinessException("填写取样取样编号必填所属项目");
                }
                if (StringUtils.isNotEmpty(executeFormContent.getParentNum())) {
                    throw new BusinessException("有取样编号后不应填写上代种子编号");
                }
                CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByProjectCodeAndSampleCodeFirst(executeFormContent.getProjectCode(), executeFormContent.getSampleCode());
                if (cerSampleTestTb == null) {
                    throw new BusinessException(executeFormContent.getProjectCode() + "项目中不存在此取样编号:" + executeFormContent.getSampleCode());
                }
            }
            if (StringUtils.isNotEmpty(executeFormContent.getParentNum())) {
                SeedStockTb parentSeedStockTb = seedStockTbMapper.selectOneBySeedNum(executeFormContent.getParentNum());
                if (parentSeedStockTb == null) {
                    throw new BusinessException("上代种子编号非法");
                }
                executeFormContent.setProjectCode(parentSeedStockTb.getProjectCode());
            }
            executeFormContent.setBreedName(cerBreedDict.getBreedName());
            executeFormContent.setSpeciesName(cerSpeciesConf.getSpeciesName());
        }
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        // 需要进行数据回退， 需要回退入库日志，库存记录 和提交记录 ，如果某一条种子已经使用，则整个工单无法回退
        SeedInStoreDTO seedInStoreDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedInStoreDTO.class);
        List<SeedStockInLog> seedStockInLogList = seedStockInLogMapper.selectAllByTaskNum(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(seedStockInLogList)) {
            for (SeedStockInLog seedStockInLog : seedStockInLogList) {
                SeedStockTb seedStockTb = seedStockTbMapper.selectOneBySeedNum(seedStockInLog.getSeedNum());
                if (seedStockTb.getSeedNumber().compareTo(seedStockTb.getTotalNumber()) != 0) {
                    throw new BusinessException("该批次中有种子被使用，无法撤销 使用种子编号：" + seedStockTb.getSeedNum());
                }
            }

            //删除种子入库记录
            seedStockInLogMapper.deleteByTaskNum(bioTaskDtlTb.getTaskNum());
            //删除种子库存
            seedStockTbMapper.deleteBySeedNumIn(seedStockInLogList.stream().map(SeedStockInLog::getSeedNum).collect(Collectors.toList()));
        }
        seedInStoreDTO.getExecuteForm().getExecuteFormContentList().stream().forEach(executeFormContent -> {
            executeFormContent.setStoreFlag(CerProjectContents.N);
        });
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(seedInStoreDTO));
    }


    private boolean validateDateFormat(String date) {
        String pattern = "\\d{4}-\\d{2}-\\d{2}"; // 定义日期格式为yyyy-MM-dd
        return date.matches(pattern);
    }

}
