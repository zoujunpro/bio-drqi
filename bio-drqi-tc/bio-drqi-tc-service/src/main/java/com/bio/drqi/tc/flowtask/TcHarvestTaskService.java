package com.bio.drqi.tc.flowtask;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.core.util.ValidatorUtil;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.mapper.CerBreedDictMapper;
import com.bio.drqi.mapper.TcExperimentTbMapper;
import com.bio.drqi.mapper.TcHarvestSeedApplyTbMapper;
import com.bio.drqi.mapper.TcPollinationTbMapper;
import com.bio.drqi.tc.enums.ExperimentStatusEnum;
import com.bio.drqi.tc.service.dto.TcHarvestExcelDTO;
import com.bio.drqi.tc.service.dto.TcHarvestTaskDTO;
import com.bio.flow.dto.BioHtmlModelDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("tc_harvest_task_apply")
@Slf4j
public class TcHarvestTaskService extends AbstractTcBaseTaskService {

    @Resource
    private OssService ossService;

    @Resource
    private TcHarvestSeedApplyTbMapper  tcHarvestSeedApplyTbMapper;


    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private TcPollinationTbMapper tcPollinationTbMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Override
    public void taskApply(BioTaskDtlTb bioTaskDtlTb) {
        TcHarvestTaskDTO tcHarvestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcHarvestTaskDTO.class);
        ValidatorUtil.validator(tcHarvestTaskDTO);
        BeanUtils.trimFiledSpace(tcHarvestTaskDTO);

        TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcHarvestTaskDTO.getExperimentNum());
        if (tcExperimentTb == null) {
            throw new BusinessException("不存在此试验");
        }
        if(!ExperimentStatusEnum.INIT.status.equals(tcExperimentTb.getExperimentStatus())){
            throw  new BusinessException("非进行中试验，无法进行任何操作");
        }
        if(StringUtils.isNotEmpty(tcExperimentTb.getHarvestApplyNum())){
            throw new BusinessException("此试验已经收获");
        }

        if (!tcHarvestTaskDTO.getHarvestFileUrl().endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + tcHarvestTaskDTO.getHarvestFileUrl();
        try {
            ossService.downloadPath(tempFilePath, tcHarvestTaskDTO.getHarvestFileUrl());
        } catch (Exception e) {
            log.error("【任务工单】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<TcHarvestExcelDTO> tcHarvestExcelDTOList = ExcelUtil.readExcel(tempFilePath, TcHarvestExcelDTO.class);
        List<TcPollinationTb> tcPollinationTbList = tcPollinationTbMapper.selectAllByExperimentNum(tcExperimentTb.getExperimentNum());
        if (tcPollinationTbList.size() != tcHarvestExcelDTOList.size()) {
            throw new BusinessException("收获总数据和授粉数不匹配，请核实收获内容");
        }
        for (TcHarvestExcelDTO tcHarvestExcelDTO : tcHarvestExcelDTOList) {
            TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSingleNumberAndMSingleNumber
                    (tcExperimentTb.getExperimentNum(),
                            tcHarvestExcelDTO.getFatherRegionNum(),
                            tcHarvestExcelDTO.getMotherRegionNum(),
                            tcHarvestExcelDTO.getFatherSeedNum(),
                            tcHarvestExcelDTO.getMotherSeedNum(),
                            tcHarvestExcelDTO.getFatherSingleNumber(),
                            tcHarvestExcelDTO.getMotherSingleNumber());
            if (tcPollinationTb == null) {
                throw new BusinessException("无此授粉记录：" + JSONUtil.toJsonStr(tcHarvestExcelDTO));
            }
        }
        tcHarvestTaskDTO.setTcHarvestExcelDTOList(tcHarvestExcelDTOList);
        bioTaskDtlTb.setTaskForm(JSONUtil.toJsonStr(tcHarvestTaskDTO));
    }

    @Override
    public void executeTask(BioTaskDtlTb bioTaskDtlTb) {
        TcHarvestTaskDTO tcHarvestTaskDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcHarvestTaskDTO.class);
        if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskStatus())) {
            TcExperimentTb tcExperimentTb = tcExperimentTbMapper.selectOneByExperimentNum(tcHarvestTaskDTO.getExperimentNum());
            tcExperimentTb.setHarvestApplyNum(bioTaskDtlTb.getTaskNum());
            tcExperimentTbMapper.updateById(tcExperimentTb);

            TcHarvestSeedApplyTb tcHarvestSeedApplyTb=new TcHarvestSeedApplyTb();
            tcHarvestSeedApplyTb.setTaskNum(bioTaskDtlTb.getTaskNum());
            tcHarvestSeedApplyTb.setHarvestApplyNum(bioTaskDtlTb.getTaskNum());
            tcHarvestSeedApplyTb.setHarvestTime(tcHarvestTaskDTO.getHarvestTime());
            tcHarvestSeedApplyTb.setCreateTime(new Date());
            tcHarvestSeedApplyTb.setCreateUserId(bioTaskDtlTb.getApplyUserId());
            tcHarvestSeedApplyTb.setCreateUserName(bioTaskDtlTb.getApplyUserName());
            tcHarvestSeedApplyTb.setExperimentNum(tcExperimentTb.getExperimentNum());
            tcHarvestSeedApplyTb.setHarvestFileUrl(tcHarvestTaskDTO.getHarvestFileUrl());
            tcHarvestSeedApplyTbMapper.insert(tcHarvestSeedApplyTb);

            for (TcHarvestExcelDTO tcHarvestExcelDTO : tcHarvestTaskDTO.getTcHarvestExcelDTOList()) {
                TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndFRegionNumAndMRegionNumAndFSeedNumAndMSeedNumAndFSingleNumberAndMSingleNumber
                        (tcExperimentTb.getExperimentNum(),
                                tcHarvestExcelDTO.getFatherRegionNum(),
                                tcHarvestExcelDTO.getMotherRegionNum(),
                                tcHarvestExcelDTO.getFatherSeedNum(),
                                tcHarvestExcelDTO.getMotherSeedNum(),
                                tcHarvestExcelDTO.getFatherSingleNumber(),
                                tcHarvestExcelDTO.getMotherSingleNumber());
                if (tcPollinationTb == null) {
                    throw new BusinessException("无此授粉记录：" + JSONUtil.toJsonStr(tcHarvestExcelDTO));
                }
                tcPollinationTb.setHarvestRemark(tcHarvestExcelDTO.getRemark());
                tcPollinationTb.setHarvestApplyNum(tcHarvestSeedApplyTb.getHarvestApplyNum());
                tcPollinationTb.setUnit(tcHarvestExcelDTO.getUnit());
                tcPollinationTb.setSeedNumber(new BigDecimal(StringUtils.isEmpty(tcHarvestExcelDTO.getSeedNumber()) ? "0" : tcHarvestExcelDTO.getSeedNumber()));
                tcPollinationTbMapper.updateById(tcPollinationTb);
            }


        }

    }

    @Override
    public void cancelTask(BioTaskDtlTb bioTaskDtlTb) {

    }

    @Override
    public List<BioHtmlModelDTO.ModelSection> getSections(BioTaskDtlTb bioTaskDtlTb) {
        TcHarvestTaskDTO dto = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), TcHarvestTaskDTO.class);
        if (dto == null) {
            return Collections.emptyList();
        }

        List<BioHtmlModelDTO.ModelSection> sections = new ArrayList<>();
        List<BioHtmlModelDTO.ModelField> applyFields = new ArrayList<>();
        applyFields.add(buildField("试验编号", dto.getExperimentNum()));
        applyFields.add(buildField("收获时间", dto.getHarvestTime()));
        sections.add(buildFieldSection("申请信息", applyFields));

        TcPollinationTb query = new TcPollinationTb();
        query.setHarvestApplyNum(bioTaskDtlTb.getTaskNum());
        List<TcPollinationTb> pollinationList = tcPollinationTbMapper.selectSelective(query);
        if (CollectionUtil.isNotEmpty(pollinationList)) {
            Map<String, String> breedNameMap = cerBreedDictMapper.selectAll().stream()
                    .collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName, (left, right) -> left));
            List<String> headers = java.util.Arrays.asList("母本小区编号", "母本种子编号", "母本单株编号", "母本取样编号", "母本品种", "父本小区编号", "父本种子编号", "父本单株编号", "父本取样编号", "父本品种", "收获数量", "收获方式", "收获备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (TcPollinationTb item : pollinationList) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("母本小区编号", item.getMRegionNum());
                row.put("母本种子编号", item.getMSeedNum());
                row.put("母本单株编号", item.getMSingleNumber());
                row.put("母本取样编号", item.getMSampleCode());
                row.put("母本品种", breedNameMap.getOrDefault(item.getMBreedCode(), item.getMBreedCode()));
                row.put("父本小区编号", item.getFRegionNum());
                row.put("父本种子编号", item.getFSeedNum());
                row.put("父本单株编号", item.getFSingleNumber());
                row.put("父本取样编号", item.getFSampleCode());
                row.put("父本品种", breedNameMap.getOrDefault(item.getFBreedCode(), item.getFBreedCode()));
                row.put("收获数量", item.getSeedNumber() == null ? "" : item.getSeedNumber().stripTrailingZeros().toPlainString() + (StringUtils.isEmpty(item.getUnit()) ? "" : item.getUnit()));
                row.put("收获方式", item.getHarvestTypeName());
                row.put("收获备注", item.getHarvestRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("收获明细", headers, rows));
            return sections;
        }

        if (dto.getTcHarvestExcelDTOList() != null && !dto.getTcHarvestExcelDTOList().isEmpty()) {
            List<String> headers = java.util.Arrays.asList("母本小区编号", "母本种子编号", "母本单株编号", "母本取样编号", "母本品种", "父本小区编号", "父本种子编号", "父本单株编号", "父本取样编号", "父本品种", "收获数量", "收获方式", "备注");
            List<Map<String, Object>> rows = new ArrayList<>();
            for (TcHarvestExcelDTO item : dto.getTcHarvestExcelDTOList()) {
                Map<String, Object> row = new LinkedHashMap<>();
                row.put("母本小区编号", item.getMotherRegionNum());
                row.put("母本种子编号", item.getMotherSeedNum());
                row.put("母本单株编号", item.getMotherSingleNumber());
                row.put("母本取样编号", item.getMotherSampleCode());
                row.put("母本品种", item.getMotherBreedName());
                row.put("父本小区编号", item.getFatherRegionNum());
                row.put("父本种子编号", item.getFatherSeedNum());
                row.put("父本单株编号", item.getFatherSingleNumber());
                row.put("父本取样编号", item.getFatherSampleCode());
                row.put("父本品种", item.getFatherBreedName());
                row.put("收获数量", item.getSeedNumber() + item.getUnit());
                row.put("收获方式", item.getHarvestTypeName());
                row.put("备注", item.getRemark());
                rows.add(row);
            }
            sections.add(buildTableSection("收获明细", headers, rows));
        }

        return sections;
    }
}
