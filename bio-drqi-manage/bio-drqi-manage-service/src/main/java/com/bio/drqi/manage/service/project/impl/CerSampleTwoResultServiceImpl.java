package com.bio.drqi.manage.service.project.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.BeanUtils;
import com.bio.drqi.common.enums.BioTaskStatusEnum;
import com.bio.drqi.domain.*;
import com.bio.drqi.external.client.BioInfoClientApi;
import com.bio.drqi.manage.sample.req.CerSampleTwoResultListPageReqDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListDetailRspDTO;
import com.bio.drqi.manage.sample.rsp.CerSampleTwoResultListPageRspDTO;
import com.bio.drqi.manage.service.common.SynSampleTestResultService;
import com.bio.drqi.manage.service.project.CerSampleTwoResultService;
import com.bio.drqi.mapper.BioSampleTestTwoResultDetailTbMapper;
import com.bio.drqi.mapper.BioSampleTestTwoResultTbMapper;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.drqi.mapper.CerSampleTestTbMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CerSampleTwoResultServiceImpl implements CerSampleTwoResultService {


    @Resource
    private BioInfoClientApi bioInfoClientApi;

    @Resource
    private BioSampleTestTwoResultTbMapper bioSampleTestTwoResultTbMapper;

    @Resource
    private BioSampleTestTwoResultDetailTbMapper bioSampleTestTwoResultDetailTbMapper;

    @Resource
    private SynSampleTestResultService synSampleTestResultService;

    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;


    @Override
    public PageInfo<CerSampleTwoResultListPageRspDTO> listPage(CerSampleTwoResultListPageReqDTO cerSampleTwoResultListPageReqDTO) {
        PageHelper.startPage(cerSampleTwoResultListPageReqDTO.getPageNum(), cerSampleTwoResultListPageReqDTO.getPageSize());
        List<BioSampleTestTwoResultTb> bioSampleTestTwoResultTbList = bioSampleTestTwoResultTbMapper.selectSelective(BeanUtils.copyProperties(cerSampleTwoResultListPageReqDTO, BioSampleTestTwoResultTb.class));
        PageInfo<BioSampleTestTwoResultTb> srcPageInfo = new PageInfo<>(bioSampleTestTwoResultTbList);
        return BeanUtils.copyPageInfoProperties(srcPageInfo, CerSampleTwoResultListPageRspDTO.class);
    }

    @Override
    public List<CerSampleTwoResultListDetailRspDTO> listDetail(Integer id) {
        BioSampleTestTwoResultTb bioSampleTestTwoResultTb = bioSampleTestTwoResultTbMapper.selectById(id);
        List<BioSampleTestTwoResultDetailTb> bioSampleTestTwoResultDetailTbList = bioSampleTestTwoResultDetailTbMapper.selectAllByApplyNoAndSampleCode(bioSampleTestTwoResultTb.getApplyNo(), bioSampleTestTwoResultTb.getSampleCode());
        return BeanUtils.copyListProperties(bioSampleTestTwoResultDetailTbList, CerSampleTwoResultListDetailRspDTO.class);
    }

    @Override
    public Object detail(Integer detailId) {
        BioSampleTestTwoResultDetailTb bioSampleTestTwoResultDetailTb = bioSampleTestTwoResultDetailTbMapper.selectById(detailId);
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("sampleID", bioSampleTestTwoResultDetailTb.getSampleId());
        paramMap.put("QBuniqCode", bioSampleTestTwoResultDetailTb.getUniqueDbCode());
        paramMap.put("HapID", bioSampleTestTwoResultDetailTb.getHapId());
        Object o = bioInfoClientApi.sampleTestBioInfoResultDetail(paramMap);
        return o;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synOne(Integer id) {
        BioSampleTestTwoResultTb bioSampleTestTwoResultTb = bioSampleTestTwoResultTbMapper.selectById(id);
        if (Objects.isNull(bioSampleTestTwoResultTb)) {
            throw new BusinessException("excel没匹配到该生信检测数据");
        }
        List<BioSampleTestTwoResultDetailTb> bioSampleTestTwoResultDetailTbList = synSampleTestResultService.synBioResult(Arrays.asList(bioSampleTestTwoResultTb));
        if (CollectionUtil.isNotEmpty(bioSampleTestTwoResultDetailTbList)) {
            for (BioSampleTestTwoResultDetailTb bioSampleTestTwoResultDetailTb : bioSampleTestTwoResultDetailTbList) {
                bioSampleTestTwoResultDetailTbMapper.deleteByApplyNoAndSampleCodeAndUniqueDbCode(bioSampleTestTwoResultDetailTb.getApplyNo(), bioSampleTestTwoResultDetailTb.getSampleCode(), bioSampleTestTwoResultDetailTb.getUniqueDbCode());
                bioSampleTestTwoResultDetailTbMapper.insert(bioSampleTestTwoResultDetailTb);
            }
        }
        //更新结果状态
        bioSampleTestTwoResultTbMapper.updateById(bioSampleTestTwoResultTb);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNgsResult(String uniqueDbCode) {
        List<BioSampleTestTwoResultDetailTb> bioSampleTestTwoResultDetailTbList = bioSampleTestTwoResultDetailTbMapper.selectAllByUniqueDbCode(uniqueDbCode);
        if (CollectionUtil.isNotEmpty(bioSampleTestTwoResultDetailTbList)) {
            //删除所有
            bioSampleTestTwoResultDetailTbMapper.deleteByIdIn(bioSampleTestTwoResultDetailTbList.stream().map(BioSampleTestTwoResultDetailTb::getId).collect(Collectors.toList()));
            Map<String, List<BioSampleTestTwoResultDetailTb>> bioSampleSampleTwoResultDetailTbListMap = bioSampleTestTwoResultDetailTbList.stream().collect(Collectors.groupingBy(bioSampleSampleTwoResultDetailTb -> bioSampleSampleTwoResultDetailTb.getApplyNo() + "|" + bioSampleSampleTwoResultDetailTb.getSampleCode()));
            bioSampleSampleTwoResultDetailTbListMap.forEach((applyAndSampleCode, bioSampleSampleTwoResultDetailTbs) -> {
                String applyNo = applyAndSampleCode.split("\\|")[0];
                String sampleCode = applyAndSampleCode.split("\\|")[1];
                BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(applyNo);
                if (bioTaskDtlTb == null) {
                    throw new BusinessException("齐博士数据异常，请联系相关人员，未找到该检测所对应的齐博士申请工单：" + applyNo);
                }
                if (BioTaskStatusEnum.TASK_STATUS_2.status.equals(bioTaskDtlTb.getTaskNum())) {
                    throw new BusinessException("齐博士业务流程不允许删除，该NGS结果所对应的取样编号：" + sampleCode + "所在的申请工单：" + applyNo + "流程已经完成，无法删除");
                }
                if (CollectionUtil.isEmpty(bioSampleTestTwoResultDetailTbMapper.selectAllByApplyNoAndSampleCode(applyNo, sampleCode))) {
                    CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByApplyNoAndSampleCode(applyNo, sampleCode);
                    if (cerSampleTestTb == null) {
                        throw new BusinessException("齐博士数据异常，请联系相关人员，错误原因，找不到申请工单下" + applyNo + "的取样编号" + sampleCode);
                    }
                    cerSampleTestTbMapper.updateTestUserIdAndTestUserNameById(null, null,cerSampleTestTb.getId());
                }

            });
        }

    }

}
