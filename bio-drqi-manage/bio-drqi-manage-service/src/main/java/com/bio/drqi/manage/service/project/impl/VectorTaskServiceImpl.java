package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.contents.CerProjectContents;
import com.bio.drqi.enums.*;
import com.bio.drqi.manage.vector.req.GetVectorTaskNumReqDTO;
import com.bio.drqi.manage.vector.req.QueryPageVectorReqDTO;
import com.bio.drqi.manage.vector.rsp.CerImplementationPlanBaseInfoRspDTO;
import com.bio.drqi.manage.vector.rsp.StepListRspDTO;
import com.bio.drqi.manage.vector.rsp.VectorListPageRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.common.core.util.ExcelUtil;
import com.bio.common.core.util.StringUtils;
import com.bio.common.oss.service.OssService;
import com.bio.drqi.domain.*;
import com.bio.drqi.manage.dto.project.VectorTaskAddDTO;
import com.bio.drqi.manage.service.project.VectorTaskService;
import com.bio.drqi.common.util.LetterUtil;
import com.bio.drqi.mapper.*;
import com.bio.drqi.manage.vector.rsp.VectorTaskSpeciesRspDTO;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class VectorTaskServiceImpl implements VectorTaskService {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;


    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerVectorStepLogMapper cerVectorStepLogMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerBreedDictMapper cerBreedDictMapper;

    @Resource
    private CerInstantVerifyTaskTbMapper cerInstantVerifyTaskTbMapper;

    @Resource
    private CerSampleCodePrefixTbMapper cerSampleCodePrefixTbMapper;


    @Override
    public PageInfo<VectorListPageRspDTO> ListPage(QueryPageVectorReqDTO queryPageVectorReqDTO) {
        PageHelper.startPage(queryPageVectorReqDTO.getPageNum(), queryPageVectorReqDTO.getPageSize());
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectSelective(BeanUtils.copyProperties(queryPageVectorReqDTO, CerVectorTaskTb.class));
        PageInfo<CerVectorTaskTb> srcPage = new PageInfo<>(cerVectorTaskTbList);
        PageInfo<VectorListPageRspDTO> vectorBaseInfoRspDTOPageInfo = BeanUtils.copyPageInfoProperties(srcPage, VectorListPageRspDTO.class);
        if (CollectionUtil.isNotEmpty(vectorBaseInfoRspDTOPageInfo.getList())) {
            Map<String, String> breedMap = cerBreedDictMapper.selectAll().stream().collect(Collectors.toMap(CerBreedDict::getBreedCode, CerBreedDict::getBreedName));
            Map<String, String> speciesMap = cerSpeciesConfMapper.selectAll().stream().collect(Collectors.toMap(CerSpeciesConf::getSpeciesCode, CerSpeciesConf::getSpeciesName));
            vectorBaseInfoRspDTOPageInfo.getList().forEach(vectorListPageRspDTO -> {
                vectorListPageRspDTO.setBreedName(breedMap.get(vectorListPageRspDTO.getBreedCode()));
                vectorListPageRspDTO.setSpeciesName(speciesMap.get(vectorListPageRspDTO.getSpeciesCode()));
            });
        }
        return vectorBaseInfoRspDTOPageInfo;
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listAllBySubProject(Integer subProjectId) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllBySubProjectId(subProjectId);
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listForVectorBuild(Integer subProjectId) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listForVectorBuild(subProjectId);
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }


    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listForTransForm(Integer subProjectId) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listForTransForm(subProjectId);
        List<CerImplementationPlanBaseInfoRspDTO> result = BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
        result.forEach(cerImplementationPlanBaseInfoRspDTO -> {
            if(StringUtils.isEmpty(cerImplementationPlanBaseInfoRspDTO.getAcceptorMaterial())){
              CerBreedDict cerBreedDict=  cerBreedDictMapper.selectOneByBreedCode(cerImplementationPlanBaseInfoRspDTO.getBreedCode());
              if(cerBreedDict!=null){
                  cerImplementationPlanBaseInfoRspDTO.setAcceptorMaterial(cerBreedDict.getBreedName());
              }
            }
        });
        return result;
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listForMoveSeed() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listForMoveSeed();
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listForFirstSample(String speciesCode) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listForFirstSample(speciesCode);
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listForPlasmid(Integer subProjectId) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listForPlasmid(subProjectId);
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }


    @Override
    public String getTaskNum(GetVectorTaskNumReqDTO getVectorTaskNumReqDTO) {
        CerSubProjectTb cerSubProjectTb = cerSubProjectTbMapper.selectById(getVectorTaskNumReqDTO.getSubProjectId());
        if (cerSubProjectTb == null) {
            throw new BusinessException("子项目不存在");
        }
        //当前子项目下所有实施方案
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllBySubProjectId(cerSubProjectTb.getId());
        //复制工单
        if (StringUtils.isNotEmpty(getVectorTaskNumReqDTO.getVectorTaskCode())) {
            CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(getVectorTaskNumReqDTO.getVectorTaskCode());
            if (cerVectorTaskTb != null && cerVectorTaskTb.getVectorTaskCode().matches("^[0-9a-zA-Z]{1,8}\\-[0-9]{2}[a-z]$")) {
                cerVectorTaskTbList = cerVectorTaskTbList.stream().filter(vectorTask -> vectorTask.getVectorTaskCode().matches("^[0-9a-zA-Z]{1,8}\\-[0-9]{2}[a-z]$")).filter(vectorTaskTb -> vectorTaskTb.getVectorTaskCode().contains(cerVectorTaskTb.getVectorTaskCode().substring(0, cerVectorTaskTb.getVectorTaskCode().length() - 1))).collect(Collectors.toList());
                List<String> lastLetter = cerVectorTaskTbList.stream().map(vectorTask -> vectorTask.getVectorTaskCode().substring(vectorTask.getVectorTaskCode().length() - 1)).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
                return cerVectorTaskTb.getVectorTaskCode().substring(0, cerVectorTaskTb.getVectorTaskCode().length() - 1) + LetterUtil.nextLetter(lastLetter.get(0));
            } else {
                Integer maxNumber = findMaxIndex(cerVectorTaskTbList);
                return getVectorTaskCode(cerSubProjectTb.getSubProjectCode(), maxNumber);
            }
        } else {
            //新建立工单 判断是否有字母
            Integer maxNumber = findMaxIndex(cerVectorTaskTbList);
            if (CerProjectContents.Y.equals(getVectorTaskNumReqDTO.getIfLetter())) {
                return getVectorTaskCode(cerSubProjectTb.getSubProjectCode(), maxNumber) + "a";
            } else {
                return getVectorTaskCode(cerSubProjectTb.getSubProjectCode(), maxNumber);
            }
        }
    }


    private Integer findMaxIndex(List<CerVectorTaskTb> cerVectorTaskTbList) {
        if (CollectionUtil.isEmpty(cerVectorTaskTbList)) {
            return 0;
        }
        List<String> numberList = new ArrayList<>();
        List<CerVectorTaskTb> haveLetterCerVectorTaskTbList = cerVectorTaskTbList.stream().filter(vectorTask -> vectorTask.getVectorTaskCode().matches("^[0-9a-zA-Z]{1,8}\\-[0-9]{2}[a-z]$")).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(haveLetterCerVectorTaskTbList)) {
            List<String> numberList1 = haveLetterCerVectorTaskTbList.stream().map(vectorTask -> vectorTask.getVectorTaskCode().substring(vectorTask.getVectorTaskCode().length() - 3, vectorTask.getVectorTaskCode().length() - 1)).collect(Collectors.toList());
            numberList.addAll(numberList1);
        }
        List<CerVectorTaskTb> noLetterCerVectorTaskTbList = cerVectorTaskTbList.stream().filter(vectorTask -> vectorTask.getVectorTaskCode().matches("^[0-9a-zA-Z]{1,8}\\-[0-9]{2}$")).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(noLetterCerVectorTaskTbList)) {
            List<String> numberList2 = noLetterCerVectorTaskTbList.stream().map(vectorTask -> vectorTask.getVectorTaskCode().substring(vectorTask.getVectorTaskCode().length() - 2)).collect(Collectors.toList());
            numberList.addAll(numberList2);
        }
        if (CollectionUtil.isEmpty(numberList)) {
            return 0;
        } else {
            numberList = numberList.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
            return Integer.valueOf(numberList.get(0));
        }
    }

    @Override
    public List<StepListRspDTO> stepList(Integer id) {
        List<StepListRspDTO> result = new ArrayList<>();
        List<CerVectorStepLog> cerVectorStepLogList = cerVectorStepLogMapper.selectAllByVectorTaskIdOrderById(id);
        List<String> stepCodeList = cerVectorStepLogList.stream().map(CerVectorStepLog::getStepCode).collect(Collectors.toList());
        for (ImplementationPlanTypeEnum implementationPlanTypeEnum : ImplementationPlanTypeEnum.values()) {
            StepListRspDTO stepListRspDTO = new StepListRspDTO();
            stepListRspDTO.setStepCode(implementationPlanTypeEnum.name());
            stepListRspDTO.setStepName(implementationPlanTypeEnum.desc);
            stepListRspDTO.setShowFlag(CollectionUtil.isNotEmpty(stepCodeList) && stepCodeList.contains(implementationPlanTypeEnum.name()) ? CerProjectContents.Y : CerProjectContents.N);
            result.add(stepListRspDTO);
        }
        return result;
    }

    @Override
    public List<StepListRspDTO> stepListByCode(String vectorTaskCode) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        if (cerVectorTaskTb == null) {
            return new ArrayList<>();
        }
        return stepList(cerVectorTaskTb.getId());
    }

    @Override
    public CerImplementationPlanBaseInfoRspDTO detail(Integer id) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(id);

        CerSampleCodePrefixTb cerSampleCodePrefixTb = cerSampleCodePrefixTbMapper.selectOneByVectorTaskCode(cerVectorTaskTb.getVectorTaskCode());
        CerImplementationPlanBaseInfoRspDTO cerImplementationPlanBaseInfoRspDTO = BeanUtils.copyProperties(cerVectorTaskTb, CerImplementationPlanBaseInfoRspDTO.class);
        cerImplementationPlanBaseInfoRspDTO.setSampleCodePrefix(cerSampleCodePrefixTb.getSampleCodePrefix());
        return cerImplementationPlanBaseInfoRspDTO;
    }

    @Override
    public CerImplementationPlanBaseInfoRspDTO detailByCode(String vectorTaskCode) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectOneByVectorTaskCode(vectorTaskCode);
        return BeanUtils.copyProperties(cerVectorTaskTb, CerImplementationPlanBaseInfoRspDTO.class);

    }

    @Override
    public void stop(Integer id) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(id);
        if (!VectorTaskStatusEnum.TASK_STATUS_2.status.equals(cerVectorTaskTb.getTaskStatus())) {
            throw new BusinessException("只有执行中实施方案可以暂停");
        }
        cerVectorTaskTb.setTaskStatus(VectorTaskStatusEnum.TASK_STATUS_4.status);
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
    }

    @Override
    public void start(Integer id) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(id);
        if (!VectorTaskStatusEnum.TASK_STATUS_4.status.equals(cerVectorTaskTb.getTaskStatus())) {
            throw new BusinessException("只有暂停实施方案可以再次启动");
        }
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(cerVectorTaskTb.getProjectId());
        if (!ProjectStatusEnum.execute.name().equals(cerProjectTb.getProjectStatus())) {
            throw new BusinessException("该项目不是执行中");
        }
        cerVectorTaskTb.setTaskStatus(VectorTaskStatusEnum.TASK_STATUS_2.status);
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
    }

    @Override
    public void complete(Integer id) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(id);
        cerVectorTaskTb.setTaskStatus(VectorTaskStatusEnum.TASK_STATUS_5.status);
        cerVectorTaskTbMapper.updateById(cerVectorTaskTb);
    }

    @Override
    public String getInstantVerifyTaskCode(String vectorTaskCode) {
        List<CerInstantVerifyTaskTb> cerInstantVerifyTaskTbList = cerInstantVerifyTaskTbMapper.selectAllByVectorTaskCode(vectorTaskCode);
        if (CollectionUtil.isEmpty(cerInstantVerifyTaskTbList)) {
            return vectorTaskCode + "-A";
        } else if (cerInstantVerifyTaskTbList.size() == 1) {
            String[] strArr = cerInstantVerifyTaskTbList.get(0).getInstantVerifyCode().split("-");
            String lastCode = strArr[strArr.length - 1];
            return vectorTaskCode + "-" + LetterUtil.nextLetterForInstantVerify(lastCode);
        } else {
            List<String> lastCodeList = cerInstantVerifyTaskTbList.stream().sorted(Comparator.comparing(CerInstantVerifyTaskTb::getId).reversed()).map(cerInstantVerifyTaskTb -> cerInstantVerifyTaskTb.getInstantVerifyCode().split("-")[cerInstantVerifyTaskTb.getInstantVerifyCode().split("-").length - 1]).collect(Collectors.toList());
            return vectorTaskCode + "-" + LetterUtil.nextLetterForInstantVerify(lastCodeList.get(0));
        }
    }

    @Override
    public List<VectorTaskSpeciesRspDTO> findAllSpecies() {
        List<VectorTaskSpeciesRspDTO> result = new ArrayList<>();
        List<String> allSpeciesCodeList = cerVectorTaskTbMapper.selectAllSpeciesCode();
        if (CollectionUtil.isNotEmpty(allSpeciesCodeList)) {
            List<CerSpeciesConf> cerSpeciesConfList = cerSpeciesConfMapper.selectAllBySpeciesCodeIn(allSpeciesCodeList);
            cerSpeciesConfList.forEach(cerSpeciesConf -> {
                VectorTaskSpeciesRspDTO vectorTaskSpeciesRspDTO = new VectorTaskSpeciesRspDTO();
                vectorTaskSpeciesRspDTO.setSpeciesCode(cerSpeciesConf.getSpeciesCode());
                vectorTaskSpeciesRspDTO.setSpeciesName(cerSpeciesConf.getSpeciesName());
                result.add(vectorTaskSpeciesRspDTO);
            });
        }
        return result;
    }


    private String getVectorTaskCode(String subProjectNum, Integer currentNum) {
        return subProjectNum + "-" + StringUtils.padl(String.valueOf(currentNum + 1), 2, '0');
    }


}
