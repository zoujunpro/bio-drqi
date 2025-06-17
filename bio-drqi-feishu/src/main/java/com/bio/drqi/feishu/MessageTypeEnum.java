package com.bio.drqi.feishu;

import com.bio.common.core.dto.BusinessException;

public enum MessageTypeEnum {
    alarm("interactive", "alarm"),
    drqi("interactive", "drqi");

    private String messageType;

    private String messageCategory;

    MessageTypeEnum(String messageType, String messageCategory) {
        this.messageType = messageType;
        this.messageCategory = messageCategory;
    }

    public static String getMessageType(String messageCategory) {
        for (MessageTypeEnum messageTypeEnum : MessageTypeEnum.values()) {
            if (messageTypeEnum.messageCategory.equals(messageCategory)) {
                return messageTypeEnum.messageCategory;
            }
        }
        throw new BusinessException("飞书消息类型配置错误");
    }

    public String getMessageType() {
        return messageType;
    }

    public String getMessageCategory() {
        return messageCategory;
    }
}
