package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
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
import com.bio.drqi.manage.util.LetterUtil;
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
import java.util.stream.Collectors;

@Service
@Slf4j
public class VectorTaskServiceImpl implements VectorTaskService {

    @Resource
    private CerVectorTaskTbMapper cerVectorTaskTbMapper;

    @Resource
    private CerVectorTbMapper cerVectorTbMapper;

    @Resource
    private CerVectorGroupTbMapper cerVectorGroupTbMapper;

    @Resource
    private CerSubProjectTbMapper cerSubProjectTbMapper;

    @Resource
    private CerVectorStepLogMapper cerVectorStepLogMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private OssService ossService;

    @Resource
    private CerSpeciesConfMapper cerSpeciesConfMapper;

    @Resource
    private CerInstantVerifyTaskTbMapper cerInstantVerifyTaskTbMapper;


    @Override
    public PageInfo<VectorListPageRspDTO> ListPage(QueryPageVectorReqDTO queryPageVectorReqDTO) {
        PageHelper.startPage(queryPageVectorReqDTO.getPageNum(), queryPageVectorReqDTO.getPageSize());
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllByProjectIdOrderById(queryPageVectorReqDTO.getProjectId());
        PageInfo<CerVectorTaskTb> srcPage = new PageInfo<>(cerVectorTaskTbList);
        PageInfo<VectorListPageRspDTO> vectorBaseInfoRspDTOPageInfo = BeanUtils.copyPageInfoProperties(srcPage, VectorListPageRspDTO.class);
        vectorBaseInfoRspDTOPageInfo.getList().forEach(vectorListPageRspDTO -> {
            List<CerVectorTb> cerVectorTbList = cerVectorTbMapper.selectAllByVectorTaskId(vectorListPageRspDTO.getId());
            List<CerVectorGroupTb> cerVectorGroupTbList = cerVectorGroupTbMapper.selectAllByVectorTaskId(vectorListPageRspDTO.getId());
            vectorListPageRspDTO.setChildren(BeanUtils.copyToList(cerVectorTbList, VectorListPageRspDTO.Vector.class));
            vectorListPageRspDTO.setVectorGroupList(cerVectorGroupTbList.stream().map(cerVectorGroupTb -> new VectorListPageRspDTO.VectorGroup(cerVectorGroupTb.getGroupName(), cerVectorGroupTb.getPlasmidNames(), cerVectorGroupTb.getRemark(), cerVectorGroupTb.getRepeatNum())).collect(Collectors.toList()));
        });
        return vectorBaseInfoRspDTOPageInfo;
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listBySubProject(Integer subProjectId) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllBySubProjectId(subProjectId);
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listAll() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listAllByQualityInspectionResult(QualityInspectionResultEnum.pass.name());
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listApproveAll(String speciesCode) {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.selectAllByTaskStatusAndSpeciesCode(VectorTaskStatusEnum.TASK_STATUS_2.status,speciesCode);
        return BeanUtils.copyListProperties(cerVectorTaskTbList, CerImplementationPlanBaseInfoRspDTO.class);
    }

    @Override
    public List<CerImplementationPlanBaseInfoRspDTO> listForTransForm() {
        List<CerVectorTaskTb> cerVectorTaskTbList = cerVectorTaskTbMapper.listForTransForm();
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
            if (cerVectorTaskTb!=null&&cerVectorTaskTb.getVectorTaskCode().matches("^[0-9a-zA-Z]{1,8}\\-[0-9]{2}[a-z]$")) {
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
    public VectorTaskAddDTO detail(Integer id) {
        CerVectorTaskTb cerVectorTaskTb = cerVectorTaskTbMapper.selectById(id);
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerVectorTaskTb.getTaskNum());
        VectorTaskAddDTO vectorTaskAddDTO = JSONUtil.toBean(bioTaskDtlTb.getTaskForm(), VectorTaskAddDTO.class);
        if (!VectorTaskTypeEnum.type_1.code.equals(cerVectorTaskTb.getVectorTaskType())) {
            vectorTaskAddDTO.setExcelVectorList(parseExcelVector(vectorTaskAddDTO.getVectorExcelUrl()));
        }
        vectorTaskAddDTO.setTaskStatus(cerVectorTaskTb.getTaskStatus());
        vectorTaskAddDTO.setCreateUserId(cerVectorTaskTb.getCreateUserId());
        vectorTaskAddDTO.setVectorTaskId(cerVectorTaskTb.getId());
        return vectorTaskAddDTO;
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

    private List<VectorTaskAddDTO.ExcelVector> parseExcelVector(String excelUrl) {
        if (StringUtils.isEmpty(excelUrl)) {
            return new ArrayList<>();
        }
        if (!excelUrl.endsWith("xlsx")) {
            throw new BusinessException("文件格式错误");
        }
        String tempFilePath = System.getProperty("java.io.tmpdir") + File.separator + excelUrl;
        try {
            ossService.downloadPath(tempFilePath, excelUrl);
        } catch (Exception e) {
            log.error("【任务工单】文件从oss下载失败", e);
            throw new BusinessException("文件处理异常");
        }
        List<VectorTaskAddDTO.ExcelVector> excelVectorList = ExcelUtil.readExcel(tempFilePath, VectorTaskAddDTO.ExcelVector.class);
        return excelVectorList;
    }


    private String getVectorTaskCode(String subProjectNum, Integer currentNum) {
        return subProjectNum + "-" + StringUtils.padl(String.valueOf(currentNum + 1), 2, '0');
    }


}
