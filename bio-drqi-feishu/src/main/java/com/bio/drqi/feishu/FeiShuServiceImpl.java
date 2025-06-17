package com.bio.drqi.feishu;


import com.bio.drqi.feishu.dto.Message;
import com.bio.drqi.feishu.dto.NoticeUserDTO;
import com.bio.drqi.feishu.properties.FeiShuProperties;
import com.bio.drqi.mapper.BioNoticeLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
@Slf4j
public class FeiShuServiceImpl extends FeiShuService {

    @Resource
    private BioNoticeLogMapper bioNoticeLogMapper;

    @Resource
    private FeiShuProperties feiShuProperties;

    @Override
    public void sendCardMessage(List<NoticeUserDTO> noticeUserDTOList, Message message, MessageTypeEnum messageTypeEnum) {
        for (NoticeUserDTO noticeUserDTO : noticeUserDTOList) {
            new Thread(() -> {
                try {
                    sendCardMessage(feiShuProperties.getAppId(), feiShuProperties.getSecret(), noticeUserDTO.getOpenId(), message,messageTypeEnum);
                    log.info("飞书卡片消息发送成功：openId={},message={}", noticeUserDTO.getOpenId(), message);
                } catch (Exception e) {
                    log.error("飞书卡片消息发送失败：openId={}", noticeUserDTO.getOpenId(), e);
                }
            }).start();

        }
    }
}
