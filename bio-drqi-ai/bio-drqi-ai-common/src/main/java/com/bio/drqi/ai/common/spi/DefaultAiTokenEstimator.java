package com.bio.drqi.ai.common.spi;

/**
 * 默认 token 估算器。
 * 当前没有绑定具体模型 tokenizer，先使用中英文混合文本的保守近似算法。
 */
public class DefaultAiTokenEstimator implements AiTokenEstimator {

    @Override
    public int estimate(String text) {
        if (!hasText(text)) {
            return 0;
        }
        int tokenCount = 0;
        int asciiWordLength = 0;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (isAsciiLetterOrDigit(ch)) {
                asciiWordLength++;
                continue;
            }
            if (asciiWordLength > 0) {
                tokenCount++;
                asciiWordLength = 0;
            }
            if (Character.isWhitespace(ch)) {
                continue;
            }
            tokenCount++;
        }
        if (asciiWordLength > 0) {
            tokenCount++;
        }
        return Math.max(1, tokenCount);
    }

    private boolean isAsciiLetterOrDigit(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || (ch >= '0' && ch <= '9');
    }

    private boolean hasText(String value) {
        return value != null && value.trim().length() > 0;
    }
}
