package com.bio.drqi.feishu;


import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.feishu.dto.Message;
import com.lark.oapi.Client;
import com.lark.oapi.service.im.v1.model.CreateMessageReq;
import com.lark.oapi.service.im.v1.model.CreateMessageReqBody;
import com.lark.oapi.service.im.v1.model.CreateMessageResp;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Slf4j
public abstract class FeiShuService {

    public abstract void sendCardMessage(List<String> openIdList, Message message,MessageTypeEnum messageTypeEnum);


    protected void sendCardMessage(String appId, String secret, String openId, Message message,MessageTypeEnum messageTypeEnum) throws Exception {
        Client client = Client.newBuilder(appId, secret).build();
        String msgType=messageTypeEnum.getMessageType();
        // 创建请求对象
        String mes = MessageUtil.replaceContent(message,messageTypeEnum);
        CreateMessageReq req = CreateMessageReq.newBuilder()
                .receiveIdType("open_id")
                .createMessageReqBody(CreateMessageReqBody.newBuilder()
                        .receiveId(openId)
                        .msgType(msgType)
                        .content(mes)
                        .uuid(UUID.randomUUID().toString())
                        .build())
                .build();
        log.info("飞书通知发送开始，通知人：{}：通知内容：{}", openId, mes);
        CreateMessageResp resp = client.im().message().create(req);
        log.info("飞书通知发送结果：" + resp.getMsg());
        if (!resp.success()) {
            throw new BusinessException(resp.getMsg());
        }
    }

}
