package com.bio.drqi.tc.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioDictTypeEnum;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.*;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.service.dto.TcPollinationExcelDTO;
import com.bio.drqi.tc.service.dto.TcPollinationTaskDTO;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;


@Service("tc_pollination_task_apply")
@Slf4j
public class TcPollinationTaskService extends AbstractTcBaseTaskService {

    @Resource
    private OssService ossService;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;


    @Resource
    private TcPollinationApplyTbMapper tcPollinationApplyTbMapper;

    @Resource
    private TcExperimentDesignTbMapper tcExperimentDesignTbMapper;

    @Resource
    private TcPollinationSingleNumTbMapper tcPollinationSingleNumTbMapper;


    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private BioDictMapper bioDictMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;


    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
        ValidatorUtil.validator(tcPollinationTaskDTO);
        BeanUtils.trimFiledSpace(tcPollinationTaskDTO);

        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcPollinationTaskDTO.getExperimentNum());
        if (tcExperimentTb == null) {
            throw new BusinessException("不存在此试验");
        }
        if (!ExperimentStatusEnum.INIT.status.equals(tcExperimentTb.getExperimentStatus())) {
            throw new BusinessException("非进行中试验，无法进行任何操作");
        }
        if (tcExperimentTb.getHarvestApplyNum() != null) {
            throw new BusinessException("该试验已经收获，无法再授粉");
        }
        BioDict bioDict = bioDictMapper.selectOneByDictTypeAndDictValueCode(BioDictTypeEnum.POLLINATE_TYPE.name(), tcPollinationTaskDTO.getPollinationType());
        if (bioDict == null) {
            throw new BusinessException("授粉方式错误");
        }

        if (!tcPollinationTaskDTO.getPollinationExcelUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + tcPollinationTaskDTO.getPollinationExcelUrl();
        try {
            ossService.downloadPath(tempFilePath, tcPollinationTaskDTO.getPollinationExcelUrl());
        } catch (Exception e) {
            log.error("【任务工单】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<TcPollinationExcelDTO> tcPollinationExcelDTOList = ExcelUtil.readExcel(tempFilePath, TcPollinationExcelDTO.class);
        for (TcPollinationExcelDTO tcPollinationExcelDTO : tcPollinationExcelDTOList) {
            //校验1:必填项校验
            ValidatorUtil.validator(tcPollinationTaskDTO);
            //校验2:父本存在性校验
            TcExperimentDesignTb father = tcExperimentDesignTbMapper.selectOneByRegionNumAndSeedNum(tcPollinationExcelDTO.getFatherRegionNum(), tcPollinationExcelDTO.getFatherSeedNum());
            if (father == null) {
                throw new BusinessException("试验中无此区域为：" + tcPollinationExcelDTO.getFatherRegionNum() + "的种子编号:" + tcPollinationExcelDTO.getFatherSeedNum());
            }
            //校验3:父本大田取样编号校验
            if (StringUtils.isNotEmpty(tcPollinationExcelDTO.getFatherSingleNumber())) {
                TcPollinationSingleNumTb tcPollinationSingleNumTb = tcPollinationSingleNumTbMapper.selectOneByExperimentNumAndTcSingleNumber(tcPollinationTaskDTO.getExperimentNum(), tcPollinationExcelDTO.getFatherSingleNumber());
                if (tcPollinationSingleNumTb == null) {
                    throw new BusinessException("大田单珠编号为：" + tcPollinationExcelDTO.getFatherSingleNumber() + "的父本查询不存在");
                }

                if (!StringUtils.equals(tcPollinationSingleNumTb.getSeedNum(), tcPollinationExcelDTO.getFatherSeedNum())) {
                    throw new BusinessException("大田单株编号编号为：" + tcPollinationExcelDTO.getFatherSingleNumber() + "的父本对应的种子编号错误，应为：" + tcPollinationSingleNumTb.getSeedNum());
                }
                if (!StringUtils.equals(tcPollinationSingleNumTb.getRegionNum(), tcPollinationExcelDTO.getFatherRegionNum())) {
                    throw new BusinessException("大田单株编号编号为：" + tcPollinationExcelDTO.getFatherSingleNumber() + "的父本对应的小区编应号错误，为：" + tcPollinationSingleNumTb.getRegionNum());
                }
                if (!StringUtils.equals(tcPollinationSingleNumTb.getSampleCode(), tcPollinationExcelDTO.getFatherSampleCode())) {
                    throw new BusinessException("大田单株编号编号为：" + tcPollinationExcelDTO.getFatherSingleNumber() + "的父本对应的取样编号错误，应为：" + tcPollinationSingleNumTb.getSampleCode());
                }

            }
            //校验5:母本存在性校验
            TcExperimentDesignTb mother = tcExperimentDesignTbMapper.selectOneByRegionNumAndSeedNum(tcPollinationExcelDTO.getMotherRegionNum(), tcPollinationExcelDTO.getMotherSeedNum());
            if (mother == null) {
                throw new BusinessException("试验中无此区域为：" + tcPollinationExcelDTO.getMotherRegionNum() + "的种子编号:" + tcPollinationExcelDTO.getMotherSeedNum());
            }
            //校验6:母本大田编号校验
            if (StringUtils.isNotEmpty(tcPollinationExcelDTO.getMotherSingleNumber())) {
                TcPollinationSingleNumTb tcPollinationSingleNumTb = tcPollinationSingleNumTbMapper.selectOneByExperimentNumAndTcSingleNumber(tcPollinationTaskDTO.getExperimentNum(), tcPollinationExcelDTO.getMotherSingleNumber());
                if (tcPollinationSingleNumTb == null) {
                    throw new BusinessException("大田单珠编号为：" + tcPollinationExcelDTO.getMotherSingleNumber() + "的母本查询不到取样信息");
                }
                if (!StringUtils.equals(tcPollinationSingleNumTb.getSeedNum(), tcPollinationExcelDTO.getMotherSeedNum())) {
                    throw new BusinessException("大田单珠编号为：" + tcPollinationExcelDTO.getMotherSingleNumber() + "的母本对应的种子编号错误，应为：" + tcPollinationSingleNumTb.getSeedNum());
                }
                if (!StringUtils.equals(tcPollinationSingleNumTb.getRegionNum(), tcPollinationExcelDTO.getMotherRegionNum())) {
                    throw new BusinessException("大田单珠编号为：" + tcPollinationExcelDTO.getMotherSingleNumber() + "的母本对应的小区编应号错误，为：" + tcPollinationSingleNumTb.getRegionNum());
                }
                if (!StringUtils.equals(tcPollinationSingleNumTb.getSampleCode(), tcPollinationExcelDTO.getMotherSampleCode())) {
                    throw new BusinessException("大田单珠编号为：" + tcPollinationExcelDTO.getMotherSingleNumber() + "的母本对应的取样编号错误，应为：" + tcPollinationSingleNumTb.getSampleCode());
                }

            }
            //校验6: 收获方式校验
            BioDict harvestTypeDict = bioDictMapper.selectOneByDictTypeAndDictValueName(BioDictTypeEnum.HARVEST_TYPE.name(), tcPollinationExcelDTO.getHarvestTypeName());
            if (harvestTypeDict == null) {
                throw new BusinessException("收获方式填写错误：" + tcPollinationExcelDTO.getHarvestTypeName());
            }
            tcPollinationExcelDTO.setHarvestTypeCode(harvestTypeDict.getDictValueCode());

            //校验7:授粉中母本只能授粉一次
            TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndMRegionNumAndMSeedNumAndMSingleNumber(tcPollinationTaskDTO.getExperimentNum(), tcPollinationExcelDTO.getMotherRegionNum(), tcPollinationExcelDTO.getMotherSeedNum(), tcPollinationExcelDTO.getMotherSampleCode());
            if (tcPollinationTb != null) {
                throw new BusinessException("小区编号：" + tcPollinationExcelDTO.getMotherRegionNum() + " 种子编号：" + tcPollinationExcelDTO.getMotherSeedNum() + (StringUtils.isNotEmpty(tcPollinationExcelDTO.getMotherSampleCode()) ? "取样编号:" + tcPollinationExcelDTO.getMotherSampleCode() : "") + "的母本已经受过粉");
            }

            tcPollinationExcelDTO.setFatherBreedCode(father.getBreedCode());
            tcPollinationExcelDTO.setMotherBreedCode(mother.getBreedCode());
        }

        tcPollinationTaskDTO.setTcPollinationExcelDTOList(tcPollinationExcelDTOList);
        tcPollinationTaskDTO.setPollinationTypeName(bioDict.getDictValueName());
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcPollinationTaskDTO));

        //更新授粉单株编号
        tcPollinationSingleNumTbMapper.updatePollinationApplyNumByExperimentNumAndPollinationApplyNumIsNull(bioTaskDtlTb.getTaskNum(), tcPollinationTaskDTO.getExperimentNum());


    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcPollinationTaskDTO tcPollinationTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            TcPollinationApplyTb tcPollinationApplyTb = new TcPollinationApplyTb();
            tcPollinationApplyTb.setExperimentNum(tcPollinationTaskDTO.getExperimentNum());
            tcPollinationApplyTb.setSampleApplyNum(tcPollinationTaskDTO.getSampleApplyNum());
            tcPollinationApplyTb.setPollinationType(tcPollinationTaskDTO.getPollinationType());
            tcPollinationApplyTb.setPollinationApplyNum(bioTaskDtlTb.getTaskNum());
            tcPollinationApplyTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            tcPollinationApplyTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
            tcPollinationApplyTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
            tcPollinationApplyTb.setCreateTime(new Date());
            tcPollinationApplyTb.setPollinationExcelUrl(tcPollinationTaskDTO.getPollinationExcelUrl());
            tcPollinationApplyTbMapper.insert(tcPollinationApplyTb);


            List<TcPollinationTb> tcPollinationTbList = new ArrayList<>();
            for (TcPollinationExcelDTO tcPollinationExcelDTO : tcPollinationTaskDTO.getTcPollinationExcelDTOList()) {
                ValidatorUtil.validator(tcPollinationTaskDTO);
                TcPollinationTb tcPollinationTb = new TcPollinationTb();
                tcPollinationTb.setExperimentNum(tcPollinationTaskDTO.getExperimentNum());
                tcPollinationTb.setSampleApplyNum(tcPollinationTaskDTO.getSampleApplyNum());
                tcPollinationTb.setPollinationApplyNum(bioTaskDtlTb.getTaskNum());
                tcPollinationTb.setMRegionNum(tcPollinationExcelDTO.getMotherRegionNum());
                tcPollinationTb.setFRegionNum(tcPollinationExcelDTO.getFatherRegionNum());
                tcPollinationTb.setMSampleCode(tcPollinationExcelDTO.getMotherSampleCode());
                tcPollinationTb.setFSampleCode(tcPollinationExcelDTO.getFatherSampleCode());
                tcPollinationTb.setFSingleNumber(tcPollinationExcelDTO.getFatherSingleNumber());
                tcPollinationTb.setMSingleNumber(tcPollinationExcelDTO.getMotherSingleNumber());
                tcPollinationTb.setMSeedNum(tcPollinationExcelDTO.getMotherSeedNum());
                tcPollinationTb.setFSeedNum(tcPollinationExcelDTO.getFatherSeedNum());
                tcPollinationTb.setFBreedCode(tcPollinationExcelDTO.getFatherBreedCode());
                tcPollinationTb.setMBreedCode(tcPollinationExcelDTO.getMotherBreedCode());
                tcPollinationTb.setMVectorTaskCode(tcPollinationExcelDTO.getMotherVectorTaskCode());
                tcPollinationTb.setFVectorTaskCode(tcPollinationExcelDTO.getFatherVectorTaskCode());
                tcPollinationTb.setMGenerationCode(tcPollinationExcelDTO.getMotherGenerationName());
                tcPollinationTb.setFGenerationCode(tcPollinationExcelDTO.getFatherGenerationName());
                tcPollinationTb.setMTcGene(tcPollinationExcelDTO.getMotherTcGene());
                tcPollinationTb.setFTcGene(tcPollinationExcelDTO.getFatherTcGene());
                tcPollinationTb.setPollinationDate(tcPollinationExcelDTO.getPollinationDate());
                tcPollinationTb.setPollinationMethodCode(tcPollinationTaskDTO.getPollinationType());
                tcPollinationTb.setPollinationMethodName(tcPollinationTaskDTO.getPollinationTypeName());
                tcPollinationTb.setHarvestTypeName(tcPollinationExcelDTO.getHarvestTypeName());
                tcPollinationTb.setHarvestTypeCode(tcPollinationExcelDTO.getHarvestTypeCode());
                tcPollinationTb.setRemark(tcPollinationExcelDTO.getRemark());
                tcPollinationTbList.add(tcPollinationTb);
            }
            try {
                tcPollinationTbMapper.insertBatch(tcPollinationTbList);
            } catch (DuplicateKeyException e) {
                log.error("重复授粉：", e);
                throw new BusinessException("有重复授粉数据");

            }
        }
    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {
        tcPollinationSingleNumTbMapper.updatePollinationApplyNumIsNullByPollinationApplyNum(bioTaskDtlTb.getTaskNum());

    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        TcPollinationTaskDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcPollinationTaskDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("试验编号", dto.getExperimentNum()));
        applyFields.add(buildField("取样批次号", dto.getSampleApplyNum()));
        applyFields.add(buildField("授粉方式", dto.getPollinationTypeName()));
        sections.add(buildFieldSection("申请信息", applyFields));

        List<TcPollinationTb> pollinationList = tcPollinationTbMapper.selectAllByPollinationApplyNum(bioTaskDtlTb.getTaskNum());
        if (CollectionUtil.isNotEmpty(pollinationList)) {
            Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
            List<String> headers = Arrays.asList("母本小区编号", "母本种子编号", "母本单株编号", "母本取样编号", "母本品种", "母本实施方案编号", "父本小区编号", "父本种子编号", "父本单株编号", "父本取样编号", "父本品种", "父本实施方案编号", "授粉时间", "授粉方式", "收获方式", "备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (TcPollinationTb item : pollinationList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("母本小区编号", item.getMRegionNum());
                row.put("母本种子编号", item.getMSeedNum());
                row.put("母本单株编号", item.getMSingleNumber());
                row.put("母本取样编号", item.getMSampleCode());
                row.put("母本品种", breedNameMap.getOrDefault(item.getMBreedCode(), item.getMBreedCode()));
                row.put("母本实施方案编号", item.getMVectorTaskCode());
                row.put("父本小区编号", item.getFRegionNum());
                row.put("父本种子编号", item.getFSeedNum());
                row.put("父本单株编号", item.getFSingleNumber());
                row.put("父本取样编号", item.getFSampleCode());
                row.put("父本品种", breedNameMap.getOrDefault(item.getFBreedCode(), item.getFBreedCode()));
                row.put("父本实施方案编号", item.getFVectorTaskCode());
                row.put("授粉时间", item.getPollinationDate());
                row.put("授粉方式", item.getPollinationMethodName());
                row.put("收获方式", item.getHarvestTypeName());
                row.put("备注", item.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("授粉明细", headers, rows));
            return sections;
        }

        if (CollectionUtil.isNotEmpty(dto.getTcPollinationExcelDTOList())) {
            List<String> headers = Arrays.asList("母本小区编号", "母本种子编号", "母本单株编号", "母本取样编号", "母本品种", "母本实施方案编号", "父本小区编号", "父本种子编号", "父本单株编号", "父本取样编号", "父本品种", "父本实施方案编号", "授粉时间", "收获方式", "备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (TcPollinationExcelDTO item : dto.getTcPollinationExcelDTOList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("母本小区编号", item.getMotherRegionNum());
                row.put("母本种子编号", item.getMotherSeedNum());
                row.put("母本单株编号", item.getMotherSingleNumber());
                row.put("母本取样编号", item.getMotherSampleCode());
                row.put("母本品种", item.getMotherBreedName());
                row.put("母本实施方案编号", item.getMotherVectorTaskCode());
                row.put("父本小区编号", item.getFatherRegionNum());
                row.put("父本种子编号", item.getFatherSeedNum());
                row.put("父本单株编号", item.getFatherSingleNumber());
                row.put("父本取样编号", item.getFatherSampleCode());
                row.put("父本品种", item.getFatherBreedName());
                row.put("父本实施方案编号", item.getFatherVectorTaskCode());
                row.put("授粉时间", item.getPollinationDate());
                row.put("收获方式", item.getHarvestTypeName());
                row.put("备注", item.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("授粉明细", headers, rows));
        }

        return sections;
    }
}
