package com.bio.drqi.ai.util;

import cn.hutool.core.util.StrUtil;
import com.bio.common.core.dto.BusinessException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class AiJsonExtractor {

    private static final Pattern JSON_CODE_BLOCK = Pattern.compile("(?is)```(?:json)?\\s*(\\{.*?})\\s*```");
    private static final Pattern THINK_BLOCK = Pattern.compile("(?is)<think>.*?</think>");

    private AiJsonExtractor() {
    }

    public static String extractObject(String content, String emptyMessage, String invalidMessage) {
        if (StrUtil.isBlank(content)) {
            throw new BusinessException(emptyMessage);
        }
        String text = normalize(content);
        Matcher matcher = JSON_CODE_BLOCK.matcher(text);
        if (matcher.find()) {
            String blockJson = firstBalancedObject(matcher.group(1));
            if (StrUtil.isNotBlank(blockJson)) {
                return blockJson;
            }
        }
        String json = firstBalancedObject(text);
        if (StrUtil.isBlank(json)) {
            throw new BusinessException(invalidMessage);
        }
        return json;
    }

    private static String normalize(String content) {
        String text = content.trim();
        text = THINK_BLOCK.matcher(text).replaceAll("");
        text = text.replace("\uFEFF", "").trim();
        return text;
    }

    private static String firstBalancedObject(String text) {
        int start = text.indexOf('{');
        if (start < 0) {
            return null;
        }
        int depth = 0;
        boolean inString = false;
        boolean escape = false;
        for (int i = start; i < text.length(); i++) {
            char ch = text.charAt(i);
            if (escape) {
                escape = false;
                continue;
            }
            if (ch == '\\' && inString) {
                escape = true;
                continue;
            }
            if (ch == '"') {
                inString = !inString;
                continue;
            }
            if (inString) {
                continue;
            }
            if (ch == '{') {
                depth++;
            } else if (ch == '}') {
                depth--;
                if (depth == 0) {
                    return text.substring(start, i + 1);
                }
            }
        }
        return null;
    }
}
