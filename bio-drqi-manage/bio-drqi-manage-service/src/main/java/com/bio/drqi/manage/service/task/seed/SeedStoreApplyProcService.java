package com.bio.drqi.manage.service.task.seed;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.enums.GenerationEnum;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.seed.SeedInStoreDTO;
import com.bio.drqi.mapper.*;
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

    /**
     * 大田
     */
    private static String SOURCE_TYPE_4="4";

    @Resource
    private SeedStockTbMapper seedStockTbMapper;

    @Resource
    private SeedStockInLogMapper seedStockInLogMapper;

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private SeedProduceAddressDictMapper seedProduceAddressDictMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;


    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        SeedInStoreDTO seedInStoreDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), SeedInStoreDTO.class);
        List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectList(null);
        List<CerBreedDict> cerBreedDictList = cerBreedDictMapper.selectAll();
        Map<String, CerBreedDict> cerBreedDictMap = cerBreedDictList.stream().collect(Collectors.toMap(cerBreedDict -> cerBreedDict.getBreedCode(), cerBreedDict -> cerBreedDict));
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

            CerBreedDict cerBreedDict = cerBreedDictMap.get(executeFormContent.getBreedCode());
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
            if (StringUtils.isNotEmpty(executeFormContent.getProductionLocationName())) {
                SeedProduceAddressDict seedProduceAddressDict = seedProduceAddressDictMapper.selectOneByAddressName(executeFormContent.getProductionLocationName());
                if (seedProduceAddressDict == null) {
                    throw new BusinessException("种子生产地点填写错误");
                }
                executeFormContent.setProductionLocationCode(seedProduceAddressDict.getAddressCode());
            }
            if (StringUtils.isNotEmpty(executeFormContent.getVectorTaskCode())) {
                CerVectorTaskTb vectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(executeFormContent.getVectorTaskCode());
                if (vectorTaskTb == null) {
                    throw new BusinessException("实施方案不存在：" + executeFormContent.getVectorTaskCode());
                }
                CerProjectTb cerProjectTb = cerProjectTbMapper.selectOneByProjectCode(vectorTaskTb.getProjectCode());
                executeFormContent.setTargetCharacter(cerProjectTb.getProjectName());
            }
            if(SeedSourceEnum.getByCode(executeFormContent.getSource())==null){
                throw new BusinessException("来源渠道不正确："+executeFormContent.getSource());
            }
            //CER 校验
            if (StringUtils.isNotEmpty(executeFormContent.getPlantCode())) {
                if (StringUtils.isNotEmpty(executeFormContent.getMatherSeedNum())) {
                    throw new BusinessException("CER的T1代种子无母本种植编号");
                }
                if (StringUtils.isNotEmpty(executeFormContent.getFatherSeedNum())) {
                    throw new BusinessException("CER的T1代种子无父本种植编号");
                }
                if (StringUtils.isEmpty(executeFormContent.getVectorTaskCode())) {
                    throw new BusinessException("CER的T1代种子请填写所属实施方案");
                }
                CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCode(executeFormContent.getPlantCode());
                if (cerPlantDtlTb == null) {
                    throw new BusinessException(executeFormContent.getPlantCode() + "种植编号不存在:" + executeFormContent.getPlantCode());
                }
            }
            //大田校验
            if (SOURCE_TYPE_4.equals(executeFormContent.getSource())) {
                if (StringUtils.isEmpty(executeFormContent.getFatherSeedNum())) {
                    throw new BusinessException("父本种子编号必填");
                }
                if (StringUtils.isEmpty(executeFormContent.getMatherSeedNum())) {
                    throw new BusinessException("母本种子编号必填");
                }
                if (StringUtils.isEmpty(executeFormContent.getFatherRegionNum())) {
                    throw new BusinessException("父本小区编号必填");
                }
                if (StringUtils.isEmpty(executeFormContent.getMatherRegionNum())) {
                    throw new BusinessException("母本小区编号必填");
                }
                if (StringUtils.isEmpty(executeFormContent.getExperimentNum())) {
                    throw new BusinessException("试验编号必填");
                }
                TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(executeFormContent.getExperimentNum());
                if (tcExperimentTb == null) {
                    throw new BusinessException("试验编号不存在：" + executeFormContent.getExperimentNum());
                }
                TcExperimentDesignTb matherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcExperimentTb.getExperimentNum(), executeFormContent.getMatherRegionNum(), executeFormContent.getMatherSeedNum());
                if (matherTcExperimentDesignTb == null) {
                    throw new BusinessException("试验方案中不存在此小区或者母本种子，当前试验方案编号：" + tcExperimentTb.getExperimentNum() + "母本小区编号：" + executeFormContent.getMatherRegionNum() + "母本种子编号:" + executeFormContent.getMatherSeedNum());
                }
                TcExperimentDesignTb fatherTcExperimentDesignTb = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcExperimentTb.getExperimentNum(), executeFormContent.getFatherRegionNum(), executeFormContent.getFatherSeedNum());
                if (fatherTcExperimentDesignTb == null) {
                    throw new BusinessException("试验方案中不存在此小区或者母本种子，当前试验方案编号：" + tcExperimentTb.getExperimentNum() + "父本小区编号：" + executeFormContent.getFatherRegionNum() + "父本种子编号:" + executeFormContent.getFatherSeedNum());
                }
                TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSampleCodeAndMSampleCode(executeFormContent.getExperimentNum(), executeFormContent.getFatherRegionNum(), executeFormContent.getMatherRegionNum(), executeFormContent.getFatherSeedNum(), executeFormContent.getMatherSeedNum(), executeFormContent.getFatherSingleNum(), executeFormContent.getMatherSingleNum());
                if(tcPollinationTb==null){
                    throw new BusinessException("无此授粉信息或者授粉信息不匹配：当前对应数据行的试验方案："+ executeFormContent.getExperimentNum()+" 父本种子编号："+ executeFormContent.getFatherSeedNum()+" 母本种子编号："+executeFormContent.getMatherSeedNum());
                }
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
