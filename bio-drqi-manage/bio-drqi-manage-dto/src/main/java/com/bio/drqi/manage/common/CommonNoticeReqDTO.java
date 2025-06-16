package com.bio.drqi.manage.common;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
public class CommonNoticeReqDTO {

    @NotBlank(message = "缺失接收人")
    private String usernames;


    @NotBlank(message = "确失消息标题")
    private String title;

    @NotBlank(message = "确失消息内容")
    private String content;
}
