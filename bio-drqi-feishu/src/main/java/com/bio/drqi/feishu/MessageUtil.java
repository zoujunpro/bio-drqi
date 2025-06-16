package com.bio.drqi.feishu;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.bio.common.core.dto.BusinessException;
import com.bio.drqi.feishu.dto.Message;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MessageUtil {


    public static String replaceContent(Message message, MessageTypeEnum messageTypeEnum) {
        if (MessageTypeEnum.alarm.getMessageCategory().equals(messageTypeEnum.getMessageCategory())) {
            return replaceAlarmContent(message);
        } else if (MessageTypeEnum.drqi.getMessageCategory().equals(messageTypeEnum.getMessageCategory())) {
            return replaceDRqiContent(message);
        }else {
            throw new BusinessException("消息类型找不到");
        }
    }

    private static String replaceDRqiContent(Message message) {
        try {
            Resource classPathResource = new ClassPathResource("json/drqi-message.json");
            InputStream inputStream = classPathResource.getInputStream();
            String s = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            JSONObject json = JSONUtil.parseObj(s);
            json.putByPath("elements.0.content", message.getContent());
            json.putByPath("header.title.content", message.getTitle());
            json.putByPath("elements.1.actions.0.multi_url.url", message.getUrl());
            return json.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String replaceAlarmContent(Message message) {
        try {
            Resource classPathResource = new ClassPathResource("json/alarm-message.json");
            InputStream inputStream = classPathResource.getInputStream();
            String s = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            JSONObject json = JSONUtil.parseObj(s);
            json.putByPath("header.title.content", message.getTitle());
            json.putByPath("body.elements.1.text.content", message.getContent());
            json.putByPath("body.elements.2.text.content", message.getTime());
            return json.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
