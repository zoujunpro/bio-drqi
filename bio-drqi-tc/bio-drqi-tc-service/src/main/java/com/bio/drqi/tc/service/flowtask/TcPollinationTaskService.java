package com.bio.drqi.tc.service.flowtask;

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
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


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
    private TcSampleTestTbMapper tcSampleTestTbMapper;

    @Resource
    private TcExperimentTbMapper tcExperimentTbMapper;

    @Resource
    private BioDictMapper bioDictMapper;


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
            TcExperimentDesignTb father = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationTaskDTO.getExperimentNum(), tcPollinationExcelDTO.getFatherRegionNum(), tcPollinationExcelDTO.getFatherSeedNum());
            if (father == null) {
                throw new BusinessException("实验中无此区域为：" + tcPollinationExcelDTO.getFatherRegionNum() + "的种子编号:" + tcPollinationExcelDTO.getFatherSeedNum());
            }
            //校验3:父本大田取样编号校验
            if (StringUtils.isNotEmpty(tcPollinationExcelDTO.getFatherTcSampleCode())) {
                TcSampleTestTb tcSampleTestTb = tcSampleTestTbMapper.selectOneBySampleApplyNumAndTcSampleCode(tcPollinationTaskDTO.getSampleApplyNum(), tcPollinationExcelDTO.getFatherTcSampleCode());
                if (tcSampleTestTb == null) {
                    throw new BusinessException("大田取样编号编号为：" + tcPollinationExcelDTO.getFatherTcSampleCode() + "的父本查询不到取样信息");
                }
                if (!StringUtils.equals(tcSampleTestTb.getSeedNum(), tcPollinationExcelDTO.getFatherSeedNum())) {
                    throw new BusinessException("大田取样编号编号为：" + tcPollinationExcelDTO.getFatherTcSampleCode() + "的父本对应的种子编号错误，应为：" + tcSampleTestTb.getSeedNum());
                }
                if (!StringUtils.equals(tcSampleTestTb.getRegionNum(), tcPollinationExcelDTO.getFatherRegionNum())) {
                    throw new BusinessException("大田取样编号编号为：" + tcPollinationExcelDTO.getFatherTcSampleCode() + "的父本对应的小区编应号错误，为：" + tcSampleTestTb.getRegionNum());
                }
                if (!StringUtils.equals(tcSampleTestTb.getSampleCode(), tcPollinationExcelDTO.getFatherSampleCode())) {
                    throw new BusinessException("大田取样编号编号为：" + tcPollinationExcelDTO.getFatherTcSampleCode() + "的父本对应的取样编号错误，应为：" + tcSampleTestTb.getSampleCode());
                }
                if (!StringUtils.equals(tcSampleTestTb.getTcSampleCode(), tcPollinationExcelDTO.getFatherSingleNumber())) {
                    throw new BusinessException("大田取样编号编号为：" + tcPollinationExcelDTO.getFatherTcSampleCode() + "的父本对应的单株编号错误，应为：" + tcSampleTestTb.getTcSampleCode());
                }
            }else {
                //校验4：父本单株编号校验
                TcPollinationSingleNumTb tcPollinationSingleNumTb = tcPollinationSingleNumTbMapper.selectOneByExperimentNumAndTcSingleNumber(tcPollinationTaskDTO.getExperimentNum(), tcPollinationExcelDTO.getFatherSingleNumber());
                if (tcPollinationSingleNumTb == null) {
                    throw new BusinessException("单株编号为：" + tcPollinationExcelDTO.getFatherSingleNumber() + "的父本不存在");
                }
                if (!StringUtils.equals(tcPollinationSingleNumTb.getSeedNum(), tcPollinationExcelDTO.getFatherSeedNum())) {
                    throw new BusinessException("单株编号为：" + tcPollinationExcelDTO.getFatherSingleNumber() + "的父本对应的种子编号应为：" + tcPollinationSingleNumTb.getSeedNum());
                }
                if (!StringUtils.equals(tcPollinationSingleNumTb.getRegionNum(), tcPollinationExcelDTO.getFatherRegionNum())) {
                    throw new BusinessException("单株编号为：" + tcPollinationExcelDTO.getFatherSingleNumber() + "的父本对应的小区编应为：" + tcPollinationSingleNumTb.getRegionNum());
                }
            }
            //校验5:母本存在性校验
            TcExperimentDesignTb mother = tcExperimentDesignTbMapper.selectOneByExperimentNumAndRegionNumAndSeedNum(tcPollinationTaskDTO.getExperimentNum(), tcPollinationExcelDTO.getMotherRegionNum(), tcPollinationExcelDTO.getMotherSeedNum());
            if (mother == null) {
                throw new BusinessException("实验中无此区域为：" + tcPollinationExcelDTO.getMotherRegionNum() + "的种子编号:" + tcPollinationExcelDTO.getMotherSeedNum());
            }
            //校验6:母本大田编号校验
            if (StringUtils.isNotEmpty(tcPollinationExcelDTO.getMotherTcSampleCode())) {
                TcSampleTestTb tcSampleTestTb = tcSampleTestTbMapper.selectOneBySampleApplyNumAndTcSampleCode(tcPollinationTaskDTO.getSampleApplyNum(), tcPollinationExcelDTO.getMotherTcSampleCode());
                if (tcSampleTestTb == null) {
                    throw new BusinessException("大田取样编号编号为：" + tcPollinationExcelDTO.getMotherTcSampleCode() + "的母本查询不到取样信息");
                }
                if (!StringUtils.equals(tcSampleTestTb.getSeedNum(), tcPollinationExcelDTO.getMotherSeedNum())) {
                    throw new BusinessException("大田取样编号编号为：" + tcPollinationExcelDTO.getMotherTcSampleCode() + "的母本对应的种子编号错误，应为：" + tcSampleTestTb.getSeedNum());
                }
                if (!StringUtils.equals(tcSampleTestTb.getRegionNum(), tcPollinationExcelDTO.getMotherRegionNum())) {
                    throw new BusinessException("大田取样编号编号为：" + tcPollinationExcelDTO.getMotherTcSampleCode() + "的母本对应的小区编应号错误，为：" + tcSampleTestTb.getRegionNum());
                }
                if (!StringUtils.equals(tcSampleTestTb.getSampleCode(), tcPollinationExcelDTO.getMotherSampleCode())) {
                    throw new BusinessException("大田取样编号编号为：" + tcPollinationExcelDTO.getMotherTcSampleCode() + "的母本对应的取样编号错误，应为：" + tcSampleTestTb.getSampleCode());
                }
                if (!StringUtils.equals(tcSampleTestTb.getTcSampleCode(), tcPollinationExcelDTO.getMotherSingleNumber())) {
                    throw new BusinessException("大田取样编号编号为：" + tcPollinationExcelDTO.getMotherTcSampleCode() + "的母本对应的单株编号错误，应为：" + tcSampleTestTb.getTcSampleCode());
                }
            }else {
                //校验4：母本单株编号校验
                TcPollinationSingleNumTb tcPollinationSingleNumTb = tcPollinationSingleNumTbMapper.selectOneByExperimentNumAndTcSingleNumber(tcPollinationTaskDTO.getExperimentNum(), tcPollinationExcelDTO.getMotherSingleNumber());
                if (tcPollinationSingleNumTb == null) {
                    throw new BusinessException("单株编号为：" + tcPollinationExcelDTO.getFatherSingleNumber() + "的母本不存在");
                }
                if (!StringUtils.equals(tcPollinationSingleNumTb.getSeedNum(), tcPollinationExcelDTO.getMotherSeedNum())) {
                    throw new BusinessException("单株编号为：" + tcPollinationExcelDTO.getFatherSingleNumber() + "的母本对应的种子编号应为：" + tcPollinationSingleNumTb.getSeedNum());
                }
                if (!StringUtils.equals(tcPollinationSingleNumTb.getRegionNum(), tcPollinationExcelDTO.getMotherRegionNum())) {
                    throw new BusinessException("单株编号为：" + tcPollinationExcelDTO.getFatherSingleNumber() + "的母本对应的小区编应为：" + tcPollinationSingleNumTb.getRegionNum());
                }
            }
            //校验6: 收获方式校验
            BioDict harvestTypeDict = bioDictMapper.selectOneByDictTypeAndDictValueName(BioDictTypeEnum.HARVEST_TYPE.name(), tcPollinationExcelDTO.getHarvestTypeName());
            if (harvestTypeDict == null) {
                throw new BusinessException("收获方式填写错误：" + tcPollinationExcelDTO.getHarvestTypeName());
            }
            tcPollinationExcelDTO.setHarvestTypeCode(harvestTypeDict.getDictValueCode());

            //校验7:授粉中母本只能授粉一次
            TcPollinationTb tcPollinationTb = tcPollinationTbMapper.selectOneByExperimentNumAndMRegionNumAndMSeedNumAndMSampleCode(tcPollinationTaskDTO.getExperimentNum(), tcPollinationExcelDTO.getMotherRegionNum(), tcPollinationExcelDTO.getMotherSeedNum(), tcPollinationExcelDTO.getMotherSampleCode());
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
}
