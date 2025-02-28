package com.bio.drqi.feishu;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import org.apache.commons.io.IOUtils;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class MessageUtil {


    public static String replaceContent(String content, String title, String url) {
        try {
            Resource classPathResource = new ClassPathResource("json/message.json");
            InputStream inputStream = classPathResource.getInputStream();
            String s = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            JSONObject json = JSONUtil.parseObj(s);
            json.putByPath("elements.0.content", content);
            json.putByPath("header.title.content", title);
            json.putByPath("elements.1.actions.0.multi_url.url", url);
            return json.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
