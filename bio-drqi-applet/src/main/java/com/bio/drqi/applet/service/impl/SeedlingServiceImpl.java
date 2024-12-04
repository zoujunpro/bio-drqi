package com.bio.drqi.applet.service.impl;

import cn.hutool.json.JSONUtil;
import com.bio.common.core.context.SecurityContextHolder;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.applet.dto.req.SeedlingRemainReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingRemoveReqDTO;
import com.bio.drqi.applet.dto.req.SeedlingReportReqDTO;
import com.bio.drqi.applet.enums.CheckResultOperateEnum;
import com.bio.drqi.applet.service.SeedlingService;
import com.bio.drqi.domain.CerPlantDtlTb;
import com.bio.drqi.domain.CerPlantReportLog;
import com.bio.drqi.domain.CerSampleTestOperateLog;
import com.bio.drqi.domain.CerSampleTestTb;
import com.bio.drqi.enums.PlantStatusEnum;
import com.bio.drqi.mapper.CerPlantDtlTbMapper;
import com.bio.drqi.mapper.CerPlantReportLogMapper;
import com.bio.drqi.mapper.CerSampleTestOperateLogMapper;
import com.bio.drqi.mapper.CerSampleTestTbMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;


@Service
@Slf4j
public class SeedlingServiceImpl implements SeedlingService {


    @Resource
    private CerSampleTestTbMapper cerSampleTestTbMapper;

    @Resource
    private CerPlantDtlTbMapper cerPlantDtlTbMapper;

    @Resource
    private CerSampleTestOperateLogMapper cerSampleTestOperateLogMapper;

    @Resource
    private CerPlantReportLogMapper cerPlantReportLogMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void remain(SeedlingRemainReqDTO seedlingRemainReqDTO) {
        CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByVectorTaskCodeAndSampleCodeFirst(seedlingRemainReqDTO.getVectorTaskCode(), seedlingRemainReqDTO.getSampleCode());
        if (cerSampleTestTb == null) {
            throw new BusinessException("找不到此取样信息");
        }
        CerSampleTestOperateLog cerSampleTestOperateLog = cerSampleTestOperateLogMapper.selectOneByUniqueCode(cerSampleTestTb.getProjectCode() + cerSampleTestTb.getSampleCode());
        if (cerSampleTestOperateLog != null) {
            throw new BusinessException("已经做过保苗/剔苗操作");
        }

        if ("传代".equals(cerSampleTestTb.getCheckResult()) || "留种".equals(cerSampleTestTb.getCheckResult())) {
            for (int i = 0; i < seedlingRemainReqDTO.getNumber(); i++) {
                CerPlantDtlTb cerPlantDtlTb = CerPlantDtlTb.of(cerSampleTestTb);
                cerPlantDtlTb.setPlantCode(cerSampleTestTb.getSampleCode() + "-" + StringUtils.padl(String.valueOf(i + 1), 2, '0'));
                cerPlantDtlTbMapper.insert(cerPlantDtlTb);
            }
            cerSampleTestOperateLog = new CerSampleTestOperateLog();
            cerSampleTestOperateLog.setProjectCode(cerSampleTestTb.getProjectCode());
            cerSampleTestOperateLog.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
            cerSampleTestOperateLog.setProjectId(cerSampleTestTb.getProjectId());
            cerSampleTestOperateLog.setVectorTaskId(cerSampleTestTb.getVectorTaskId());
            cerSampleTestOperateLog.setSampleCode(cerSampleTestTb.getSampleCode());
            cerSampleTestOperateLog.setOperateCode(CheckResultOperateEnum.remain.name());
            cerSampleTestOperateLog.setRemark(null);
            cerSampleTestOperateLog.setPictureUrls(JSONUtil.toJsonStr(seedlingRemainReqDTO.getPictureUrls()));
            cerSampleTestOperateLog.setCreateTime(new Date());
            cerSampleTestOperateLog.setUniqueCode(cerSampleTestTb.getProjectCode() + cerSampleTestTb.getSampleCode());
            cerSampleTestOperateLog.setCreateUserId(SecurityContextHolder.getUserId());
            cerSampleTestOperateLog.setCreateUserName(SecurityContextHolder.getNickName());
            cerSampleTestOperateLogMapper.insert(cerSampleTestOperateLog);
        } else {
            throw new BusinessException("只有取样检测最新结果是传代或者留种时可以进行保苗操作");
        }
    }

