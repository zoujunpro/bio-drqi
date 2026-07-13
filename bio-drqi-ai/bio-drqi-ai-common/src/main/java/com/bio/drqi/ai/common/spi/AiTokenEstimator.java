package com.bio.drqi.ai.common.spi;

/**
 * Token 估算器。
 * 不同 embedding 模型的 tokenizer 规则不完全一致，分块工具通过这个接口预留切换点。
 */
public interface AiTokenEstimator {

    /**
     * 估算文本 token 数。
     *
     * @param text 待估算文本
     * @return token 数
     */
    int estimate(String text);
}
