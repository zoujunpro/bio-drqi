package com.bio.drqi.manage.listener;

import cn.hutool.core.collection.CollectionUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.req.QueryUserByIdListReqDTO;
import com.bio.base.user.rsp.UserBaseInfoRspDTO;

import com.bio.drqi.enums.ProjectStatusEnum;

import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.domain.CerProjectTb;
import com.bio.drqi.feishu.FeiShuService;
import com.bio.drqi.feishu.MessageTypeEnum;
import com.bio.drqi.feishu.dto.Message;
import com.bio.drqi.feishu.dto.NoticeUserDTO;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.drqi.mapper.CerProjectTbMapper;
import com.easyflow.engine.FlowEngineService;
import com.easyflow.engine.entity.FlowHisCommitTb;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
public class CerProjectStatusListener {

    private final static Map<String, String> vieMap = new ConcurrentHashMap<>();

    @Value("${cer.properties.feiShuProjectJumpUrl}")
    private String feiShuProjectJumpUrl;

    @Resource
    private CerProjectTbMapper cerProjectTbMapper;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private FeiShuService feiShuService;

    @Resource
    private FlowEngineService flowEngineService;


    public void notice(ProjectStatusEnum projectStatusEnum, Supplier<Integer> supplier) {
        Integer projectId = supplier.get();
        CerProjectTb cerProjectTb = cerProjectTbMapper.selectById(projectId);
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(cerProjectTb.getTaskNum());
        List<FlowHisCommitTb> flowHisCommitTbList = flowEngineService.getQueryService().getFlowCommitTbByInstanceId(bioTaskDtlTb.getInstanceId());
        List<Integer> userIdList = flowHisCommitTbList.stream().filter(flowHisCommitTb -> isNumber(flowHisCommitTb.getCreateId())).map(flowHisCommitTb -> Integer.valueOf(flowHisCommitTb.getCreateId())).collect(Collectors.toList());
        if (projectStatusEnum.name() == ProjectStatusEnum.compete.name()) {
            String title = "项目完成通知";
            sendMessage(cerProjectTb, title, userIdList);
        } else if (projectStatusEnum.name() == ProjectStatusEnum.stop.name()) {
            String title = "项目暂停通知";
            sendMessage(cerProjectTb, title, userIdList);
        } else if (projectStatusEnum.name() == ProjectStatusEnum.execute.name()) {
            String title = "项目启动通知";
            sendMessage(cerProjectTb, title, userIdList);
        }
    }

    private void sendMessage(CerProjectTb cerProjectTb, String title, List<Integer> userIdList) {
        QueryUserByIdListReqDTO queryUserByIdListReqDTO = new QueryUserByIdListReqDTO();
        queryUserByIdListReqDTO.setUserIdList(userIdList);
        ResponseResult<List<UserBaseInfoRspDTO>> responseResult = remoteUserService.queryUserByIdList(queryUserByIdListReqDTO);
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        List<UserBaseInfoRspDTO> rspDTOList = responseResult.getData();
        if(CollectionUtil.isEmpty(rspDTOList)){
            return;
        }
        List<NoticeUserDTO> noticeUserDTOList = rspDTOList.stream().filter(userBaseInfoRspDTO -> StringUtils.isNotEmpty(userBaseInfoRspDTO.getFeiShuUserId())).map(userBaseInfoRspDTO->new NoticeUserDTO(userBaseInfoRspDTO.getUsername(),userBaseInfoRspDTO.getFeiShuUserId())).collect(Collectors.toList());
        String content = "**项目名称：**" + cerProjectTb.getProjectName() + "\n" + "**项目编号：**" + cerProjectTb.getProjectCode();
        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setUrl("http://172.16.14.2:18888/#/poc/productDetail/" + cerProjectTb.getId());
        feiShuService.sendCardMessage(noticeUserDTOList, message,MessageTypeEnum.drqi);
    }

    private static boolean isNumber(String str) {
        try {
            Integer.valueOf(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}
