package com.bio.drqi.ai.prompt;

/**
 * 普通对话提示词。
 */
public final class SearchPrompt {

    private SearchPrompt() {
    }

    public static String generalAnswerPrompt() {
        return "你是本系统的AI助手。用户问题如果不是系统数据查询，也要正常回答。"
                + "回答要友好、明白、简洁，使用中文。"
                + "不要输出JSON，不要输出查询计划，不要编造本系统数据库里不存在的数据。"
                + "如果用户的问题需要查询系统实时数据才能确定，请说明需要指定查询范围或让用户换成数据查询问题。"
                + "如果问题是普通知识、解释、操作建议或闲聊，直接给出自然语言回答。";
    }
}