    @Override
    public void remove(SeedlingRemoveReqDTO seedlingRemoveReqDTO) {
        CerSampleTestTb cerSampleTestTb = cerSampleTestTbMapper.selectOneByVectorTaskCodeAndSampleCodeFirst(seedlingRemoveReqDTO.getVectorTaskCode(), seedlingRemoveReqDTO.getSampleCode());
        if (cerSampleTestTb != null) {
            throw new BusinessException("找不到此取样信息");
        }
        CerSampleTestOperateLog cerSampleTestOperateLog = cerSampleTestOperateLogMapper.selectOneByUniqueCode(cerSampleTestTb.getProjectCode() + cerSampleTestTb.getSampleCode());
        if (cerSampleTestOperateLog != null) {
            throw new BusinessException("已经做过保苗/剔苗操作");
        }
        if ("舍弃".equals(cerSampleTestTb.getCheckResult())) {
            cerSampleTestOperateLog = new CerSampleTestOperateLog();
            cerSampleTestOperateLog.setProjectCode(cerSampleTestTb.getProjectCode());
            cerSampleTestOperateLog.setVectorTaskCode(cerSampleTestTb.getVectorTaskCode());
            cerSampleTestOperateLog.setProjectId(cerSampleTestTb.getProjectId());
            cerSampleTestOperateLog.setVectorTaskId(cerSampleTestTb.getVectorTaskId());
            cerSampleTestOperateLog.setSampleCode(cerSampleTestTb.getSampleCode());
            cerSampleTestOperateLog.setOperateCode(CheckResultOperateEnum.remove.name());
            cerSampleTestOperateLog.setRemark(seedlingRemoveReqDTO.getRemark());
            cerSampleTestOperateLog.setPictureUrls(JSONUtil.toJsonStr(seedlingRemoveReqDTO.getPictureUrls()));
            cerSampleTestOperateLog.setCreateTime(new Date());
            cerSampleTestOperateLog.setUniqueCode(cerSampleTestTb.getProjectCode() + cerSampleTestTb.getSampleCode());
            cerSampleTestOperateLog.setCreateUserId(SecurityContextHolder.getUserId());
            cerSampleTestOperateLog.setCreateUserName(SecurityContextHolder.getNickName());
            cerSampleTestOperateLogMapper.insert(cerSampleTestOperateLog);
        } else {
            throw new BusinessException("只有取样检测最新结果是舍弃时可以进行剔苗操作");
        }
    }

    @Override
    public void report(SeedlingReportReqDTO seedlingReportReqDTO) {
        CerPlantDtlTb cerPlantDtlTb = cerPlantDtlTbMapper.selectOneByPlantCode(seedlingReportReqDTO.getPlantCode());
        if(PlantStatusEnum.STATUS_3.code.equals(cerPlantDtlTb.getPlantStatus())){
            throw new BusinessException("苗已剔除");
        }
        CerPlantReportLog cerPlantReportLog=new CerPlantReportLog();
        cerPlantReportLog.setPlantCode(seedlingReportReqDTO.getPlantCode());
        cerPlantReportLog.setRemark(seedlingReportReqDTO.getRemark());
        cerPlantReportLog.setCreateTime(new Date());
        cerPlantReportLog.setCreateUserId(SecurityContextHolder.getUserId());
        cerPlantReportLog.setCreateUserName(SecurityContextHolder.getNickName());
        cerPlantReportLog.setPictureUrls(JSONUtil.toJsonStr(seedlingReportReqDTO.getPictureUrls()));
        cerPlantReportLog.setPlantAttribute(JSONUtil.toJsonStr(seedlingReportReqDTO.getAttributes()));
        cerPlantReportLogMapper.insert(cerPlantReportLog);

    }
}
