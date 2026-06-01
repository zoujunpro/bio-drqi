package com.bio.drqi.tc.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.drqi.common.contents.BioDrQiContents;
import com.bio.drqi.common.enums.*;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.service.dto.TcSampleTestTaskDTO;
import com.bio.flow.dto.BioHtmlModelDTO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service("tc_sample_test_task_apply")
public class TcSampleTestTaskService extends AbstractTcBaseTaskService {

    @Resource
    private BioSampleTestTbMapper bioSampleTestTbMapper;

    @Resource
    private BioSampleTestHisTbMapper bioSampleTestHisTbMapper;

    @Resource
    private BioSampleApplyTbMapper bioSampleApplyTbMapper;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestOneResultTbMapper bioSampleTestOneResultTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;

    @Resource
    private BioSampleTestResultFileTbMapper bioSampleTestResultFileTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private BioSampleLayoutTbMapper bioSampleLayoutTbMapper;

    @Resource
    private TcPollinationSingleNumTbMapper tcPollinationSingleNumTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcSampleTestTaskDTO tcSampleTestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcSampleTestTaskDTO.class);
        if (tcSampleTestTaskDTO == null) {
            throw new BusinessException("工单无表单信息");
        }
        ValidatorUtil.validator(tcSampleTestTaskDTO);

        if (StringUtils.isEmpty(tcSampleTestTaskDTO.getFirstSampleApplyList()) && StringUtils.isEmpty(tcSampleTestTaskDTO.getRepeatSampleApplyList())) {
            throw new BusinessException("缺少取样数据");
        }

        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcSampleTestTaskDTO.getExperimentNum());
        if (tcExperimentTb == null) {
            throw new BusinessException("不存在此试验");
        }

        if (!ExperimentStatusEnum.INIT.status.equals(tcExperimentTb.getExperimentStatus())) {
            throw new BusinessException("非进行中试验，无法进行任何操作");
        }

        //取样备注上区分是单管还是孔板取样
        if ("one".equals(tcSampleTestTaskDTO.getTestType())) {
            bioTaskDtlTb.setTaskDesc(bioTaskDtlTb.getTaskDesc() + "(单管取样)");
        } else {
            bioTaskDtlTb.setTaskDesc(bioTaskDtlTb.getTaskDesc() + "(96孔板取样)");
        }

        //插入数据库
        synchronized (this) {
            BioSampleApplyTb bioSampleApplyTb = bioSampleApplyTbMapper.selectOneByApplyNo(bioTaskDtlTb.getTaskNum());
            if (bioSampleApplyTb == null) {
                synchronized (this) {
                    bathInsertData(bioTaskDtlTb, tcSampleTestTaskDTO, tcSampleTestTaskDTO.getExperimentNum());
                    //如果是单管，则直接默认生成模板
                }
            }
        }

    }

    private void bathInsertData(BioTaskDtlTb bioTaskDtlTb, TcSampleTestTaskDTO tcSampleTestTaskDTO, String experimentNum) {
        BioSampleApplyTb bioSampleApplyTb = new BioSampleApplyTb();
        bioSampleApplyTb.setApplyNo(bioTaskDtlTb.getTaskNum());
        bioSampleApplyTb.setApplyNumber(0);
        bioSampleApplyTb.setApplyTime(new Date());
        bioSampleApplyTb.setApplyUserId(SecurityContextHolder.getUserId());
        bioSampleApplyTb.setApplyUserName(SecurityContextHolder.getNickName());
        bioSampleApplyTb.setApplyType(tcSampleTestTaskDTO.getApplyType());
        bioSampleApplyTb.setLayoutFlag(tcSampleTestTaskDTO.getTestType());
        bioSampleApplyTb.setSampleOrganize(tcSampleTestTaskDTO.getSampleOrganize());
        bioSampleApplyTb.setCloneFlag(BioDrQiContents.N);
        bioSampleApplyTb.setVectorTaskCodes(null);
        bioSampleApplyTb.setSampleCodeRange(null);
        bioSampleApplyTbMapper.insert(bioSampleApplyTb);

        List<BioSampleTestTb> batchList = new ArrayList<BioSampleTestTb>();
        List<TcPollinationSingleNumTb> tcPollinationSingleNumTbList = new ArrayList<TcPollinationSingleNumTb>();
        //首次取样
        if (CollectionUtil.isNotEmpty(tcSampleTestTaskDTO.getFirstSampleApplyList())) {
            //每一个小区的最大的大田取样编号后缀
            Map<String, Integer> reginofMaxSampleCodeNumberMap = queryReginOfMaxSampleCodeNumber(experimentNum);
            TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(experimentNum);
            //当前数据库中某一个试验方案取样编号后缀最大值
            List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByExperimentNum(experimentNum);
            Integer maxSampleNumber = null;
            if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
                maxSampleNumber = bioSampleTestTbList.stream().map(bioSampleTestTb -> Integer.valueOf(bioSampleTestTb.getSampleCode().substring(3))).max(Integer::compare).get();
            }
            for (int i = 0; i < tcSampleTestTaskDTO.getFirstSampleApplyList().size(); i++) {
                TcSampleTestTaskDTO.FirstSampleApply firstSampleApply = tcSampleTestTaskDTO.getFirstSampleApplyList().get(i);
                for (int j = 1; j <= firstSampleApply.getSampleNum(); j++) {
                    Integer nextTcSampleCodeNumber = reginofMaxSampleCodeNumberMap.get(firstSampleApply.getRegionNum()) == null ? 1 : reginofMaxSampleCodeNumberMap.get(firstSampleApply.getRegionNum()) + 1;
                    maxSampleNumber = maxSampleNumber == null ? 1 : maxSampleNumber + 1;
                    reginofMaxSampleCodeNumberMap.put(firstSampleApply.getRegionNum(), nextTcSampleCodeNumber);
                    BioSampleTestTb bioSampleTestTb = new BioSampleTestTb();
                    bioSampleTestTb.setVectorTaskCode(firstSampleApply.getVectorTaskCode());
                    bioSampleTestTb.setSampleCode(tcExperimentTb.getSampleCodePrefix() + maxSampleNumber);
                    bioSampleTestTb.setApplyTime(new Date());
                    bioSampleTestTb.setApplyUserId(bioTaskDtlTb.getApplyUserId());
                    bioSampleTestTb.setApplyUserName(bioTaskDtlTb.getApplyUserName());
                    bioSampleTestTb.setCreateTime(new Date());
                    bioSampleTestTb.setApplyNo(bioSampleApplyTb.getApplyNo());
                    bioSampleTestTb.setUniqueCode(bioSampleTestTb.getSampleCode());
                    bioSampleTestTb.setCheckResult(CheckResultEnum.noCheck.name());
                    bioSampleTestTb.setSourceCode(SourceCodeEnum.field.name());
                    bioSampleTestTb.setBreedCode(firstSampleApply.getBreedCode());
                    bioSampleTestTb.setSpeciesCode(firstSampleApply.getSpeciesCode());
                    bioSampleTestTb.setGeneration(GenerationEnum.T0.code);
                    bioSampleTestTb.setRegionNum(firstSampleApply.getRegionNum());
                    bioSampleTestTb.setSeedNum(firstSampleApply.getSeedNum());
                    bioSampleTestTb.setExperimentNum(experimentNum);
                    bioSampleTestTb.setTestResult(TestResultEnum.noTest.name());
                    bioSampleTestTb.setApplyTime(new Date());
                    bioSampleTestTb.setTcSampleCode(firstSampleApply.getRegionNum() + StringUtils.padl(nextTcSampleCodeNumber.toString(), 3, '0'));
                    batchList.add(bioSampleTestTb);

                    TcPollinationSingleNumTb tcPollinationSingleNumTb = TcPollinationSingleNumTb.of(experimentNum, bioSampleTestTb.getSeedNum(), bioSampleTestTb.getRegionNum(), bioSampleTestTb.getTcSampleCode(), bioSampleTestTb.getSampleCode(), bioSampleTestTb.getApplyNo(), bioSampleTestTb.getApplyUserName());
                    tcPollinationSingleNumTbList.add(tcPollinationSingleNumTb);

                }
            }

        }
        //重复取样
        if (CollectionUtil.isNotEmpty(tcSampleTestTaskDTO.getRepeatSampleApplyList())) {
            for (TcSampleTestTaskDTO.RepeatSampleApply repeatSampleApply : tcSampleTestTaskDTO.getRepeatSampleApplyList()) {
                List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllBySampleCode(repeatSampleApply.getSampleCode());
                if (CollectionUtil.isEmpty(bioSampleTestTbList)) {
                    throw new BusinessException("没找到历史取样信息" + repeatSampleApply.getSampleCode());
                }
                BioSampleTestTb bioSampleTestTb = BioSampleTestTb.ofRepeat(bioSampleTestTbList.get(0), bioTaskDtlTb, CheckResultEnum.noCheck, TestResultEnum.noTest);
                batchList.add(bioSampleTestTb);
            }
        }
        //如果是首次取样，更新取样区间
        if (SampleTestApplyTypeEnum.first.name().equals(tcSampleTestTaskDTO.getApplyType())) {
            StringBuffer sampleCodeRangeBuff = new StringBuffer();
            Map<String, List<BioSampleTestTb>> plantSampleTestTbMap = batchList.stream().collect(Collectors.groupingBy(sampleTestTb -> sampleTestTb.getSampleCode().replaceAll("\\d", "")));
            plantSampleTestTbMap.forEach((sampleCodePrefix, sampleTestList) -> {
                sampleTestList = sampleTestList.stream().filter(sampleTest -> sampleTest.getSampleCode().startsWith(sampleCodePrefix)).sorted(Comparator.comparing(sampleTest -> Integer.valueOf(sampleTest.getSampleCode().substring(sampleCodePrefix.length())))).collect(Collectors.toList());
                if (CollectionUtil.isNotEmpty(sampleTestList)) {
                    sampleCodeRangeBuff.append(sampleTestList.get(0).getSampleCode() + "-" + sampleTestList.get(sampleTestList.size() - 1).getSampleCode()).append(",");
                }
            });
            if (StringUtils.isNotEmpty(sampleCodeRangeBuff.toString())) {
                bioSampleApplyTb.setSampleCodeRange(sampleCodeRangeBuff.substring(0, sampleCodeRangeBuff.length() - 1));
                bioSampleApplyTb.setVectorTaskCodes(JSONUtil.toJsonStr(batchList.stream().filter(plantSampleTestTb -> StringUtils.isNotEmpty(plantSampleTestTb.getVectorTaskCode())).map(BioSampleTestTb::getVectorTaskCode).distinct().collect(Collectors.toList())).replace("[", "").replace("]", "").replace("\"", ""));
            }
            bioSampleApplyTb.setApplyNumber(tcSampleTestTaskDTO.getFirstSampleApplyList().stream().map(TcSampleTestTaskDTO.FirstSampleApply::getSampleNum).mapToInt(Integer::intValue).sum());
        } else {
            bioSampleApplyTb.setApplyNumber(tcSampleTestTaskDTO.getRepeatSampleApplyList().size());
        }
        if (CollectionUtil.isNotEmpty(tcPollinationSingleNumTbList)) {
            tcPollinationSingleNumTbMapper.insertBatch(tcPollinationSingleNumTbList);
        }

        bioSampleApplyTbMapper.updateById(bioSampleApplyTb);
        bioSampleTestTbMapper.insertBatch(batchList);
    }

    private Map<String, Integer> queryReginOfMaxSampleCodeNumber(String experimentNum) {
        Map<String, Integer> reginofMaxSampleCodeNumberMap = new HashMap<>();
        Map<String, List<String>> reginofMaxSampleCodeListMap = new HashMap<>();
        List<TcPollinationSingleNumTb> tcPollinationSingleNumTbList = tcPollinationSingleNumTbMapper.selectAllByExperimentNumOrderByIdDesc(experimentNum);
        if (CollectionUtil.isNotEmpty(tcPollinationSingleNumTbList)) {
            tcPollinationSingleNumTbList.forEach(tcPollinationSingleNumTb -> {
                if (reginofMaxSampleCodeListMap.get(tcPollinationSingleNumTb.getRegionNum()) == null) {
                    List<String> tcSingleNumberList=new ArrayList<>();
                    tcSingleNumberList.add(tcPollinationSingleNumTb.getTcSingleNumber());
                    reginofMaxSampleCodeListMap.put(tcPollinationSingleNumTb.getRegionNum(), tcSingleNumberList);
                } else {
                    reginofMaxSampleCodeListMap.get(tcPollinationSingleNumTb.getRegionNum()).add(tcPollinationSingleNumTb.getTcSingleNumber());
                }
            });
        }
        reginofMaxSampleCodeListMap.forEach((reginNum, tcSampleCodeList) -> {
            reginofMaxSampleCodeNumberMap.put(reginNum, tcSampleCodeList.stream().distinct().map(tcSampleCode -> Integer.valueOf(tcSampleCode.substring(reginNum.length()))).max(Integer::compare).get());
        });
        return reginofMaxSampleCodeNumberMap;
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            BioSampleApplyTb bioSampleApplyTb = bioSampleApplyTbMapper.selectOneByApplyNo(bioTaskDtlTb.getTaskNum());
            String currentTime = DateUtil.format(new Date(), DatePattern.NORM_DATETIME_PATTERN);
            bioSampleTestTbMapper.updateNoCheckDataByApplyNoAndCheckResult(CheckResultEnum.remove.name(), SecurityContextHolder.getUserId(), SecurityContextHolder.getNickName(), currentTime, SecurityContextHolder.getUserId(), SecurityContextHolder.getNickName(), currentTime, TestResultEnum.noResult.name(), bioSampleApplyTb.getApplyNo(), CheckResultEnum.noCheck.name());
        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        List<BioSampleTestTb> bioSampleTestTbList = bioSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(bioSampleTestTbList)) {
            bioSampleTestHisTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
            bioSampleTestHisTbMapper.insertBatch(BeanUtils.copyListProperties(bioSampleTestTbList, BioSampleTestHisTb.class));

        }

        bioSampleApplyTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        bioSampleTestTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        bioSampleLayoutTbMapper.deleteByApplyNo(bioTaskDtlTb.getTaskNum());
        List<BioSampleTestTwoResultTb> bioSampleSampleTwoResultTbList = bioSampleTestTwoResultTbMapper.selectAllByUploadNum(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(bioSampleSampleTwoResultTbList)) {
            bioSampleTestTwoResultTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
            bioSampleSampleTwoResultTbList.forEach(bioSampleSampleTwoResultTb -> {
                bioSampleTestTwoResultDetailTbMapper.deleteByApplyNoAndSampleCode(bioSampleSampleTwoResultTb.getApplyNo(), bioSampleSampleTwoResultTb.getSampleCode());
            });
        }
        bioSampleTestResultFileTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
        bioSampleTestOneResultTbMapper.deleteByUploadNum(bioTaskDtlTb.getTaskNum());
        tcPollinationSingleNumTbMapper.deleteBySampleApplyNum(bioTaskDtlTb.getTaskNum());
    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        TcSampleTestTaskDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcSampleTestTaskDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        TcExperimentTb tcExperimentTb = StringUtils.isEmpty(dto.getExperimentNum()) ? null : tcExperimentTbMapper.selectOneByExperimentNum(dto.getExperimentNum());

        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("试验编号", dto.getExperimentNum()));
        applyFields.add(buildField("取样类型", sampleApplyTypeName(dto.getApplyType())));
        applyFields.add(buildField("取样方式", testTypeName(dto.getTestType())));
        applyFields.add(buildField("取样组织", dto.getSampleOrganize()));
        applyFields.add(buildField("预计取样时间", dto.getExpectedSampleTime()));
        applyFields.add(buildField("预计结果返回时间", dto.getExpectedResultTime()));
        applyFields.add(buildField("取样编号前缀", tcExperimentTb == null ? "" : tcExperimentTb.getSampleCodePrefix()));
        sections.add(buildFieldSection("申请信息", applyFields));

        if (CollectionUtil.isNotEmpty(dto.getFirstSampleApplyList())) {
            List<String> headers = Arrays.asList("小区编号", "种子编号", "实施方案编号", "品种", "代次", "基因型", "目标性状", "取样数量", "取样时间");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (TcSampleTestTaskDTO.FirstSampleApply item : dto.getFirstSampleApplyList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("小区编号", item.getRegionNum());
                row.put("种子编号", item.getSeedNum());
                row.put("实施方案编号", item.getVectorTaskCode());
                row.put("品种", item.getBreedName());
                row.put("代次", item.getGenerationCode());
                row.put("基因型", item.getTcGene());
                row.put("目标性状", item.getTargetCharacter());
                row.put("取样数量", item.getSampleNum());
                row.put("取样时间", item.getSampleTime());
                rows.add(row);
            }
            sections.add(buildTableSection("首次取样申请明细", headers, rows));
        }

        if (CollectionUtil.isNotEmpty(dto.getRepeatSampleApplyList())) {
            List<String> headers = Arrays.asList("取样编号", "小区编号", "种子编号", "实施方案编号", "品种", "代次", "基因型", "目标性状", "取样时间");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (TcSampleTestTaskDTO.RepeatSampleApply item : dto.getRepeatSampleApplyList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("取样编号", item.getSampleCode());
                row.put("小区编号", item.getRegionNum());
                row.put("种子编号", item.getSeedNum());
                row.put("实施方案编号", item.getVectorTaskCode());
                row.put("品种", item.getBreedName());
                row.put("代次", item.getGenerationCode());
                row.put("基因型", item.getTcGene());
                row.put("目标性状", item.getTargetCharacter());
                row.put("取样时间", item.getSampleTime());
                rows.add(row);
            }
            sections.add(buildTableSection("重复取样申请明细", headers, rows));
        }

        List<BioSampleTestTb> sampleList = bioSampleTestTbMapper.selectAllByApplyNo(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(sampleList)) {
            Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
            List<String> headers = Arrays.asList("取样编号", "田测单株编号", "小区编号", "种子编号", "实施方案编号", "品种", "代次", "检测结果", "审核结果", "检测人");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (BioSampleTestTb item : sampleList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("取样编号", item.getSampleCode());
                row.put("田测单株编号", item.getTcSampleCode());
                row.put("小区编号", item.getRegionNum());
                row.put("种子编号", item.getSeedNum());
                row.put("实施方案编号", item.getVectorTaskCode());
                row.put("品种", breedNameMap.getOrDefault(item.getBreedCode(), item.getBreedCode()));
                row.put("代次", item.getGeneration());
                row.put("检测结果", testResultName(item.getTestResult()));
                row.put("审核结果", checkResultName(item.getCheckResult()));
                row.put("检测人", item.getTestUserName());
                rows.add(row);
            }
            sections.add(buildTableSection("取样信息明细", headers, rows));
        }

        return sections;
    }

    private String sampleApplyTypeName(String code) {
        if (SampleTestApplyTypeEnum.first.name().equals(code)) {
            return "首次取样";
        }
        if (SampleTestApplyTypeEnum.repeat.name().equals(code)) {
            return "重复取样";
        }
        return code;
    }

    private String testTypeName(String code) {
        if ("one".equals(code)) {
            return "单管取样";
        }
        if ("more".equals(code)) {
            return "96孔板取样";
        }
        return code;
    }

    private String testResultName(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        if (TestResultEnum.noTest.name().equals(code)) {
            return "未检测";
        }
        if (TestResultEnum.noResult.name().equals(code)) {
            return "无结果";
        }
        if (TestResultEnum.haveResult.name().equals(code)) {
            return "已有结果";
        }
        return code;
    }

    private String checkResultName(String code) {
        if (StringUtils.isEmpty(code)) {
            return "";
        }
        if (CheckResultEnum.stay.name().equals(code)) {
            return "保留";
        }
        if (CheckResultEnum.remove.name().equals(code)) {
            return "剔除";
        }
        if (CheckResultEnum.noCheck.name().equals(code)) {
            return "未审核";
        }
        return code;
    }
}
