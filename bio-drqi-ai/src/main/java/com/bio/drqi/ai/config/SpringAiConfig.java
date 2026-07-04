package com.bio.drqi.ai.config;

import org.springframework.context.annotation.Configuration;

/**
 * Spring AI 扩展配置。
 * 当前项目先使用 OpenAI 兼容 HTTP 客户端直接调用千问/本地模型；
 * 如果后续引入 spring-ai 依赖，可以在这里集中声明 ChatModel。
 */
@Configuration
public class SpringAiConfig {
}
