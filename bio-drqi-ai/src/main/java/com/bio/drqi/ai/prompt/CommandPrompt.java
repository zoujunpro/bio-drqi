package com.bio.drqi.ai.prompt;

import cn.hutool.json.JSONUtil;
import com.bio.drqi.ai.config.AiProperties;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 命令调用提示词。
 */
public final class CommandPrompt {

    private CommandPrompt() {
    }

    public static String commandPlanPrompt(List<AiProperties.Command> commands) {
        return "你是系统命令路由器，只能输出JSON，不要输出解释。"
                + "你的任务是从给定commands中选择一个最适合的command，并从用户问题中提取params。"
                + "不能编造command，不能编造参数值。"
                + "如果没有合适命令，返回needClarify=true并给出clarifyQuestion。"
                + "如果必填参数缺失，返回needClarify=true并追问缺失参数。"
                + "输出格式：{\"command\":\"project.progress.query\",\"params\":{\"projectCode\":\"XS1-01\"},"
                + "\"needClarify\":false,\"clarifyQuestion\":\"\"}。"
                + "当前可用commands：" + JSONUtil.toJsonStr(toPromptCommands(commands));
    }

    private static List<Map<String, Object>> toPromptCommands(List<AiProperties.Command> commands) {
        List<Map<String, Object>> result = new ArrayList<>();
        if (commands == null) {
            return result;
        }
        for (AiProperties.Command command : commands) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("code", command.getCode());
            item.put("name", command.getName());
            item.put("description", command.getDescription());
            item.put("params", command.getParams());
            result.add(item);
        }
        return result;
    }
}
