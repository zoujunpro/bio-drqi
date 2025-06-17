package com.bio.drqi.feishu.dto;

import lombok.Data;

@Data
public class NoticeUserDTO {
    private String username;
    private String openId;

    public NoticeUserDTO(String username, String openId) {
        this.username = username;
        this.openId = openId;
    }

    public NoticeUserDTO() {
    }
}
