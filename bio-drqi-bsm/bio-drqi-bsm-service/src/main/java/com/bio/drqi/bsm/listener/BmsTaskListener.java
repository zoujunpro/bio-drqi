package com.bio.drqi.bsm.listener;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DatePattern;
import cn.hutool.core.date.DateUtil;
import com.bio.base.api.RemoteUserService;
import com.bio.base.user.req.QueryUserByIdListReqDTO;
import com.bio.base.user.rsp.UserBaseInfoRspDTO;
import com.bio.common.core.dto.BusinessException;
import com.bio.common.core.dto.ResponseResult;
import com.bio.common.core.util.StringUtils;
import com.bio.drqi.domain.BioTaskDtlTb;
import com.bio.drqi.feishu.FeiShuService;
import com.bio.drqi.feishu.MessageTypeEnum;
import com.bio.drqi.feishu.dto.Message;
import com.bio.drqi.feishu.dto.NoticeUserDTO;
import com.bio.drqi.mapper.BioTaskDtlTbMapper;
import com.bio.flow.enums.EventType;
import com.bio.flow.hander.DefaultDuplicateCopyHandler;
import com.bio.flow.service.FlowTaskListener;
import com.easyflow.engine.FlowEngineService;
import com.easyflow.engine.core.FlowActor;
import com.easyflow.engine.entity.FlowTaskActorTb;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BmsTaskListener extends DefaultDuplicateCopyHandler implements FlowTaskListener<BioTaskDtlTb> {

    private final static Map<String, String> vieMap = new ConcurrentHashMap<>();

    @Value("${cer.properties.feiShuBmsJumpUrl}")
    private String feiShuBmsJumpUrl;

    @Resource
    private BioTaskDtlTbMapper bioTaskDtlTbMapper;

    @Resource
    private FlowEngineService flowEngineService;

    @Resource
    private RemoteUserService remoteUserService;

    @Resource
    private FeiShuService feiShuService;


    static {
        vieMap.put("bms_purchase_apply", "purchasingManage/purApply");
        vieMap.put("bms_product_input", "InventoryManage/conWarehousing");
        vieMap.put("bms_product_out", "InventoryManage/conOutboundApply");
    }

    @Override
    public void notice(EventType eventType, Supplier<BioTaskDtlTb> supplier) {
        BioTaskDtlTb bioTaskDtlTb = supplier.get();
        if (vieMap.get(bioTaskDtlTb.getTaskTypeCode()) == null) {
            return;
        }
        if (EventType.complete == eventType) {
            String title = "你的" + bioTaskDtlTb.getTaskTypeName() + "已通过";
            sendMessage(bioTaskDtlTb, title, Arrays.asList(bioTaskDtlTb.getApplyUserId()));
        } else if (EventType.revoke == eventType) {
            String title = "你的" + bioTaskDtlTb.getTaskTypeName() + "已撤销";
            sendMessage(bioTaskDtlTb, title, Arrays.asList(bioTaskDtlTb.getApplyUserId()));
        } else if (EventType.reject == eventType) {
            String title = "你的" + bioTaskDtlTb.getTaskTypeName() + "已拒绝";
            sendMessage(bioTaskDtlTb, title, Arrays.asList(bioTaskDtlTb.getApplyUserId()));
        } else if (EventType.active == eventType) {
            List<FlowTaskActorTb> flowTaskActorTbList = flowEngineService.getQueryService().getActiveTaskActorByInstanceId(bioTaskDtlTb.getInstanceId());
            String title = "你有一个" + bioTaskDtlTb.getTaskTypeName() + "待审批";
            sendMessage(bioTaskDtlTb, title, flowTaskActorTbList.stream().map(flowTaskActorTb -> Integer.valueOf(flowTaskActorTb.getActorId())).collect(Collectors.toList()));
        }
    }

    private void sendMessage(BioTaskDtlTb bioTaskDtlTb, String title, List<Integer> userIdList) {
        QueryUserByIdListReqDTO queryUserByIdListReqDTO = new QueryUserByIdListReqDTO();
        queryUserByIdListReqDTO.setUserIdList(userIdList);
        ResponseResult<List<UserBaseInfoRspDTO>> responseResult = remoteUserService.queryUserByIdList(queryUserByIdListReqDTO);
        if (responseResult.isError()) {
            throw new BusinessException(responseResult.getMessage());
        }
        List<UserBaseInfoRspDTO> rspDTOList = responseResult.getData();
        if (CollectionUtil.isEmpty(rspDTOList)) {
            return;
        }
        List<NoticeUserDTO> noticeUserDTOList = rspDTOList.stream().filter(userBaseInfoRspDTO -> StringUtils.isNotEmpty(userBaseInfoRspDTO.getFeiShuUserId())).map(userBaseInfoRspDTO->new NoticeUserDTO(userBaseInfoRspDTO.getNickName(),userBaseInfoRspDTO.getFeiShuUserId())).collect(Collectors.toList());
        String content = "**任务描述：**" + bioTaskDtlTb.getTaskDesc() + "\n" + "**申  请 人：**" + bioTaskDtlTb.getApplyUserName() + "\n" + "**申请时间：**" + DateUtil.format(bioTaskDtlTb.getCreateTime(), DatePattern.NORM_DATETIME_PATTERN);
        Message message = new Message();
        message.setTitle(title);
        message.setContent(content);
        message.setUrl(String.format(feiShuBmsJumpUrl, vieMap.get(bioTaskDtlTb.getTaskTypeCode()), bioTaskDtlTb.getId()));
        feiShuService.sendCardMessage(noticeUserDTOList, message,MessageTypeEnum.drqi);
    }

    @Override
    public void doHandle(List<FlowActor> flowActorList, Long instanceId,String businessId) {
        BioTaskDtlTb bioTaskDtlTb = bioTaskDtlTbMapper.selectOneByTaskNum(businessId);
        if (Objects.nonNull(bioTaskDtlTb)) {
            String title = bioTaskDtlTb.getTaskTypeName() + "抄送通知";
            sendMessage(bioTaskDtlTb, title, flowActorList.stream().map(flowActor -> Integer.valueOf(flowActor.getCreateId())).collect(Collectors.toList()));
        }
    }
}
