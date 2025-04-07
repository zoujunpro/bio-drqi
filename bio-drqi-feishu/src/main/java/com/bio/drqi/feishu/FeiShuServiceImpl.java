package com.bio.drqi.feishu;


import com.bio.drqi.feishu.dto.Message;
import com.bio.drqi.feishu.properties.FeiShuProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class FeiShuServiceImpl extends FeiShuService {

    @Resource
    private FeiShuProperties feiShuProperties;


    @Override
    public void sendCardMessage(List<String> openIdList, Message message) {
        for (String openId : openIdList) {
            new Thread(() -> {
                try {
                    sendCardMessage(feiShuProperties.getAppId(), feiShuProperties.getSecret(), openId, message);
                    log.info("飞书卡片消息发送成功：openId={},message={}", openId, message);
                } catch (Exception e) {
                    log.error("飞书卡片消息发送失败：openId={}", openId, e);
                }
            }).start();

        }
    }
}
